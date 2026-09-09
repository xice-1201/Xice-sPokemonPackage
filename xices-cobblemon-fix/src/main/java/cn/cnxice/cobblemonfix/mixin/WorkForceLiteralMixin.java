package cn.cnxice.cobblemonfix.mixin;

import cn.cnxice.cobblemonfix.WorkForceTranslations;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Localizes user-facing strings that Cobblemon WorkForce 1.0.1 hard-codes. */
@Mixin(Component.class)
public interface WorkForceLiteralMixin {
    @Inject(method = "literal", at = @At("HEAD"), cancellable = true)
    private static void xice$translateWorkForceLiteral(String text, CallbackInfoReturnable<MutableComponent> cir) {
        String translated = WorkForceTranslations.translate(text);
        if (!translated.equals(text)) {
            cir.setReturnValue(MutableComponent.create(PlainTextContents.create(translated)));
        }
    }
}
