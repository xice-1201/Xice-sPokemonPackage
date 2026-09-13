package cn.cnxice.cobblemonfix.mixin;
import cn.cnxice.cobblemonfix.combat.CombatEffectsRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class BattleFocusMixin {
    @Inject(method="hurt", at=@At("HEAD"), cancellable=true)
    private void xice$ignoreDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self=(LivingEntity)(Object)this;
        if (source.getEntity() instanceof net.minecraft.server.level.ServerPlayer player &&
                player.hasEffect(CombatEffectsRegistry.BATTLE_FOCUS)) {
            cir.setReturnValue(false);
            return;
        }
        if (self.hasEffect(CombatEffectsRegistry.BATTLE_FOCUS) && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) cir.setReturnValue(false);
    }
    @Inject(method="knockback", at=@At("HEAD"), cancellable=true)
    private void xice$ignoreKnockback(double strength, double x, double z, CallbackInfo ci) {
        if (((LivingEntity)(Object)this).hasEffect(CombatEffectsRegistry.BATTLE_FOCUS)) ci.cancel();
    }
}
