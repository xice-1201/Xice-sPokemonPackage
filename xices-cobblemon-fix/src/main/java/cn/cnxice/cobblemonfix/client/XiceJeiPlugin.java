package cn.cnxice.cobblemonfix.client;

import cn.cnxice.cobblemonfix.XicesCobblemonFix;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@JeiPlugin
public final class XiceJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, "jei_plugin");
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

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        List<ItemStack> hidden = new ArrayList<>(DISABLED_ITEMS.stream()
                .map(BuiltInRegistries.ITEM::get)
                .filter(item -> item != Items.AIR)
                .map(ItemStack::new)
                .toList());
        BuiltInRegistries.ITEM.entrySet().stream()
                .filter(entry -> xice$isWaxedBotanyPot(entry.getKey().location()))
                .map(entry -> new ItemStack(entry.getValue()))
                .forEach(hidden::add);
        jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hidden);
    }

    private static boolean xice$isWaxedBotanyPot(ResourceLocation id) {
        return ("botanypots".equals(id.getNamespace()) || "botanypotstiers".equals(id.getNamespace()))
                && id.getPath().contains("_waxed_botany_pot");
    }

    private static ResourceLocation xice$createAges(String path) {
        return ResourceLocation.fromNamespaceAndPath("createages", path);
    }
}
