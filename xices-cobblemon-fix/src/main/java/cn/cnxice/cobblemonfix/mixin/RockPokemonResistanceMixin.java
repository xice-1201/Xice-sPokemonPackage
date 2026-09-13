package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonEntity.class)
public abstract class RockPokemonResistanceMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void xice$rockFireImmunity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (isRockType(((PokemonEntity) (Object) this).getPokemon()) && source.is(DamageTypeTags.IS_FIRE)) cir.setReturnValue(false);
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$rockDamageModifiers(float amount, DamageSource source) {
        if (!isRockType(((PokemonEntity) (Object) this).getPokemon())) return amount;
        if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.LAVA)) amount *= 0.5F;
        if (source.is(DamageTypes.FALLING_BLOCK)) amount *= 2.0F;
        if (source.is(DamageTypes.WITHER)) amount *= 0.5F;
        if (source.is(DamageTypes.MAGIC) && source.getEntity() == null) amount *= 0.5F;
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
            String n = attacker.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
            if (n.contains("fish") || n.contains("squid") || n.contains("guardian") || n.equals("dolphin") || n.equals("turtle") || n.equals("axolotl") || n.equals("tadpole") || n.equals("frog")) amount *= 2.0F;
        }
        return amount;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void xice$rockKnockbackResistance(CallbackInfo ci) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (entity.level().isClientSide() || !isRockType(entity.getPokemon())) return;
        var attr = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (attr != null && attr.getBaseValue() < 0.9) attr.setBaseValue(0.9);
    }

    private static boolean isRockType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("rock".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }
}
