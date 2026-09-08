package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.arcaryx.cobblemonintegrations.jade.PokemonTooltip$DEX_STATUS$WhenMappings", remap = false)
public abstract class DexStatusMappingsMixin {
    @Redirect(
            method = "<clinit>",
            at = @At(value = "FIELD", target = "Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;NONE:Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;", opcode = Opcodes.GETSTATIC, remap = false)
    )
    private static PokedexEntryProgress xicesFix$mapNone() {
        return PokedexEntryProgress.UNREGISTERED;
    }

    @Redirect(
            method = "<clinit>",
            at = @At(value = "FIELD", target = "Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;ENCOUNTERED:Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;", opcode = Opcodes.GETSTATIC, remap = false)
    )
    private static PokedexEntryProgress xicesFix$mapEncountered() {
        return PokedexEntryProgress.SEEN;
    }

    @Redirect(
            method = "<clinit>",
            at = @At(value = "FIELD", target = "Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;CAUGHT:Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;", opcode = Opcodes.GETSTATIC, remap = false)
    )
    private static PokedexEntryProgress xicesFix$mapCaught() {
        return PokedexEntryProgress.OWNED;
    }
}
