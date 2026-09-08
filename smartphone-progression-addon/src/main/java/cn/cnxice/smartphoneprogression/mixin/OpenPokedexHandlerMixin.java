package cn.cnxice.smartphoneprogression.mixin;

import cn.cnxice.smartphoneprogression.ProgressionChecks;
import cn.cnxice.smartphoneprogression.SmartphoneProgression;
import com.cobblemon.mod.common.client.pokedex.PokedexType;
import com.nbp.cobblemon_smartphone.network.handler.OpenPokedexHandler;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OpenPokedexHandler.class, remap = false)
public abstract class OpenPokedexHandlerMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private void smartphoneprogression$requireUpgrade(ServerPlayer player, PokedexType type, boolean fromQuickAction, CallbackInfo ci) {
        if (!ProgressionChecks.playerHas(SmartphoneProgression.POKEDEX, player)) {
            ci.cancel();
        }
    }
}
