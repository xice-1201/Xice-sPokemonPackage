package cn.cnxice.cobblemonfix.mixin;

import net.darkhax.botanypots.common.impl.block.BotanyPotBlock;
import net.darkhax.botanypots.common.impl.block.BotanyPotRenderer;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Keeps functional hopper pots visually fixed at the crop's mature stage. */
@Mixin(value = BotanyPotRenderer.class, remap = false)
public abstract class BotanyPotRendererMixin {
    @ModifyVariable(
            method = "render(Lnet/darkhax/botanypots/common/impl/block/entity/BotanyPotBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "STORE"),
            index = 10
    )
    private float xice$showHopperCropAsMature(float growthProgress, BotanyPotBlockEntity pot) {
        if (pot.getBlockState().getBlock() instanceof BotanyPotBlock block && block.isHopper()) {
            return 1.0F;
        }
        return growthProgress;
    }
}
