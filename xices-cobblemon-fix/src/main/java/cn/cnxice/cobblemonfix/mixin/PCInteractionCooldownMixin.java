package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Keeps interaction cooldowns moving while Pokémon are stored in an online player's PC. */
@Mixin(value = PlayerPartyStore.class, remap = false)
public abstract class PCInteractionCooldownMixin {
    @Inject(method = "onSecondPassed", at = @At("TAIL"))
    private void xice$tickPcInteractionCooldowns(ServerPlayer player, CallbackInfo ci) {
        PCStore pc = Cobblemon.INSTANCE.getStorage().getPC(player);
        for (Pokemon pokemon : pc) {
            if (!pokemon.getInteractionCooldowns().isEmpty()) {
                pokemon.tickInteractionCooldown(20);
            }
        }
    }
}
