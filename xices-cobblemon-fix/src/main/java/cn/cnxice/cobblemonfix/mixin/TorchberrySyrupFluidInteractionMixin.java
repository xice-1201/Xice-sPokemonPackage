package cn.cnxice.cobblemonfix.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public abstract class TorchberrySyrupFluidInteractionMixin {
    private static final String SYRUP_NAMESPACE = "kubejs";
    private static final String SYRUP_PATH = "torchberry_syrup";
    private static final int FIZZ_EVENT = 1501;

    @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
    private void xice$applyTorchberrySyrupInteractions(
            Level level,
            BlockPos pos,
            BlockState state,
            CallbackInfoReturnable<Boolean> cir
    ) {
        FluidState self = state.getFluidState();
        if (self.isEmpty()) {
            return;
        }

        boolean selfIsSyrup = xice$isTorchberrySyrup(self);
        boolean selfIsLava = self.is(FluidTags.LAVA);

        for (Direction direction : Direction.values()) {
            FluidState neighbor = level.getFluidState(pos.relative(direction));
            if (neighbor.isEmpty()) {
                continue;
            }

            boolean neighborIsSyrup = xice$isTorchberrySyrup(neighbor);
            boolean neighborIsLava = neighbor.is(FluidTags.LAVA);

            // A lava source solidifies just as it does when touched by water.
            if (selfIsLava && self.isSource() && neighborIsSyrup) {
                xice$replaceAndFizz(level, pos, Blocks.OBSIDIAN.defaultBlockState());
                cir.setReturnValue(false);
                return;
            }

            // Flowing syrup is the consumed side when it reaches lava.
            if (selfIsSyrup && !self.isSource() && neighborIsLava) {
                xice$replaceAndFizz(level, pos, Blocks.GLOWSTONE.defaultBlockState());
                cir.setReturnValue(false);
                return;
            }

            // A syrup source meeting any other non-lava fluid becomes stone.
            if (selfIsSyrup && self.isSource() && !neighborIsSyrup && !neighborIsLava) {
                xice$replaceAndFizz(level, pos, Blocks.STONE.defaultBlockState());
                cir.setReturnValue(false);
                return;
            }

            // A flowing non-lava fluid is consumed when it reaches syrup.
            if (!selfIsSyrup && !selfIsLava && !self.isSource() && neighborIsSyrup) {
                xice$replaceAndFizz(level, pos, Blocks.WET_SPONGE.defaultBlockState());
                cir.setReturnValue(false);
                return;
            }
        }
    }

    private static boolean xice$isTorchberrySyrup(FluidState state) {
        ResourceLocation id = BuiltInRegistries.FLUID.getKey(state.getType());
        return SYRUP_NAMESPACE.equals(id.getNamespace()) && id.getPath().endsWith(SYRUP_PATH);
    }

    private static void xice$replaceAndFizz(Level level, BlockPos pos, BlockState replacement) {
        level.setBlockAndUpdate(pos, replacement);
        level.levelEvent(FIZZ_EVENT, pos, 0);
    }
}
