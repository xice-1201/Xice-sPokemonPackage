package cn.cnxice.cobblemonfix.mixin;

import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftbxmodcompat.ftbquests.jei.FTBQuestsJEIIntegration;
import dev.ftb.mods.ftbxmodcompat.ftbquests.jei.helper.JEIRecipeHelper;
import mezz.jei.api.runtime.IJeiRuntime;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Backfills the bookmark-key method added by FTB Quests 2101.1.34 but missing
 * from FTB XMod Compat 21.1.8, preventing an AbstractMethodError in the valid
 * item list opened by filter-backed item tasks.
 */
@Mixin(value = JEIRecipeHelper.class, remap = false)
public abstract class FTBXModCompatJEIRecipeHelperMixin {
    public boolean isBookmarkKey(Key key) {
        IJeiRuntime runtime = FTBQuestsJEIIntegration.runtime;
        return runtime != null
                && runtime.getKeyMappings().getBookmark().isActiveAndMatches(key.getInputMapping());
    }
}
