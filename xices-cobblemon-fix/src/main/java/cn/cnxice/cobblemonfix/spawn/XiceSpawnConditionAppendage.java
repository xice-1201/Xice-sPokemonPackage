package cn.cnxice.cobblemonfix.spawn;

import cn.cnxice.cobblemonfix.XicesCobblemonFix;
import com.cobblemon.mod.common.api.spawning.condition.AppendageCondition;
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ChunkPos;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

public final class XiceSpawnConditionAppendage implements AppendageCondition {
    private static final TagKey<Block> ROTOM_REDSTONE_COMPONENTS = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "rotom_redstone_components")
    );
    private static final TagKey<Block> CRYSTAL_SOURCES = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "crystal_sources")
    );
    private static final TagKey<Block> CORAL_REEF_BLOCKS = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "coral_reef_blocks")
    );
    private static final TagKey<Block> HONEY_SOURCES = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "honey_sources")
    );
    private static final TagKey<Block> BERRY_SOURCES = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "berry_sources")
    );
    private static final TagKey<Block> TREE_SOURCES = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "tree_sources")
    );
    private static final TagKey<Block> IRON_SOURCES = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "iron_sources")
    );
    private static final TagKey<Block> MAGMA_SOURCES = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "magma_sources")
    );
    private static final TagKey<Block> CACTUS_SOURCES = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "cactus_sources")
    );
    private static final TagKey<Block> HEALING_SOURCES = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "healing_sources")
    );
    private static final TagKey<Block> INFRASTRUCTURE_SOURCES = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "infrastructure_components")
    );

    private Integer nearbyRedstoneRadius;
    private Integer nearbyCrystalRadius;
    private Integer nearbyCoralRadius;
    private Integer nearbyHoneyRadius;
    private Integer nearbyBerryRadius;
    private Integer nearbyTreeRadius;
    private Integer nearbyIronRadius;
    private Integer nearbyMagmaRadius;
    private Integer nearbyCactusRadius;
    private Integer nearbyHealingRadius;
    private Integer nearbyInfrastructureRadius;
    private List<String> excludedStructures = List.of();
    private List<String> nearbyStructures = List.of();
    private Integer nearbyStructureRadius;
    private Boolean requiresAnyStructure;
    private Boolean requiresNoStructure;
    private Integer noStructureRadius;
    private Integer nearbyWaterRadius;
    private Boolean twilightForestOnly;
    private Boolean overworldNonOceanOnly;
    private Boolean duskOrNightOnly;

    @Override
    public boolean fits(SpawnablePosition position) {
        var manager = position.getWorld().structureManager();
        BlockPos center = position.getPosition();

        if (Boolean.TRUE.equals(twilightForestOnly)
                && !position.getWorld().dimension().location().equals(
                ResourceLocation.fromNamespaceAndPath("twilightforest", "twilight_forest"))) {
            return false;
        }
        if (Boolean.TRUE.equals(overworldNonOceanOnly)) {
            if (position.getWorld().dimension() != Level.OVERWORLD
                    || position.getWorld().getBiome(center).is(BiomeTags.IS_OCEAN)) {
                return false;
            }
        }
        if (Boolean.TRUE.equals(duskOrNightOnly)) {
            long timeOfDay = Math.floorMod(position.getWorld().getDayTime(), 24000L);
            if (timeOfDay < 12000L || timeOfDay >= 23000L) {
                return false;
            }
        }

        if (Boolean.TRUE.equals(requiresAnyStructure) && !manager.hasAnyStructureAt(center)) {
            return false;
        }
        if (Boolean.TRUE.equals(requiresNoStructure) && manager.hasAnyStructureAt(center)) {
            return false;
        }
        if (noStructureRadius != null && hasStructureNear(position, Math.max(0, Math.min(128, noStructureRadius)))) {
            return false;
        }
        if (nearbyStructures != null && !nearbyStructures.isEmpty()
                && !hasNamedStructureNear(
                        position,
                        Math.max(0, Math.min(128, nearbyStructureRadius == null ? 32 : nearbyStructureRadius)),
                        nearbyStructures)) {
            return false;
        }

        if (excludedStructures != null && !excludedStructures.isEmpty()) {
            var cache = position.getStructureCache(center);
            for (String structure : excludedStructures) {
                if (structure != null && cache.check(manager, center, ResourceLocation.parse(structure))) {
                    return false;
                }
            }
        }

        if (nearbyRedstoneRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyRedstoneRadius)), ROTOM_REDSTONE_COMPONENTS)) {
            return false;
        }
        if (nearbyCrystalRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyCrystalRadius)), CRYSTAL_SOURCES)) {
            return false;
        }
        if (nearbyCoralRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyCoralRadius)), CORAL_REEF_BLOCKS)) {
            return false;
        }
        if (nearbyHoneyRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyHoneyRadius)), HONEY_SOURCES)) {
            return false;
        }
        if (nearbyBerryRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyBerryRadius)), BERRY_SOURCES)) {
            return false;
        }
        if (nearbyTreeRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyTreeRadius)), TREE_SOURCES)) {
            return false;
        }
        if (nearbyIronRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyIronRadius)), IRON_SOURCES)) {
            return false;
        }
        if (nearbyMagmaRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyMagmaRadius)), MAGMA_SOURCES)) {
            return false;
        }
        if (nearbyCactusRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyCactusRadius)), CACTUS_SOURCES)) {
            return false;
        }
        if (nearbyHealingRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyHealingRadius)), HEALING_SOURCES)) {
            return false;
        }
        if (nearbyInfrastructureRadius != null && !hasBlockNearby(
                position, center, Math.max(1, Math.min(16, nearbyInfrastructureRadius)), INFRASTRUCTURE_SOURCES)) {
            return false;
        }
        if (nearbyWaterRadius != null && !hasWaterNearby(
                position, center, Math.max(1, Math.min(16, nearbyWaterRadius)))) {
            return false;
        }
        return true;
    }

    private static boolean hasBlockNearby(
            SpawnablePosition position,
            BlockPos center,
            int radius,
            TagKey<Block> tag
    ) {
        for (BlockPos candidate : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            if (position.getWorld().getBlockState(candidate).is(tag)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasWaterNearby(SpawnablePosition position, BlockPos center, int radius) {
        for (BlockPos candidate : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            if (position.getWorld().getFluidState(candidate).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasStructureNear(SpawnablePosition position, int radius) {
        BlockPos center = position.getPosition();
        var manager = position.getWorld().structureManager();
        int minChunkX = Math.floorDiv(center.getX() - radius, 16);
        int maxChunkX = Math.floorDiv(center.getX() + radius, 16);
        int minChunkZ = Math.floorDiv(center.getZ() - radius, 16);
        int maxChunkZ = Math.floorDiv(center.getZ() + radius, 16);

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                for (var start : manager.startsForStructure(new ChunkPos(chunkX, chunkZ), structure -> true)) {
                    if (start.isValid() && start.getBoundingBox().intersects(
                            center.getX() - radius,
                            center.getZ() - radius,
                            center.getX() + radius,
                            center.getZ() + radius)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasNamedStructureNear(
            SpawnablePosition position,
            int radius,
            List<String> structureNames
    ) {
        Set<ResourceLocation> targets = new HashSet<>();
        for (String name : structureNames) {
            if (name != null) {
                targets.add(ResourceLocation.parse(name));
            }
        }
        BlockPos center = position.getPosition();
        var manager = position.getWorld().structureManager();
        var registry = position.getWorld().registryAccess().registryOrThrow(Registries.STRUCTURE);
        int minChunkX = Math.floorDiv(center.getX() - radius, 16);
        int maxChunkX = Math.floorDiv(center.getX() + radius, 16);
        int minChunkZ = Math.floorDiv(center.getZ() - radius, 16);
        int maxChunkZ = Math.floorDiv(center.getZ() + radius, 16);

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                for (var start : manager.startsForStructure(
                        new ChunkPos(chunkX, chunkZ),
                        structure -> targets.contains(registry.getKey(structure)))) {
                    if (start.isValid() && start.getBoundingBox().intersects(
                            center.getX() - radius,
                            center.getZ() - radius,
                            center.getX() + radius,
                            center.getZ() + radius)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
