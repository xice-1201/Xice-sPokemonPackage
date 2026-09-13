package cn.cnxice.cobblemonfix.mixin;

import cn.cnxice.cobblemonfix.PokemonBeehivePastureBridge;
import com.cobblemon.mod.common.net.messages.server.pasture.UnpastureAllPokemonPacket;
import com.cobblemon.mod.common.net.serverhandling.pasture.UnpastureAllPokemonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UnpastureAllPokemonHandler.class)
public final class PokemonBeehiveUnpastureAllMixin {
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void xice$handle(UnpastureAllPokemonPacket packet, MinecraftServer server, ServerPlayer player, CallbackInfo ci) {
        if (PokemonBeehivePastureBridge.handle(packet, server, player)) ci.cancel();
    }
}
