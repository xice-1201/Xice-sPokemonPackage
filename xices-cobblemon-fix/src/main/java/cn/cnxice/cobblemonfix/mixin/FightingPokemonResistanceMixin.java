package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Fighting-type Pokémon resist damage dealt by vanilla arthropods. */
@Mixin(PokemonEntity.class)
public abstract class FightingPokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$fightingArthropodResistance(float amount, DamageSource source) {
        if (!isFightingType(((PokemonEntity) (Object) this).getPokemon())) return amount;
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker
                && isVanillaArthropod(attacker)) {
            return amount * 0.5F;
        }
        return amount;
    }

    private static boolean isFightingType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) {
            if ("fighting".equalsIgnoreCase(type.getShowdownId())) return true;
        }
        return false;
    }

    private static boolean isVanillaArthropod(net.minecraft.world.entity.LivingEntity entity) {
        String n = entity.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        return n.contains("spider") || n.contains("silverfish") || n.contains("endermite") || n.equals("bee");
    }
}
