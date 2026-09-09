package cn.cnxice.cobblemonfix.mixin;

import cn.cnxice.cobblemonfix.WorkForceMinerAccess;
import com.nbp.cobblemon_workforce.block.PokeMinerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokeMinerBlock.class)
public abstract class PokeMinerBlockMixin {
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void xice$openOutputOrAssign(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (player.isShiftKeyDown()) {
            return;
        }
        if (!level.isClientSide()) {
            WorkForceMinerAccess menu = xice$findMiner(level, pos);
            if (menu != null) player.openMenu(menu);
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void xice$openOutputWithHeldItem(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit,
            CallbackInfoReturnable<ItemInteractionResult> cir
    ) {
        if (player.isShiftKeyDown()
                || stack.is(com.nbp.cobblemon_workforce.registry.ModRegistry.INSTANCE
                        .getPOKE_FARM_NORMAL_SLOT_UPGRADE().get())) {
            return;
        }
        if (!level.isClientSide()) {
            WorkForceMinerAccess menu = xice$findMiner(level, pos);
            if (menu != null) player.openMenu(menu);
        }
        cir.setReturnValue(ItemInteractionResult.SUCCESS);
    }

    @Unique
    private static WorkForceMinerAccess xice$findMiner(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof WorkForceMinerAccess menu) return menu;
        if (level.getBlockEntity(pos.below()) instanceof WorkForceMinerAccess menu) return menu;
        if (level.getBlockEntity(pos.above()) instanceof WorkForceMinerAccess menu) return menu;
        return null;
    }
}
