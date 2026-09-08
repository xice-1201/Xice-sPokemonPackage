package cn.cnxice.smartphoneprogression.mixin;

import cn.cnxice.smartphoneprogression.ProgressionChecks;
import cn.cnxice.smartphoneprogression.SmartphoneProgression;
import com.cobblemon.mod.common.net.messages.server.pokedex.scanner.StartScanningPacket;
import com.cobblemon.mod.common.net.serverhandling.pokedex.scanner.StartScanningHandler;
import com.cobblemon.mod.common.util.PlayerExtensionsKt;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StartScanningHandler.class, remap = false)
public abstract class StartScanningHandlerMixin {
    @Inject(
            method = "handle(Lcom/cobblemon/mod/common/net/messages/server/pokedex/scanner/StartScanningPacket;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/level/ServerPlayer;)V",
            at = @At("HEAD"), cancellable = true, remap = false
    )
    private void smartphoneprogression$requireScannerUpgrade(StartScanningPacket packet, MinecraftServer server,
                                                             ServerPlayer player, CallbackInfo ci) {
        if (!PlayerExtensionsKt.isUsingPokedex(player)
                && !ProgressionChecks.playerHas(SmartphoneProgression.SCANNER, player)) {
            ci.cancel();
        }
    }
}
