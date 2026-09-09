package cn.cnxice.cobblemonfix.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/** Keeps the KubeJS reinforced brush's durability bar but prevents all durability loss. */
@Mixin(ItemStack.class)
public abstract class ReinforcedBrushDurabilityMixin {
    private static final ResourceLocation XICE_REINFORCED_BRUSH =
            ResourceLocation.fromNamespaceAndPath("kubejs", "reinforced_brush");

    @Inject(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void xice$keepReinforcedBrushIntact(
            int amount,
            ServerLevel level,
            LivingEntity entity,
            Consumer<Item> onBreak,
            CallbackInfo ci
    ) {
        ItemStack stack = (ItemStack) (Object) this;
        if (BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(XICE_REINFORCED_BRUSH)) {
            ci.cancel();
        }
    }
}
