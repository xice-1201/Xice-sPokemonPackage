package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.projectile.Snowball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Grass-type Pokémon weaknesses, resistances and out-of-combat regeneration. */
@Mixin(PokemonEntity.class)
public abstract class GrassPokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$grassDamageModifiers(float amount, DamageSource source) {
        if (!isGrassType(((PokemonEntity) (Object) this).getPokemon())) return amount;
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA)) amount *= 2.0F;
        if (source.is(DamageTypes.FREEZE) || source.is(DamageTypes.LIGHTNING_BOLT)) amount *= 2.0F;
        if (source.is(DamageTypes.WITHER)) amount *= 2.0F;
        if (source.getDirectEntity() instanceof Snowball) amount += 2.0F;
        // Poison's periodic damage uses an un-attributed magic source in vanilla.
        if (source.is(DamageTypes.MAGIC) && source.getEntity() == null) amount *= 2.0F;
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
            String n = attacker.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
            if (n.contains("spider") || n.contains("silverfish") || n.contains("endermite") || n.equals("bee")) amount *= 2.0F;
            if (n.contains("fish") || n.contains("squid") || n.contains("guardian") || n.equals("dolphin")
                    || n.equals("turtle") || n.equals("axolotl") || n.equals("tadpole") || n.equals("frog")) amount *= 0.5F;
        }
        return amount;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void xice$grassRegeneration(CallbackInfo ci) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        Pokemon pokemon = entity.getPokemon();
        if (!isGrassType(pokemon) || entity.level().isClientSide() || entity.getTarget() != null || entity.tickCount % 400 != 0
                || pokemon.getCurrentHealth() >= pokemon.getMaxHealth()) return;
        var pos = entity.blockPosition().below();
        var state = entity.level().getBlockState(pos);
        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.PODZOL) || state.is(Blocks.MYCELIUM)) {
            entity.level().setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
            int healed = Math.max(1, (pokemon.getMaxHealth() + 2) / 3);
            pokemon.setCurrentHealth(Math.min(pokemon.getMaxHealth(), pokemon.getCurrentHealth() + healed));
            entity.heal(entity.getMaxHealth() / 3.0F);
        }
    }

    private static boolean isGrassType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("grass".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }
}
