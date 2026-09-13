package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Electric-type Pokémon defensive interactions. */
@Mixin(PokemonEntity.class)
public abstract class ElectricPokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$electricDamageModifiers(float amount, DamageSource source) {
        if (!isElectricType(((PokemonEntity) (Object) this).getPokemon())) return amount;
        if (source.is(DamageTypes.LIGHTNING_BOLT)) amount *= 0.5F;
        if (source.is(DamageTypes.FALLING_BLOCK)) amount *= 0.5F;
        return amount;
    }

    private static boolean isElectricType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("electric".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }
}
