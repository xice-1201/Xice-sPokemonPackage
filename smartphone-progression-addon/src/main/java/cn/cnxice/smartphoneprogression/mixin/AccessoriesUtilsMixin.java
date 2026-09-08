package cn.cnxice.smartphoneprogression.mixin;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

/** Keeps Mega Showdown gimmick detection working after its wearable items move to Curios. */
@Mixin(targets = "com.github.yajatkaul.mega_showdown.utils.AccessoriesUtils", remap = false)
public abstract class AccessoriesUtilsMixin {
    @Inject(method = "checkTagInAccessories", at = @At("HEAD"), cancellable = true, remap = false)
    private static void smartphoneprogression$checkCurios(LivingEntity entity, TagKey<Item> tag,
                                                           CallbackInfoReturnable<Boolean> cir) {
        if (CuriosApi.getCuriosInventory(entity)
                .flatMap(inventory -> inventory.findFirstCurio(stack -> stack.is(tag)))
                .isPresent()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "findFirstItemWithTag", at = @At("HEAD"), cancellable = true, remap = false)
    private static void smartphoneprogression$findInCurios(LivingEntity entity, TagKey<Item> tag,
                                                            CallbackInfoReturnable<ItemStack> cir) {
        CuriosApi.getCuriosInventory(entity)
                .flatMap(inventory -> inventory.findFirstCurio(stack -> stack.is(tag)))
                .ifPresent(result -> cir.setReturnValue(result.stack()));
    }
}
