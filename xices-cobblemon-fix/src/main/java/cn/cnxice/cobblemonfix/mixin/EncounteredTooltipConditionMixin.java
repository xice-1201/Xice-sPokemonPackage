package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.arcaryx.cobblemonintegrations.jade.PokemonTooltipCondition$ENCOUNTERED", remap = false)
public abstract class EncounteredTooltipConditionMixin {
    @Redirect(
            method = "evaluateCondition",
            at = @At(value = "FIELD", target = "Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;ENCOUNTERED:Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;", opcode = Opcodes.GETSTATIC, remap = false)
    )
    private PokedexEntryProgress xicesFix$useSeen() {
        return PokedexEntryProgress.SEEN;
    }
}
