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
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Set;

/** Enforces the disablement even for machines already present in an existing world. */
@EventBusSubscriber(modid = XicesCobblemonFix.MOD_ID)
public final class CreateAgesEconomyDisabledHandler {
    private static final Set<ResourceLocation> DISABLED_BLOCKS = Set.of(
            xice$createAges("package_assembler"),
            xice$createAges("port_station"),
            xice$createAges("shop")
    );
    private static final Set<ResourceLocation> DISABLED_ITEMS = Set.of(
            xice$createAges("package_assembler"),
            xice$createAges("port_station"),
            xice$createAges("shop"),
            xice$createAges("andesite_package"),
            xice$createAges("supply_package"),
            xice$createAges("machine_package"),
            xice$createAges("resource_bundle"),
            xice$createAges("mechanism_bundle"),
            xice$createAges("rarity_bundle"),
            xice$createAges("ae2_bundle")
    );

    private CreateAgesEconomyDisabledHandler() {
    }

    public static void onCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        DISABLED_ITEMS.stream()
                .map(BuiltInRegistries.ITEM::get)
                .map(ItemStack::new)
                .forEach(stack -> event.remove(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(event.getPlacedBlock().getBlock());
        if (DISABLED_BLOCKS.contains(id)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockUsed(PlayerInteractEvent.RightClickBlock event) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(event.getLevel().getBlockState(event.getPos()).getBlock());
        if (DISABLED_BLOCKS.contains(id)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    private static ResourceLocation xice$createAges(String path) {
        return ResourceLocation.fromNamespaceAndPath("createages", path);
    }
}
