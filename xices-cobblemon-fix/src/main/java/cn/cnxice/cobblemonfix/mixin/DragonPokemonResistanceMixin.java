package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.Snowball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonEntity.class)
public abstract class DragonPokemonResistanceMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void xice$dragonBreathImmunity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (isDragonType(((PokemonEntity) (Object) this).getPokemon()) && source.is(DamageTypes.DRAGON_BREATH)) cir.setReturnValue(false);
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$dragonDamageModifiers(float amount, DamageSource source) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isDragonType(entity.getPokemon())) return amount;
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA)
                || source.is(DamageTypes.LIGHTNING_BOLT)) amount *= 0.5F;
        if (source.is(DamageTypes.FREEZE)) amount *= 2.0F;
        if (source.getDirectEntity() instanceof Snowball) amount += 2.0F;
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
            String n = attacker.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
            if (n.contains("fish") || n.contains("squid") || n.contains("guardian") || n.equals("dolphin") || n.equals("turtle") || n.equals("axolotl") || n.equals("tadpole") || n.equals("frog")) amount *= 0.5F;
            if (attacker instanceof EnderDragon) amount *= 2.0F;
        }
        return amount;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void xice$dragonBreathHealing(CallbackInfo ci) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (entity.level().isClientSide() || !isDragonType(entity.getPokemon()) || entity.tickCount % 20 != 0) return;
        boolean inBreath = !entity.level().getEntitiesOfClass(AreaEffectCloud.class, entity.getBoundingBox().inflate(1.5), cloud ->
                cloud.getOwner() instanceof EnderDragon).isEmpty();
        if (inBreath) entity.heal(1.0F);
    }

    private static boolean isDragonType(Pokemon pokemon) { if (pokemon == null) return false; for (var t : pokemon.getTypes()) if ("dragon".equalsIgnoreCase(t.getShowdownId())) return true; return false; }
}
