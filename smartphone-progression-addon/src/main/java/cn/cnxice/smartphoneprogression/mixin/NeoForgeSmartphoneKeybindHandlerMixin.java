package cn.cnxice.smartphoneprogression.mixin;

import cn.cnxice.smartphoneprogression.SmartphoneProgression;
import com.nbp.cobblemon_smartphone.client.scanner.ScannerManager;
import com.nbp.cobblemon_smartphone.upgrade.SmartphoneUpgradeHelperKt;
import com.nbp.neoforge.compat.SmartphoneCompatManager;
import com.nbp.neoforge.keybind.NeoForgeSmartphoneKeybindHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NeoForgeSmartphoneKeybindHandler.class, remap = false)
public abstract class NeoForgeSmartphoneKeybindHandlerMixin {
    @Inject(method = "handleScannerKeybind", at = @At("HEAD"), cancellable = true, remap = false)
    private void smartphoneprogression$gateScannerKey(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            ci.cancel();
            return;
        }

        ItemStack smartphone = SmartphoneCompatManager.INSTANCE.getSmartphone(player);
        if (smartphone == null || smartphone.isEmpty()) {
            ci.cancel();
            return;
        }

        if (!SmartphoneUpgradeHelperKt.hasUpgrade(smartphone, SmartphoneProgression.SCANNER)) {
            if (ScannerManager.INSTANCE.isActive()) {
                ScannerManager.INSTANCE.deactivate();
            }
            player.displayClientMessage(
                    Component.translatable("message.smartphoneprogression.scanner_upgrade_required").withColor(0xFD0000),
                    true
            );
            ci.cancel();
        }
    }

    @Inject(method = "onClientTick", at = @At("HEAD"), remap = false)
    private void smartphoneprogression$stopScannerWithoutUpgrade(Minecraft client, CallbackInfo ci) {
        if (!ScannerManager.INSTANCE.isActive() || client.player == null) {
            return;
        }
        ItemStack smartphone = SmartphoneCompatManager.INSTANCE.getSmartphone(client.player);
        if (smartphone == null || smartphone.isEmpty()
                || !SmartphoneUpgradeHelperKt.hasUpgrade(smartphone, SmartphoneProgression.SCANNER)) {
            ScannerManager.INSTANCE.deactivate();
        }
    }
}
