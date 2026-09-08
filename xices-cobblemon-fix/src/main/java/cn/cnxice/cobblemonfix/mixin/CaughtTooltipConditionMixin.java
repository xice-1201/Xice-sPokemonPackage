package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.arcaryx.cobblemonintegrations.jade.PokemonTooltipCondition$CAUGHT", remap = false)
public abstract class CaughtTooltipConditionMixin {
    @Redirect(
            method = "evaluateCondition",
            at = @At(value = "FIELD", target = "Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;CAUGHT:Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;", opcode = Opcodes.GETSTATIC, remap = false)
    )
    private PokedexEntryProgress xicesFix$useOwned() {
        return PokedexEntryProgress.OWNED;
    }
}
