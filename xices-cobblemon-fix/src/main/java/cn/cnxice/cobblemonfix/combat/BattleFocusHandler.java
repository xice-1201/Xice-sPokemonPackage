package cn.cnxice.cobblemonfix.combat;

import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.HashSet;
import java.util.Set;

public final class BattleFocusHandler {
    public static void onStarted(BattleStartedEvent.Post event) { apply(event.getBattle()); }
    // The periodic refresh deliberately replaces immediate removal, leaving a short
    // grace period after a battle ends for UI/recall animations to finish.
    public static void onVictory(BattleVictoryEvent event) { }
    public static void onFled(BattleFledEvent event) { }

    private static int tickCounter;
    public static void onServerTick(ServerTickEvent.Post event) {
        if (++tickCounter < 200) return;
        tickCounter = 0;
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        Set<java.util.UUID> refreshed = new HashSet<>();
        for (var player : server.getPlayerList().getPlayers()) {
            PokemonBattle battle = com.cobblemon.mod.common.battles.BattleRegistry.getBattleByParticipatingPlayer(player);
            if (battle == null || battle.getEnded() || !refreshed.add(battle.getBattleId())) continue;
            apply(battle);
        }
    }

    private static void apply(PokemonBattle battle) {
        for (var player : battle.getPlayers()) AttentionTransfer(battle, player);
        for (ActiveBattlePokemon active : battle.getActivePokemon()) {
            if (active.hasPokemon() && active.getBattlePokemon().getEntity() != null)
                AttentionTransfer(battle, active.getBattlePokemon().getEntity());
        }
    }

    private static void clear(PokemonBattle battle) {
        for (var player : battle.getPlayers()) player.removeEffect(CombatEffectsRegistry.BATTLE_FOCUS);
        for (ActiveBattlePokemon active : battle.getActivePokemon()) {
            if (active.hasPokemon() && active.getBattlePokemon().getEntity() != null)
                active.getBattlePokemon().getEntity().removeEffect(CombatEffectsRegistry.BATTLE_FOCUS);
        }
    }

    private static void AttentionTransfer(PokemonBattle ignored, LivingEntity entity) {
        entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                CombatEffectsRegistry.BATTLE_FOCUS, 220, 0, false, true));
    }
    private BattleFocusHandler() { }
}
