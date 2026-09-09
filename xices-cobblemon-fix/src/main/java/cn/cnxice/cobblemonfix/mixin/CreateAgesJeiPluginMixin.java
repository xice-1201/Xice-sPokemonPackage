package cn.cnxice.cobblemonfix.mixin;

import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Prevents Create: Ages from registering its synthetic profession, shipment and bundle JEI pages. */
@Mixin(targets = "com.riftx666.createages.compat.jei.CreateAgesJeiPlugin", remap = false)
public abstract class CreateAgesJeiPluginMixin {
    @Inject(method = "registerCategories", at = @At("HEAD"), cancellable = true)
    private void xice$disableCategories(IRecipeCategoryRegistration registration, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "registerRecipes", at = @At("HEAD"), cancellable = true)
    private void xice$disableRecipes(IRecipeRegistration registration, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "registerRecipeCatalysts", at = @At("HEAD"), cancellable = true)
    private void xice$disableCatalysts(IRecipeCatalystRegistration registration, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "registerGuiHandlers", at = @At("HEAD"), cancellable = true)
    private void xice$disableGuiHandlers(IGuiHandlerRegistration registration, CallbackInfo ci) {
        ci.cancel();
    }
}
