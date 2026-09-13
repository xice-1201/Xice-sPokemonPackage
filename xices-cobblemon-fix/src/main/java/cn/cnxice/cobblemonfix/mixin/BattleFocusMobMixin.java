package cn.cnxice.cobblemonfix.mixin;
import cn.cnxice.cobblemonfix.combat.CombatEffectsRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class BattleFocusMobMixin {
    @Inject(method="setTarget", at=@At("HEAD"), cancellable=true)
    private void xice$clearHostileTarget(LivingEntity target, CallbackInfo ci) {
        if (target != null && target.hasEffect(CombatEffectsRegistry.BATTLE_FOCUS)) ci.cancel();
    }
}
