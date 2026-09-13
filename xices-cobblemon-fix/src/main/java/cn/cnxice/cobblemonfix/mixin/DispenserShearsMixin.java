package cn.cnxice.cobblemonfix.mixin;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Prevent dispensers from using shears to harvest honeycomb. */
@Mixin(DispenserBlock.class)
public abstract class DispenserShearsMixin {
    @Inject(method = "dispenseFrom", at = @At("HEAD"), cancellable = true)
    private void xice$disableShears(ServerLevel level, BlockState state, BlockPos pos, CallbackInfo ci) {
        Direction facing = state.getValue(DispenserBlock.FACING);
        var target = level.getBlockState(pos.relative(facing));
        if ((target.is(Blocks.BEE_NEST) || target.is(Blocks.BEEHIVE))
                && target.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
            if (level.getBlockEntity(pos) instanceof DispenserBlockEntity dispenser) {
                for (int i = 0; i < dispenser.getContainerSize(); i++) {
                    if (dispenser.getItem(i).is(Items.SHEARS)) {
                        ci.cancel();
                        return;
                    }
                }
            }
        }
    }
}
