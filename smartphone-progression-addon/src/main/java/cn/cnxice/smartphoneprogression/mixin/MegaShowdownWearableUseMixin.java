package cn.cnxice.smartphoneprogression.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Map;

/** Routes Mega Showdown's right-click auto-equip into the replacement Curios slots. */
@Mixin(targets = {
        "com.github.yajatkaul.mega_showdown.item.custom.gimmick.MegaBracelet",
        "com.github.yajatkaul.mega_showdown.item.custom.gimmick.DynamaxBand",
        "com.github.yajatkaul.mega_showdown.item.custom.gimmick.TeraOrb",
        "com.github.yajatkaul.mega_showdown.item.custom.gimmick.ZRing",
        "com.github.yajatkaul.mega_showdown.item.custom.tera.LikosPendant"
}, remap = false)
public abstract class MegaShowdownWearableUseMixin {
    private static final Map<ResourceLocation, String> TARGET_SLOTS = Map.ofEntries(
            Map.entry(id("dynamax_band"), "bracelet"),
            Map.entry(id("mega_bracelet"), "bracelet"),
            Map.entry(id("mega_bracelet_red"), "bracelet"),
            Map.entry(id("mega_bracelet_yellow"), "bracelet"),
            Map.entry(id("mega_bracelet_pink"), "bracelet"),
            Map.entry(id("mega_bracelet_green"), "bracelet"),
            Map.entry(id("mega_bracelet_blue"), "bracelet"),
            Map.entry(id("mega_bracelet_black"), "bracelet"),
            Map.entry(id("may_bracelet"), "bracelet"),
            Map.entry(id("brendan_mega_cuff"), "bracelet"),
            Map.entry(id("korrina_glove"), "glove"),
            Map.entry(id("mega_ring"), "ring"),
            Map.entry(id("lysandre_ring"), "ring"),
            Map.entry(id("maxie_glasses"), "glasses"),
            Map.entry(id("archie_anchor"), "head"),
            Map.entry(id("lisia_mega_tiara"), "hat"),
            Map.entry(id("tera_orb"), "portable"),
            Map.entry(id("diantha_mega_charm"), "head"),
            Map.entry(id("zinnia_mega_anklet"), "anklet"),
            Map.entry(id("z_ring"), "bracelet"),
            Map.entry(id("z_ring_black"), "bracelet"),
            Map.entry(id("z_ring_yellow"), "bracelet"),
            Map.entry(id("z_ring_green"), "bracelet"),
            Map.entry(id("z_ring_blue"), "bracelet"),
            Map.entry(id("z_ring_pink"), "bracelet"),
            Map.entry(id("z_ring_red"), "bracelet"),
            Map.entry(id("olivias_z_ring"), "bracelet"),
            Map.entry(id("hapus_z_ring"), "bracelet"),
            Map.entry(id("z_power_ring"), "bracelet"),
            Map.entry(id("olivia_z_power_ring"), "bracelet"),
            Map.entry(id("hapu_z_power_ring"), "bracelet"),
            Map.entry(id("rocket_z_power_ring"), "bracelet"),
            Map.entry(id("gladion_z_power_ring"), "bracelet"),
            Map.entry(id("nanu_z_power_ring"), "bracelet"),
            Map.entry(id("likos_pendant"), "head")
    );

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void smartphoneprogression$routeToCurios(Level level, Player player, InteractionHand hand,
                                                     CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack held = player.getItemInHand(hand);
        String slot = TARGET_SLOTS.get(BuiltInRegistries.ITEM.getKey(held.getItem()));
        if (slot == null) {
            return;
        }

        if (level.isClientSide()) {
            cir.setReturnValue(InteractionResultHolder.success(held));
            return;
        }

        boolean equipped = CuriosApi.getCuriosInventory(player).map(inventory ->
                inventory.getStacksHandler(slot).map(handler -> {
                    for (int index = 0; index < handler.getStacks().getSlots(); index++) {
                        if (handler.getStacks().getStackInSlot(index).isEmpty()) {
                            ItemStack equippedStack = held.copy();
                            equippedStack.setCount(1);
                            inventory.setEquippedCurio(slot, index, equippedStack);
                            held.shrink(1);
                            return true;
                        }
                    }
                    return false;
                }).orElse(false)
        ).orElse(false);

        cir.setReturnValue(equipped
                ? InteractionResultHolder.consume(held)
                : InteractionResultHolder.fail(held));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("mega_showdown", path);
    }
}
