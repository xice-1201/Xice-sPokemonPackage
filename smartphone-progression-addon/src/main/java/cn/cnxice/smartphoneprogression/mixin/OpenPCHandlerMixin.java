package cn.cnxice.smartphoneprogression.mixin;

import cn.cnxice.smartphoneprogression.ProgressionChecks;
import cn.cnxice.smartphoneprogression.SmartphoneProgression;
import com.nbp.cobblemon_smartphone.network.handler.OpenPCHandler;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OpenPCHandler.class, remap = false)
public abstract class OpenPCHandlerMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private void smartphoneprogression$requireUpgrade(ServerPlayer player, boolean fromQuickAction, CallbackInfo ci) {
        if (!ProgressionChecks.playerHas(SmartphoneProgression.PC, player)) {
            ci.cancel();
        }
    }
}
