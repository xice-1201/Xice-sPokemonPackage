package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Ice-type Pokémon fire/freeze interactions and snow-weather defenses. */
@Mixin(PokemonEntity.class)
public abstract class IcePokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$iceDamageModifiers(float amount, DamageSource source) {
        if (!isIceType(((PokemonEntity) (Object) this).getPokemon())) return amount;
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA)) amount *= 2.0F;
        if (source.is(DamageTypes.FALLING_BLOCK)) amount *= 2.0F;
        if (source.is(DamageTypes.FREEZE)) amount = 0.0F;
        return amount;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void xice$iceSnowDefense(CallbackInfo ci) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isIceType(entity.getPokemon()) || entity.level().isClientSide()) return;
        boolean snowing = entity.level().isRaining() && entity.level().getBiome(entity.blockPosition()).value().coldEnoughToSnow(entity.blockPosition());
        double armor = entity.getPersistentData().getDouble("xice_base_armor");
        double toughness = entity.getPersistentData().getDouble("xice_base_armor_toughness");
        if (armor <= 0.0) armor = entity.getAttributeValue(Attributes.ARMOR);
        if (toughness <= 0.0) toughness = entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        var armorAttribute = entity.getAttribute(Attributes.ARMOR);
        var toughnessAttribute = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (armorAttribute != null) armorAttribute.setBaseValue(snowing ? armor * 1.5 : armor);
        if (toughnessAttribute != null) toughnessAttribute.setBaseValue(snowing ? toughness * 1.5 : toughness);
    }

    private static boolean isIceType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("ice".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }
}
