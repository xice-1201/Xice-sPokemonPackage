package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.spawning.detail.PokemonHerdSpawnDetail;
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = SpawnDetail.class, remap = false)
public abstract class SpawnWeightMixin {
    private static final float WEATHER_BOOST = 1.5F;
    private static final float WEATHER_PENALTY = 0.5F;
    private static final TagKey<Block> FLOWERS = TagKey.create(
            Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "flowers")
    );
    private static final TagKey<Block> INFRASTRUCTURE = TagKey.create(
            Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "infrastructure_components")
    );
    private static final Map<String, Set<String>> TYPE_CACHE = new ConcurrentHashMap<>();

    @Inject(method = "getWeight", at = @At("RETURN"), cancellable = true)
    private void xicesFix$applyContextualWeight(SpawnablePosition position, CallbackInfoReturnable<Float> cir) {
        SpawnDetail detail = (SpawnDetail) (Object) this;
        float multiplier = 1.0F;

        if (detail instanceof PokemonSpawnDetail pokemonDetail) {
            PokemonProperties properties = pokemonDetail.getPokemon();
            multiplier *= typeWeatherMultiplier(resolveTypes(properties), position);

            String species = properties.getSpecies();
            if ("plusle".equals(species) || "minun".equals(species)) {
                if (hasNearbyTaggedBlock(position, INFRASTRUCTURE, 8, 5)) {
                    multiplier *= 2.0F;
                }
            }
            if ("volbeat".equals(species) || "illumise".equals(species)
                    || "carnivine".equals(species) || "comfey".equals(species)) {
                int flowers = countNearbyTaggedBlocks(position, FLOWERS, 6, 3);
                multiplier *= (float) Math.pow(1.03D, flowers);
            }
            if (!"alomomola".equals(species)
                    && resolveTypes(properties).contains("water")
                    && hasNearbyAlomomola(position)) {
                multiplier *= 2.0F;
            }
        } else if (detail instanceof PokemonHerdSpawnDetail herdDetail) {
            float totalWeight = 0.0F;
            float weightedMultiplier = 0.0F;
            for (PokemonHerdSpawnDetail.Herdable member : herdDetail.getHerdablePokemon()) {
                float memberWeight = Math.max(0.0F, member.getWeight());
                totalWeight += memberWeight;
                weightedMultiplier += memberWeight * typeWeatherMultiplier(resolveTypes(member.getPokemon()), position);
            }
            if (totalWeight > 0.0F) {
                multiplier *= weightedMultiplier / totalWeight;
            }
        }

        cir.setReturnValue(cir.getReturnValueF() * multiplier);
    }

    private static boolean hasNearbyAlomomola(SpawnablePosition position) {
        BlockPos center = position.getPosition();
        AABB area = new AABB(center).inflate(16.0D, 8.0D, 16.0D);
        return !position.getWorld().getEntitiesOfClass(
                PokemonEntity.class,
                area,
                entity -> "alomomola".equals(
                        entity.getPokemon().getSpecies().getResourceIdentifier().getPath())
        ).isEmpty();
    }

    private static float typeWeatherMultiplier(Set<String> types, SpawnablePosition position) {
        boolean raining = position.getWorld().isRaining();
        boolean thundering = position.getWorld().isThundering();
        boolean wetWeather = raining || thundering;
        boolean night = position.getWorld().isNight();
        float result = 1.0F;

        if (wetWeather) {
            if (types.contains("water")) {
                result *= WEATHER_BOOST;
            }
            if (types.contains("fire")) {
                result *= WEATHER_PENALTY;
            }
        }
        if (thundering) {
            if (types.contains("electric")) {
                result *= WEATHER_BOOST;
            }
            if (types.contains("ghost")) {
                result *= WEATHER_BOOST;
            }
        }
        if (types.contains("ghost")) {
            if (night) {
                result *= WEATHER_BOOST;
            } else if (!wetWeather) {
                result *= WEATHER_PENALTY;
            }
        }
        return result;
    }

    private static Set<String> resolveTypes(PokemonProperties properties) {
        String key = properties.asString(" ");
        return TYPE_CACHE.computeIfAbsent(key, ignored -> {
            Pokemon pokemon = properties.create();
            Set<String> types = new HashSet<>();
            pokemon.getTypes().forEach(type -> types.add(type.getName()));
            return Set.copyOf(types);
        });
    }

    private static boolean hasNearbyTaggedBlock(
            SpawnablePosition position, TagKey<Block> tag, int horizontalRadius, int verticalRadius) {
        BlockPos center = position.getPosition();
        for (BlockPos candidate : BlockPos.betweenClosed(
                center.offset(-horizontalRadius, -verticalRadius, -horizontalRadius),
                center.offset(horizontalRadius, verticalRadius, horizontalRadius))) {
            if (position.getWorld().getBlockState(candidate).is(tag)) {
                return true;
            }
        }
        return false;
    }

    private static int countNearbyTaggedBlocks(
            SpawnablePosition position, TagKey<Block> tag, int horizontalRadius, int verticalRadius) {
        int count = 0;
        BlockPos center = position.getPosition();
        for (BlockPos candidate : BlockPos.betweenClosed(
                center.offset(-horizontalRadius, -verticalRadius, -horizontalRadius),
                center.offset(horizontalRadius, verticalRadius, horizontalRadius))) {
            if (position.getWorld().getBlockState(candidate).is(tag)) {
                count++;
            }
        }
        return count;
    }
}
