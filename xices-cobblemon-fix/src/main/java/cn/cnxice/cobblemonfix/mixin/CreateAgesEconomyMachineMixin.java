package cn.cnxice.cobblemonfix.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Stops existing package assemblers and delivery stations in old worlds from processing. */
@Mixin(targets = {
        "com.riftx666.createages.blockentity.PackageAssemblerBlockEntity",
        "com.riftx666.createages.blockentity.PortStationBlockEntity"
}, remap = false)
public abstract class CreateAgesEconomyMachineMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void xice$disableMachine(CallbackInfo ci) {
        ci.cancel();
    }
}
