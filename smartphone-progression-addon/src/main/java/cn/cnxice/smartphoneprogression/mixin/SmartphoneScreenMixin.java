package cn.cnxice.smartphoneprogression.mixin;

import com.nbp.cobblemon_smartphone.api.SmartphoneActionRegistry;
import com.nbp.cobblemon_smartphone.client.gui.SmartphoneScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SmartphoneScreen.class, remap = false)
public abstract class SmartphoneScreenMixin {
    @Shadow private int screenX;
    @Shadow private int screenY;

    @Inject(method = "render", at = @At("TAIL"), remap = false)
    private void smartphoneprogression$renderUpgradeHint(GuiGraphics graphics, int mouseX, int mouseY,
                                                          float partialTick, CallbackInfo ci) {
        if (!SmartphoneActionRegistry.INSTANCE.getEnabledActions().isEmpty()) {
            return;
        }

        var font = Minecraft.getInstance().font;
        int centerX = screenX + 65;
        graphics.drawCenteredString(font,
                Component.translatable("screen.smartphoneprogression.empty"), centerX, screenY + 82, 0xFFFFFF);
        graphics.drawCenteredString(font,
                Component.translatable("screen.smartphoneprogression.upgrade_hint"), centerX, screenY + 96, 0xD8D8D8);
        graphics.drawCenteredString(font,
                Component.translatable("screen.smartphoneprogression.smithing_hint"), centerX, screenY + 108, 0xD8D8D8);
    }
}
