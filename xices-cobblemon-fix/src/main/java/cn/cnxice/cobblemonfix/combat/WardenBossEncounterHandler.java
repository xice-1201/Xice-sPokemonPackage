package cn.cnxice.cobblemonfix.combat;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.MoveActionResponse;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.api.moves.categories.DamageCategories;
import com.cobblemon.mod.common.battles.MoveTarget;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import cn.cnxice.cobblemonfix.XicesCobblemonFix;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Server-side bridge between a vanilla Warden and its hidden Cobblemon battle proxy. */
public final class WardenBossEncounterHandler {
    private static final org.slf4j.Logger DEBUG_LOG = org.slf4j.LoggerFactory.getLogger("Xice Warden");
    static void debug(String message) { if (cn.cnxice.cobblemonfix.BattleAttributeConfig.WARDEN_BATTLE_DEBUG_LOG.get()) DEBUG_LOG.info("[Warden debug] {}", message); }
    private static final Map<UUID, UUID> BATTLES = new HashMap<>();
    private static final Map<UUID, UUID> PROXIES = new HashMap<>();
    private static final Map<UUID, UUID> PROXY_ACTORS = new HashMap<>();
    private static int scanTicks;

    /** Allows the normal Cobblemon battle key/interact flow to be emulated for a vanilla Warden. */
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(event.getTarget() instanceof Warden warden)
                || player.isSpectator() || !warden.isAlive()
                || BattleRegistry.getBattleByParticipatingPlayer(player) != null) return;
        if (warden.getPersistentData().getBoolean("xice_warden_in_battle")) return;
        event.setCanceled(true);
        debug("主动触发 battle: warden="+warden.getUUID()+" player="+player.getGameProfile().getName());
        start(warden, player, (ServerLevel) player.level());
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (++scanTicks < 10) return;
        scanTicks = 0;
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        for (ServerLevel level : server.getAllLevels()) {
            // Defensive cleanup: battle callbacks can be skipped when the
            // client disconnects or the battle is force-closed.
            for (var entry : new HashMap<>(PROXIES).entrySet()) {
                if (BattleRegistry.getBattle(entry.getKey()) == null) {
                    if (level.getEntity(entry.getValue()) != null) level.getEntity(entry.getValue()).discard();
                    BossBattleRegistry.unregisterBattle(entry.getKey());
                    PROXIES.remove(entry.getKey());
                    PROXY_ACTORS.remove(entry.getKey());
                    BATTLES.remove(entry.getKey());
                    WardenBattleAiPolicy.clearBattle(entry.getKey());
                }
            }
            for (Warden warden : level.getEntitiesOfClass(Warden.class,
                    new AABB(-30_000_000, -2048, -30_000_000, 30_000_000, 2048, 30_000_000),
                    Warden::isAlive)) {
                if (warden.getPersistentData().getBoolean("xice_warden_in_battle")) {
                    UUID challenger = warden.getPersistentData().getUUID("xice_warden_challenger");
                    ServerPlayer current = server.getPlayerList().getPlayer(challenger);
                    if (current != null) {
                        var activeBattle = BattleRegistry.getBattleByParticipatingPlayer(current);
                        if (activeBattle != null && !BATTLES.containsKey(activeBattle.getBattleId())) {
                            debug("Untracked battle for challenger; not adopting an unrelated battle: " + activeBattle.getBattleId());
                        }
                    }
                    // Battle creation can be deferred by Cobblemon; once the challenger is no longer
                    // participating, safely restore the vanilla Warden and remove the hidden proxy.
                    long started = warden.getPersistentData().getLong("xice_warden_battle_start");
                    if ((current == null || BattleRegistry.getBattleByParticipatingPlayer(current) == null)
                            && (started == 0L || level.getGameTime() - started > 100L)) {
                        cleanup(level, warden);
                    }
                    continue;
                }
                LivingEntity target = warden.getTarget();
                if (!(target instanceof ServerPlayer player) || player.isSpectator() ||
                        warden.distanceToSqr(player) > 25.0 || BattleRegistry.getBattleByParticipatingPlayer(player) != null) continue;
                start(warden, player, level);
            }
        }
        // The installed BattleAI handles requests synchronously; do not race it with tick polling.
    }

    private static void driveAi() {
        for (UUID battleId : new HashMap<>(BATTLES).keySet()) {
            var battle = BattleRegistry.getBattle(battleId);
            if (battle == null || battle.getEnded()) continue;
            // Battle actors are keyed by the proxy Pokémon UUID, not the vanilla Warden UUID.
            UUID wardenActorId = BATTLES.get(battleId);
            var actor = wardenActorId == null ? null : battle.getActor(wardenActorId);
            if (actor == null || !actor.getMustChoose() || actor.getRequest() == null
                    || !actor.getResponses().isEmpty() || actor.getActivePokemon().isEmpty()) continue;
            var active = actor.getActivePokemon().get(0);
            var target = active.getOppositeOpponent();
            if (target == null) continue;
            int targetAccuracyStage = target instanceof ActiveBattlePokemon opponent && opponent.getBattlePokemon() != null
                    ? opponent.getBattlePokemon().getStatChanges().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.ACCURACY, 0)
                    : 0;
            boolean darkSurgeEligible = WardenBattleAiPolicy.darkSurgeEligible(active);
            boolean entryLock = WardenBattleAiPolicy.opponentJustEntered(battle, active, false);
            WardenBattleAiPolicy.Action action = WardenBattleAiPolicy.chooseNormalAction(net.minecraft.util.RandomSource.create(),
                    darkSurgeEligible);
            String move = switch (action) {
                case DARK_SURGE -> "darksurge";
                case WARDEN_PUNCH -> "wardenpunch";
                case SONIC_WAVE -> "sonicwave";
                case SONIC_LOCK -> "soniclock";
            };
            if (entryLock) move = "soniclock";
            debug("AI action battle="+battleId+" turn="+battle.getTurn()+" move="+move+" target="+target.getPNX()
                    +" targetAccuracyStage="+targetAccuracyStage+" darkSurgeEligible="+darkSurgeEligible+" entryLock="+entryLock);
            actor.forceChoose(new MoveActionResponse(move, "darksurge".equals(move) ? null : target.getPNX(), ""));
        }
    }

    private static void start(Warden warden, ServerPlayer player, ServerLevel level) {
        // A final Cobblemon/Mega Showdown ability reload can replace the early
        // registration. Queue a verified re-registration immediately before
        // constructing the battle so the native callbacks are present.
        XicesCobblemonFix.queueWardenAbilityRegistration();
        debug("Queued dark_shroud registration before BattleBuilder");
        var species = PokemonSpecies.getByIdentifier(ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "warden_boss"));
        if (species == null) return;
        Pokemon pokemon = new Pokemon();
        pokemon.setSpecies(species);
        pokemon.setLevel(100);
        pokemon.setNature(Natures.getRandomNature());
        debug("创建代理 pokemon="+pokemon.getUuid()+" at="+warden.blockPosition());
        XicesCobblemonFix.registerWardenMoves();
        // A freshly constructed Pokemon has no move set until it is initialized;
        // without this step the battle engine falls back to Struggle.
        pokemon.initialize();
        var darkShroud = com.cobblemon.mod.common.api.abilities.Abilities.get("xice_dark_shroud");
        if (darkShroud != null) pokemon.setAbility$common(darkShroud.create(false, com.cobblemon.mod.common.api.Priority.NORMAL));
        debug("代理能力=" + (pokemon.getAbility() == null ? "null" : pokemon.getAbility().getTemplate().getName()));
        pokemon.initializeMoveset(true);
        // The species learnset is parsed before the runtime extension registry;
        // bind the four templates directly to this boss instance as a fallback.
        pokemon.getMoveSet().clear();
        MoveTemplate[] wardenMoves = {
                com.cobblemon.mod.common.api.moves.Moves.getByName("wardenpunch"),
                com.cobblemon.mod.common.api.moves.Moves.getByName("sonicwave"),
                com.cobblemon.mod.common.api.moves.Moves.getByName("darksurge"),
                com.cobblemon.mod.common.api.moves.Moves.getByName("soniclock")
        };
        // Move's third constructor argument is raised PP stages (0-3), not max PP.
        // Passing the template PP here corrupts the battle request/moveset codec.
        for (MoveTemplate template : wardenMoves) if (template != null) pokemon.getMoveSet().add(new Move(template, template.getPp(), 0));
        // Do not validate against the datapack learnset here: custom boss
        // templates are runtime-only and validation would remove them.
        PokemonEntity proxy = pokemon.sendOut(level, warden.position(), null, ignored -> kotlin.Unit.INSTANCE);
        if (proxy == null) return;
        // sendOut creates the battle-facing Pokemon snapshot; bind the ability
        // once more immediately before BattleBuilder serializes that snapshot.
        debug("战斗快照能力=" + (pokemon.getAbility() == null ? "null" : pokemon.getAbility().getName()));
        proxy.setCustomName(net.minecraft.network.chat.Component.literal("监守者"));
        proxy.setCustomNameVisible(false);
        proxy.setInvisible(true);
        proxy.setInvulnerable(true);
        warden.getPersistentData().putBoolean("xice_warden_in_battle", true);
        warden.getPersistentData().putUUID("xice_warden_challenger", player.getUUID());
        warden.getPersistentData().putLong("xice_warden_battle_start", level.getGameTime());
        warden.setTarget(null);
        warden.setNoAi(true);
        warden.setInvulnerable(true);
        warden.setInvisible(true);
        // Explicit singles format with capture and flee disabled; the proxy is the
        // only opposing party member, while the player's normal party remains 6v6.
        var result = BattleBuilder.INSTANCE.pve(player, proxy, warden.getUUID(),
                BattleFormat.Companion.getGEN_9_SINGLES(), false, false, 1_000_000.0F);
        // Battle creation may complete on the next server tick; register through the
        // success callback instead of assuming the registry is populated immediately.
        result.ifSuccessful(battle -> {
            debug("BattleBuilder success battle="+battle.getBattleId()+" warden="+warden.getUUID());
            BATTLES.put(battle.getBattleId(), warden.getUUID());
            PROXIES.put(battle.getBattleId(), proxy.getUUID());
            BossBattleRegistry.register(battle.getBattleId(), pokemon.getUuid());
            for (var actor : battle.getActors()) {
                debug("Actor battle=" + battle.getBattleId() + " uuid=" + actor.getUuid() + " type=" + actor.getType());
                if (actor instanceof com.cobblemon.mod.common.battles.actor.PokemonBattleActor wild
                        && wild.getEntity() == proxy) {
                    PROXY_ACTORS.put(battle.getBattleId(), actor.getUuid());
                    ((cn.cnxice.cobblemonfix.mixin.WardenBattleAiAccessor) wild).xice$setBattleAI(new WardenEncounterAi());
                    debug("Installed Warden AI actor=" + actor.getUuid() + " pokemon=" + pokemon.getUuid());
                }
            }
            return kotlin.Unit.INSTANCE;
        });
        debug("BattleBuilder request submitted for warden="+warden.getUUID());
    }

    public static void onVictory(BattleVictoryEvent event) {
        UUID battleId = event.getBattle().getBattleId();
        UUID wardenId = BATTLES.remove(battleId);
        BossBattleRegistry.unregisterBattle(battleId);
        WardenBattleAiPolicy.clearBattle(battleId);
        if (wardenId == null) return;
        UUID proxyActorId = PROXY_ACTORS.remove(battleId);
        boolean playerWon = event.getWinners().stream()
                .anyMatch(actor -> actor.getType() == com.cobblemon.mod.common.api.battles.model.actor.ActorType.PLAYER);
        boolean proxyLost = proxyActorId != null && event.getLosers().stream()
                .anyMatch(actor -> actor.getUuid().equals(proxyActorId));
        debug("Victory battle=" + battleId + " proxyActor=" + proxyActorId + " playerWon=" + playerWon
                + " proxyLost=" + proxyLost + " winners=" + event.getWinners().stream().map(a -> a.getUuid()).toList()
                + " losers=" + event.getLosers().stream().map(a -> a.getUuid()).toList());
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        UUID victoryProxyId = PROXIES.remove(battleId);
        for (ServerLevel level : server.getAllLevels()) {
            for (Warden warden : level.getEntitiesOfClass(Warden.class,
                    new AABB(-30_000_000, -2048, -30_000_000, 30_000_000, 2048, 30_000_000),
                    w -> w.getUUID().equals(wardenId))) {
                if (!playerWon || !proxyLost) {
                    cleanup(level, warden);
                    continue;
                }
                warden.setInvulnerable(false);
                warden.setNoAi(false);
                warden.setInvisible(false);
                // Extra reward for completing the custom encounter; vanilla Warden
                // death still supplies its normal sculk catalyst/drop behavior.
                level.addFreshEntity(new ItemEntity(level, warden.getX(), warden.getY() + 0.5D,
                        warden.getZ(), new ItemStack(Items.NETHERITE_INGOT)));
                warden.kill();
                debug("Victory reward applied warden=" + wardenId + " dead=" + warden.isDeadOrDying());
            }
            if (victoryProxyId != null && level.getEntity(victoryProxyId) != null) level.getEntity(victoryProxyId).discard();
        }
    }

    public static void onFled(BattleFledEvent event) {
        UUID battleId = event.getBattle().getBattleId();
        debug("Fled battle=" + battleId + "; no reward");
        UUID wardenId = BATTLES.remove(battleId);
        BossBattleRegistry.unregisterBattle(battleId);
        WardenBattleAiPolicy.clearBattle(battleId);
        UUID proxyId = PROXIES.remove(battleId);
        PROXY_ACTORS.remove(battleId);
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        for (ServerLevel level : server.getAllLevels()) {
            if (proxyId != null && level.getEntity(proxyId) != null) level.getEntity(proxyId).discard();
            if (wardenId != null && level.getEntity(wardenId) instanceof Warden warden) cleanup(level, warden);
        }
    }

    private static void cleanup(ServerLevel level, Warden warden) {
        UUID challenger = warden.getPersistentData().hasUUID("xice_warden_challenger")
                ? warden.getPersistentData().getUUID("xice_warden_challenger") : null;
        if (challenger != null) {
            // Remove any proxy associated with this encounter (if battle setup failed or was fled).
            for (var entry : new HashMap<>(BATTLES).entrySet()) {
                if (entry.getValue().equals(warden.getUUID())) {
                    BossBattleRegistry.unregisterBattle(entry.getKey());
                    UUID proxy = PROXIES.remove(entry.getKey());
                    PROXY_ACTORS.remove(entry.getKey());
                    BATTLES.remove(entry.getKey());
                    if (proxy != null && level.getEntity(proxy) != null) level.getEntity(proxy).discard();
                }
            }
        }
        warden.getPersistentData().remove("xice_warden_in_battle");
        warden.getPersistentData().remove("xice_warden_challenger");
        warden.getPersistentData().remove("xice_warden_battle_start");
        warden.setNoAi(false);
        warden.setInvulnerable(false);
        warden.setInvisible(false);
    }
    private WardenBossEncounterHandler() { }
}
