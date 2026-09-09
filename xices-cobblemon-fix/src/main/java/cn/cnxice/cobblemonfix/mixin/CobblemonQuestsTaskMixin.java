package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import winterwolfsv.cobblemon_quests.tasks.CobblemonTask;

/** Maps the removed Cobblemon 1.7 CAUGHT state to Cobblemon 1.8 OWNED. */
@Mixin(value = CobblemonTask.class, remap = false)
public abstract class CobblemonQuestsTaskMixin {
    @Redirect(
            method = "increaseHaveRegistered",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;CAUGHT:Lcom/cobblemon/mod/common/api/pokedex/PokedexEntryProgress;"
            )
    )
    private PokedexEntryProgress xice$useOwnedProgress() {
        return PokedexEntryProgress.OWNED;
    }
}
