package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.core.registries.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Water-type Pokémon aquatic breathing and damage interactions. */
@Mixin(PokemonEntity.class)
public abstract class WaterPokemonResistanceMixin {
    @Inject(method = "canBreatheUnderwater", at = @At("HEAD"), cancellable = true)
    private void xice$waterBreathing(CallbackInfoReturnable<Boolean> cir) {
        if (isWaterType(((PokemonEntity) (Object) this).getPokemon())) cir.setReturnValue(true);
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$waterDamageModifiers(float amount, DamageSource source) {
        if (!isWaterType(((PokemonEntity) (Object) this).getPokemon())) return amount;
        if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.LAVA)
                || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)) amount *= 0.5F;
        if (source.is(DamageTypes.LIGHTNING_BOLT)) amount *= 2.0F;
        if (source.is(DamageTypes.FREEZE)) amount *= 0.5F;
        if (source.is(DamageTypes.FALLING_BLOCK)) amount *= 0.5F;
        int impaling = impalingLevel(source);
        // Vanilla Impaling: +2.5 damage per enchantment level against aquatic targets.
        if (impaling > 0) amount += 2.5F * impaling;
        return amount;
    }

    private static boolean isWaterType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("water".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }

    private static int impalingLevel(DamageSource source) {
        var direct = source.getDirectEntity();
        var attacker = source.getEntity();
        if (!(direct instanceof ThrownTrident) && !(attacker instanceof LivingEntity)) return 0;
        var level = (attacker != null ? attacker.level() : direct.level());
        var holder = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.IMPALING);
        int result = 0;
        if (direct instanceof ThrownTrident trident) {
            result = EnchantmentHelper.getItemEnchantmentLevel(holder, ((ThrownTridentAccessor) trident).getPickupItem());
        }
        if (attacker instanceof LivingEntity living) {
            result = Math.max(result, EnchantmentHelper.getItemEnchantmentLevel(holder, living.getMainHandItem()));
            result = Math.max(result, EnchantmentHelper.getItemEnchantmentLevel(holder, living.getOffhandItem()));
        }
        return result;
    }
}
