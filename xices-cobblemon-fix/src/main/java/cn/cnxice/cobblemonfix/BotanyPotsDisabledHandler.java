package cn.cnxice.cobblemonfix;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/** Pack-wide enforcement for disabled waxed Botany Pots variants. */
@EventBusSubscriber(modid = XicesCobblemonFix.MOD_ID)
public final class BotanyPotsDisabledHandler {
    private BotanyPotsDisabledHandler() {
    }

    public static void onCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        BuiltInRegistries.ITEM.entrySet().stream()
                .filter(entry -> isWaxedPot(entry.getKey().location()))
                .map(entry -> new ItemStack(entry.getValue()))
                .forEach(stack -> event.remove(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(event.getPlacedBlock().getBlock());
        if (isWaxedPot(id)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockUsed(PlayerInteractEvent.RightClickBlock event) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(event.getLevel().getBlockState(event.getPos()).getBlock());
        if (isWaxedPot(id)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        event.getDrops().removeIf(drop -> isWaxedPot(BuiltInRegistries.ITEM.getKey(drop.getItem().getItem())));
    }

    public static boolean isWaxedPot(ResourceLocation id) {
        return id != null
                && ("botanypots".equals(id.getNamespace()) || "botanypotstiers".equals(id.getNamespace()))
                && id.getPath().contains("_waxed_botany_pot");
    }
}
