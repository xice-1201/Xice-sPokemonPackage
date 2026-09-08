package cn.cnxice.smartphoneprogression.mixin;

import net.axelwf.cobblemonexpall.service.ExpAllEquipResolver;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(value = ExpAllEquipResolver.class, remap = false)
public abstract class ExpAllEquipResolverMixin {
    private static final ResourceLocation EXP_ALL_ID =
            ResourceLocation.fromNamespaceAndPath("cobblemonexpall", "exp_all");

    @Inject(method = "isExpAllEquipped", at = @At("HEAD"), cancellable = true, remap = false)
    private static void smartphoneprogression$onlyCountHatSlot(ServerPlayer player,
                                                               CallbackInfoReturnable<Boolean> cir) {
        boolean equippedInHat = CuriosApi.getCuriosInventory(player)
                .flatMap(inventory -> inventory.findFirstCurio(
                        stack -> EXP_ALL_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem())), "hat"))
                .isPresent();
        cir.setReturnValue(equippedInHat);
    }
}
