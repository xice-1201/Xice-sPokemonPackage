package cn.cnxice.cobblemonfix.mixin;

import cn.cnxice.cobblemonfix.PokemonBeehivePastureBridge;
import com.cobblemon.mod.common.net.messages.server.pasture.PasturePokemonPacket;
import com.cobblemon.mod.common.net.serverhandling.pasture.PasturePokemonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PasturePokemonHandler.class)
public final class PokemonBeehivePasturePokemonMixin {
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void xice$handle(PasturePokemonPacket packet, MinecraftServer server, ServerPlayer player, CallbackInfo ci) {
        if (PokemonBeehivePastureBridge.handle(packet, server, player)) ci.cancel();
    }
}
