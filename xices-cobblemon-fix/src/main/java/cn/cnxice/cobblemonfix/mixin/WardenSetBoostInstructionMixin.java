package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import cn.cnxice.cobblemonfix.combat.WardenAccuracyStageMirror;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.cobblemon.mod.common.battles.interpreter.instructions.SetBoostInstruction", remap = false)
class WardenSetBoostInstructionMixin {
    @Shadow public BattleMessage getMessage() { return null; }

    @Inject(method = "invoke", at = @At("HEAD"), remap = false)
    private void xice$mirror(PokemonBattle battle, CallbackInfo ci) {
        WardenAccuracyStageMirror.applySetBoostMessage(getMessage(), battle);
    }
}
