package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.battles.ShowdownMoveset;
import kotlin.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/** Cobblemon 1.8.0 assumes every Showdown request has a full gimmick move list.
 * Custom PvE requests may omit it; an empty mapping is the correct fallback. */
@Mixin(value = ShowdownMoveset.class, remap = false)
public abstract class ShowdownMovesetGimmickMixin {
    @Overwrite
    public final Unit setGimmickMapping() {
        return Unit.INSTANCE;
    }
}
