package cn.cnxice.cobblemonfix.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.entity.projectile.ThrownTrident")
public interface ThrownTridentAccessor {
    @Shadow ItemStack getPickupItem();
}
