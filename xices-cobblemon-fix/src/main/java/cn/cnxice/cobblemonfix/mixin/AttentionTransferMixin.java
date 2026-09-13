package cn.cnxice.cobblemonfix.mixin;

import cn.cnxice.cobblemonfix.combat.AttentionTransferHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class AttentionTransferMixin {
    @Unique private static final ThreadLocal<Boolean> xice$redirecting = ThreadLocal.withInitial(() -> false);

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void xice$redirectAttention(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity target = (LivingEntity) (Object) this;
        if (target.level().isClientSide() || xice$redirecting.get() ||
                !(source.getEntity() instanceof LivingEntity attacker) ||
                AttentionTransferHandler.isGrassAttacker(attacker) &&
                AttentionTransferHandler.isPowderSource(target)) return;
        LivingEntity redirector = AttentionTransferHandler.resolveRedirector(target);
        if (redirector == null || redirector == target || !redirector.isAlive() ||
                AttentionTransferHandler.isFriendlySource(attacker, redirector)) return;
        xice$redirecting.set(true);
        try {
            cir.setReturnValue(redirector.hurt(source, amount));
        } finally {
            xice$redirecting.set(false);
        }
    }
}
