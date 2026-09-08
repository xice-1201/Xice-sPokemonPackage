package cn.cnxice.smartphoneprogression.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    private static final ResourceLocation EXP_ALL_ID =
            ResourceLocation.fromNamespaceAndPath("cobblemonexpall", "exp_all");

    @Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
    private void smartphoneprogression$rejectExpAllAsHelmet(ItemStack stack,
                                                            CallbackInfoReturnable<EquipmentSlot> cir) {
        if (EXP_ALL_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()))) {
            cir.setReturnValue(EquipmentSlot.MAINHAND);
        }
    }
}
