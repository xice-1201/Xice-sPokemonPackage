package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.arcaryx.cobblemonintegrations.config.CobblemonIntegrationsConfig;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CobblemonIntegrationsConfig.class, remap = false)
public abstract class CobblemonIntegrationsConfigMixin {
    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;NONE:Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;",
                    opcode = Opcodes.GETSTATIC,
                    remap = false
            )
    )
    private static PokedexEntryProgress xicesFix$useUnregistered() {
        return PokedexEntryProgress.UNREGISTERED;
    }
}
