package cn.cnxice.cobblemonfix;

import cn.cnxice.cobblemonfix.spawn.NincadaEvolutionHandler;
import cn.cnxice.cobblemonfix.spawn.SpawnCompanionHandler;
import cn.cnxice.cobblemonfix.spawn.XiceSpawnConditionAppendage;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.spawning.condition.AppendageCondition;
import com.cobblemon.mod.common.api.spawning.condition.GroundedSpawningCondition;
import com.cobblemon.mod.common.api.spawning.condition.FishingSpawningCondition;
import com.cobblemon.mod.common.api.spawning.condition.SeafloorSpawningCondition;
import com.cobblemon.mod.common.api.spawning.condition.SubmergedSpawningCondition;
import com.cobblemon.mod.common.api.spawning.condition.SurfaceSpawningCondition;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(XicesCobblemonFix.MOD_ID)
public final class XicesCobblemonFix {
    public static final String MOD_ID = "xices_cobblemon_fix";
    private static final Logger LOGGER = LogUtils.getLogger();

    public XicesCobblemonFix() {
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
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(SpawnCompanionHandler::onPokemonSpawn);
        LOGGER.info("Xice's Cobblemon1.8.0 Fix loaded");
    }
}
