package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.arcaryx.cobblemonintegrations.jade.PokemonTooltipCondition$UNKNOWN", remap = false)
public abstract class UnknownTooltipConditionMixin {
    @Redirect(
            method = "evaluateCondition",
            at = @At(value = "FIELD", target = "Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;NONE:Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;", opcode = Opcodes.GETSTATIC, remap = false)
    )
    private PokedexEntryProgress xicesFix$useUnregistered() {
        return PokedexEntryProgress.UNREGISTERED;
    }
}
