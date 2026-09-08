package cn.cnxice.smartphoneprogression.mixin;

import cn.cnxice.smartphoneprogression.ProgressionChecks;
import cn.cnxice.smartphoneprogression.SmartphoneProgression;
import com.nbp.cobblemon_smartphone.actions.OpenPcAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = OpenPcAction.class, remap = false)
public abstract class OpenPcActionMixin {
    @Inject(method = "isEnabled", at = @At("HEAD"), cancellable = true, remap = false)
    private void smartphoneprogression$requireUpgrade(CallbackInfoReturnable<Boolean> cir) {
        if (!ProgressionChecks.contextPhoneHas(SmartphoneProgression.PC)) {
            cir.setReturnValue(false);
        }
    }
}
