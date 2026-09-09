package cn.cnxice.cobblemonfix.client;

import cn.cnxice.cobblemonfix.XicesCobblemonFix;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Set;

@EventBusSubscriber(modid = XicesCobblemonFix.MOD_ID, value = Dist.CLIENT)
public final class DisabledItemTooltipHandler {
    private static final ResourceLocation UNCRAFTING_TABLE =
            ResourceLocation.fromNamespaceAndPath("twilightforest", "uncrafting_table");
    private static final Set<ResourceLocation> DISABLED_ITEMS = Set.of(
            UNCRAFTING_TABLE,
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

    private DisabledItemTooltipHandler() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (DISABLED_ITEMS.contains(itemId) || xice$isWaxedBotanyPot(itemId)) {
            event.getToolTip().add(Component.translatable("tooltip.xices_cobblemon_fix.disabled_item").withStyle(ChatFormatting.RED));
        }
    }

    private static boolean xice$isWaxedBotanyPot(ResourceLocation id) {
        return id != null
                && ("botanypots".equals(id.getNamespace()) || "botanypotstiers".equals(id.getNamespace()))
                && id.getPath().contains("_waxed_botany_pot");
    }

    private static ResourceLocation xice$createAges(String path) {
        return ResourceLocation.fromNamespaceAndPath("createages", path);
    }
}
