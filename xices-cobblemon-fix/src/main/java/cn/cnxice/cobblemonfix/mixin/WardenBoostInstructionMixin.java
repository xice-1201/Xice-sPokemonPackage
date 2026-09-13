package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import cn.cnxice.cobblemonfix.combat.WardenAccuracyStageMirror;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.cobblemon.mod.common.battles.interpreter.instructions.BoostInstruction", remap = false)
class WardenBoostInstructionMixin {
    @Shadow public com.cobblemon.mod.common.battles.pokemon.BattlePokemon getPokemon() { return null; }
    @Shadow public String getStatKey() { return null; }
    @Shadow public int getStages() { return 0; }
    @Shadow public boolean isBoost() { return false; }

    @Inject(method = "invoke", at = @At("HEAD"), remap = false)
    private void xice$mirror(PokemonBattle battle, CallbackInfo ci) {
        WardenAccuracyStageMirror.applyBoost(getPokemon(), getStatKey(), getStages(), isBoost());
    }
}
