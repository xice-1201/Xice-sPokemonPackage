package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonEntity.class)
public abstract class BugPokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$bugDamageModifiers(float amount, DamageSource source) {
        if (!isBugType(((PokemonEntity) (Object) this).getPokemon())) return amount;
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA)) amount *= 2.0F;
        int bane = baneLevel(source);
        if (bane > 0) {
            amount += 2.5F * bane;
            // Bane of Arthropods also applies Slowness IV for a random 1–3 seconds.
            if (!((PokemonEntity) (Object) this).level().isClientSide()) {
                int duration = 20 + ((PokemonEntity) (Object) this).getRandom().nextInt(20 * bane);
                ((PokemonEntity) (Object) this).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 3), source.getEntity());
            }
        }
        return amount;
    }

    private static int baneLevel(DamageSource source) {
        var attacker = source.getEntity();
        if (!(attacker instanceof LivingEntity living)) return 0;
        var holder = living.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BANE_OF_ARTHROPODS);
        int level = EnchantmentHelper.getItemEnchantmentLevel(holder, living.getMainHandItem());
        level = Math.max(level, EnchantmentHelper.getItemEnchantmentLevel(holder, living.getOffhandItem()));
        return level;
    }

    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    private void xice$bugWebImmunity(BlockState state, Vec3 motionMultiplier, CallbackInfo ci) {
        if (isBugType(((PokemonEntity) (Object) this).getPokemon()) && state.is(Blocks.COBWEB)) ci.cancel();
    }

    private static boolean isBugType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("bug".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }
}
