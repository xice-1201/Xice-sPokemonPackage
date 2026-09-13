package cn.cnxice.cobblemonfix;

import cn.cnxice.cobblemonfix.spawn.NincadaEvolutionHandler;
import cn.cnxice.cobblemonfix.spawn.SpawnCompanionHandler;
import cn.cnxice.cobblemonfix.spawn.XiceSpawnConditionAppendage;
import cn.cnxice.cobblemonfix.spawn.VolbeatIllumiseDropHandler;
import cn.cnxice.cobblemonfix.combat.CombatEffectsRegistry;
import cn.cnxice.cobblemonfix.combat.BattleFocusHandler;
import cn.cnxice.cobblemonfix.combat.WardenBossEncounterHandler;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.categories.DamageCategories;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.battles.MoveTarget;
import java.lang.reflect.Method;
import com.cobblemon.mod.common.api.spawning.condition.AppendageCondition;
import com.cobblemon.mod.common.api.spawning.condition.GroundedSpawningCondition;
import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition;
import com.cobblemon.mod.common.api.spawning.condition.SeafloorSpawningCondition;
import com.cobblemon.mod.common.api.spawning.condition.SubmergedSpawningCondition;
import com.cobblemon.mod.common.api.spawning.condition.SurfaceSpawningCondition;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.charset.StandardCharsets;

@Mod(XicesCobblemonFix.MOD_ID)
public final class XicesCobblemonFix {
    public static final String MOD_ID = "xices_cobblemon_fix";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AtomicBoolean WARDEN_ABILITY_QUEUED = new AtomicBoolean();

    public XicesCobblemonFix(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerWardenAbility);
        QuestFilterItems.ITEMS.register(modEventBus);
        CombatEffectsRegistry.EFFECTS.register(modEventBus);
        TradingAutomationRegistry.ITEMS.register(modEventBus);
        TradingAutomationRegistry.BLOCKS.register(modEventBus);
        TradingAutomationRegistry.BLOCK_ENTITIES.register(modEventBus);
        PokemonBeehiveRegistry.BLOCKS.register(modEventBus);
        PokemonBeehiveRegistry.ITEMS.register(modEventBus);
        PokemonBeehiveRegistry.BLOCK_ENTITIES.register(modEventBus);
        modEventBus.addListener(PokemonBeehiveCapabilities::register);
        modEventBus.addListener(TradingAutomationCapabilities::register);
        modEventBus.addListener(PokemonBeehiveCreativeTab::add);
        modContainer.registerConfig(ModConfig.Type.SERVER, PokemonBeehiveConfig.SPEC);
        // 战斗属性参数在实体 tick 时需要立即可用，使用独立 COMMON 配置避免与蜂箱 SERVER 配置冲突。
        modContainer.registerConfig(ModConfig.Type.COMMON, BattleAttributeConfig.SPEC);
        modEventBus.addListener(CreateAgesEconomyDisabledHandler::onCreativeTabContents);
        modEventBus.addListener(BotanyPotsDisabledHandler::onCreativeTabContents);
        modEventBus.addListener(TradingAutomationCreativeTab::add);
        modEventBus.addListener(WorkForceMinerCapabilities::register);
        NeoForge.EVENT_BUS.addListener(TradingAutomationHandler::onTrade);
        NeoForge.EVENT_BUS.addListener(WorkForceMinerLootManager::onReloadListeners);
        NeoForge.EVENT_BUS.addListener(VolbeatIllumiseDropHandler::onLivingDrops);
        // The initial Showdown registration happens before Cobblemon/Mega Showdown
        // finishes its ability resource reload. Re-register once after the server
        // is ready so the runtime callbacks survive the final registry rebuild.
        NeoForge.EVENT_BUS.addListener(XicesCobblemonFix::onServerStarted);
        QuestTreeItemFilter.register();
        AppendageCondition.Companion.registerAppendage(
                GroundedSpawningCondition.class,
                XiceSpawnConditionAppendage.class
        );
        AppendageCondition.Companion.registerAppendage(
                FishingSpawningCondition.class,
                XiceSpawnConditionAppendage.class
        );
        AppendageCondition.Companion.registerAppendage(
                SeafloorSpawningCondition.class,
                XiceSpawnConditionAppendage.class
        );
        AppendageCondition.Companion.registerAppendage(
                SubmergedSpawningCondition.class,
                XiceSpawnConditionAppendage.class
        );
        AppendageCondition.Companion.registerAppendage(
                SurfaceSpawningCondition.class,
                XiceSpawnConditionAppendage.class
        );
        CobblemonEvents.EVOLUTION_COMPLETE.subscribe(NincadaEvolutionHandler::onEvolutionComplete);
        CobblemonEvents.BATTLE_STARTED_POST.subscribe(BattleFocusHandler::onStarted);
        CobblemonEvents.BATTLE_VICTORY.subscribe(BattleFocusHandler::onVictory);
        CobblemonEvents.BATTLE_FLED.subscribe(BattleFocusHandler::onFled);
        NeoForge.EVENT_BUS.addListener(BattleFocusHandler::onServerTick);
        NeoForge.EVENT_BUS.addListener(WardenBossEncounterHandler::onServerTick);
        NeoForge.EVENT_BUS.addListener(WardenBossEncounterHandler::onInteract);
        CobblemonEvents.BATTLE_VICTORY.subscribe(WardenBossEncounterHandler::onVictory);
        CobblemonEvents.BATTLE_FLED.subscribe(WardenBossEncounterHandler::onFled);
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(SpawnCompanionHandler::onPokemonSpawn);
        LOGGER.info("Xice's Cobblemon1.8.0 Fix loaded");
    }

    private static void onServerStarted(ServerStartedEvent event) {
        queueWardenAbilityRegistration();
    }

    private void registerWardenAbility(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (Abilities.get("xice_dark_shroud") == null) {
                Abilities.register(new AbilityTemplate("xice_dark_shroud", (template, forced, priority) -> new Ability(template, forced, priority),
                        "cobblemon.ability.xice_dark_shroud", "cobblemon.ability.xice_dark_shroud.desc"));
            }
            // Queue before Cobblemon launches its Showdown thread so the native
            // ability registry is populated during engine bootstrap.
        });
    }

    public static synchronized void registerWardenMoves() {
        if (Moves.getByName("soniclock") != null) return;
        try {
            MoveTemplate sonicLock = new MoveTemplate(
                    "soniclock",
                    -10005,
                    ElementalTypes.DARK,
                    DamageCategories.INSTANCE.getSTATUS(),
                    0.0,
                    MoveTarget.normal,
                    100.0,
                    20,
                    0,
                    1.0,
                    new Double[0],
                    1.0F
            );
            Method register = Moves.class.getDeclaredMethod("register", MoveTemplate.class);
            register.setAccessible(true);
            register.invoke(Moves.INSTANCE, sonicLock);
            LOGGER.info("Registered Java move template soniclock={}", Moves.getByName("soniclock") != null);
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to register Java move template soniclock", exception);
        }
    }

    public static void queueWardenAbilityRegistration() {
        if (!WARDEN_ABILITY_QUEUED.compareAndSet(false, true)) return;
        com.cobblemon.mod.common.Cobblemon.INSTANCE.getShowdownThread().queue(service -> {
            try {
                if (service instanceof com.cobblemon.mod.common.battles.runner.graal.GraalShowdownService graal) {
                    cn.cnxice.cobblemonfix.combat.WardenAbilityRegistration.register(graal);
                } else {
                    try (var in = XicesCobblemonFix.class.getResourceAsStream("/warden/dark_shroud.js")) {
                        if (in != null) service.sendRegistryEntry(new String(in.readAllBytes(), StandardCharsets.UTF_8), "ability");
                    }
                }
            } catch (Exception e) { LOGGER.warn("Warden ability registration failed", e); }
            finally { WARDEN_ABILITY_QUEUED.set(false); }
            return kotlin.Unit.INSTANCE;
        });
    }
}

