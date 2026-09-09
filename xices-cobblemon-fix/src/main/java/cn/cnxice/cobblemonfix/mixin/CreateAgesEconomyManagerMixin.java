package cn.cnxice.cobblemonfix.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

/** Removes every runtime data source used by Create: Ages' profession/delivery economy. */
public final class CreateAgesEconomyManagerMixin {
    private CreateAgesEconomyManagerMixin() {
    }

    @Mixin(targets = "com.riftx666.createages.economy.ProfessionManager", remap = false)
    public static abstract class Professions {
        @Inject(method = "getProfessions", at = @At("HEAD"), cancellable = true)
        private static void xice$removeProfessions(CallbackInfoReturnable<List<?>> cir) {
            cir.setReturnValue(List.of());
        }
    }

    @Mixin(targets = "com.riftx666.createages.economy.ShipmentExportManager", remap = false)
    public static abstract class ShipmentExports {
        @Inject(method = "getExports", at = @At("HEAD"), cancellable = true)
        private static void xice$removeExports(CallbackInfoReturnable<List<?>> cir) {
            cir.setReturnValue(List.of());
        }

        @Inject(method = "findFor", at = @At("HEAD"), cancellable = true)
        private static void xice$preventExportLookup(CallbackInfoReturnable<Optional<?>> cir) {
            cir.setReturnValue(Optional.empty());
        }
    }

    @Mixin(targets = "com.riftx666.createages.economy.ShopOfferManager", remap = false)
    public static abstract class ShopOffers {
        @Inject(method = "getOffers", at = @At("HEAD"), cancellable = true)
        private static void xice$removeOffers(CallbackInfoReturnable<List<?>> cir) {
            cir.setReturnValue(List.of());
        }
    }

    @Mixin(targets = "com.riftx666.createages.bundle.BundleManager", remap = false)
    public static abstract class Bundles {
        @Inject(method = "getBundles", at = @At("HEAD"), cancellable = true)
        private static void xice$removeBundles(CallbackInfoReturnable<List<?>> cir) {
            cir.setReturnValue(List.of());
        }

        @Inject(method = "getBundle", at = @At("HEAD"), cancellable = true)
        private static void xice$preventBundleLookup(CallbackInfoReturnable<Object> cir) {
            cir.setReturnValue(null);
        }

        @Inject(method = "setBundles", at = @At("HEAD"), cancellable = true)
        private static void xice$preventBundleLoading(CallbackInfo ci) {
            ci.cancel();
        }
    }
}
