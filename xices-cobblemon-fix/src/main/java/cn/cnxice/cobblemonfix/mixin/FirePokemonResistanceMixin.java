package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Fire-type Pokémon defensive interactions with vanilla damage sources. */
@Mixin(PokemonEntity.class)
public abstract class FirePokemonResistanceMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void xice$fireImmunity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (isFireType(entity.getPokemon()) && (source.is(DamageTypeTags.IS_FIRE)
                || source.is(DamageTypes.LAVA) || source.is(DamageTypes.IN_FIRE)
                || source.is(DamageTypes.ON_FIRE))) cir.setReturnValue(false);
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$fireDamageModifiers(float amount, DamageSource source) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isFireType(entity.getPokemon())) return amount;
        if (source.is(DamageTypes.FREEZE)) amount *= 0.5F;
        if (source.getEntity() instanceof LivingEntity attacker && isArthropod(attacker)) amount *= 0.5F;
        if (source.is(DamageTypes.FALLING_BLOCK)) amount *= 0.5F;
        if (source.is(DamageTypes.IN_WALL)) amount *= 2.0F;
        return amount;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void xice$clearFire(CallbackInfo ci) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (isFireType(entity.getPokemon()) && entity.isOnFire()) entity.clearFire();
    }

    private static boolean isFireType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("fire".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }

    private static boolean isArthropod(LivingEntity entity) {
        String name = entity.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        return name.contains("spider") || name.contains("silverfish") || name.contains("endermite") || name.contains("bee");
    }
}
