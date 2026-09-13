package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import cn.cnxice.cobblemonfix.combat.WardenAccuracyStageMirror;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.cobblemon.mod.common.battles.interpreter.instructions.ClearAllBoostInstruction", remap = false)
class WardenClearAllBoostInstructionMixin {
    @Inject(method = "invoke", at = @At("HEAD"), remap = false)
    private void xice$mirror(PokemonBattle battle, CallbackInfo ci) {
        WardenAccuracyStageMirror.clearAll(battle.getActivePokemon(), "clearallboost");
    }
}
