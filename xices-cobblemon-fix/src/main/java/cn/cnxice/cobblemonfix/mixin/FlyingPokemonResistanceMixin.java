package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.projectile.Snowball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PokemonEntity.class)
public abstract class FlyingPokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$flyingDamageModifiers(float amount, DamageSource source) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isFlyingType(entity.getPokemon())) return amount;
        if (source.is(DamageTypes.FALL)) return 0.0F;
        if (source.is(DamageTypes.LIGHTNING_BOLT) || source.is(DamageTypes.FREEZE)) amount *= 2.0F;
        if (source.getDirectEntity() instanceof Snowball) amount += 2.0F;
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker && isArthropod(attacker)) amount *= 0.5F;
        return amount;
    }
    private static boolean isFlyingType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("flying".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }
    private static boolean isArthropod(net.minecraft.world.entity.LivingEntity entity) {
        String n = entity.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        return n.contains("spider") || n.contains("silverfish") || n.contains("endermite") || n.equals("bee");
    }
}
