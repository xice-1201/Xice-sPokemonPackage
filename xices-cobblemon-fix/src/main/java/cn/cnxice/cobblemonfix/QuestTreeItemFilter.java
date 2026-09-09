package cn.cnxice.cobblemonfix;

import dev.ftb.mods.ftbquests.api.ItemFilterAdapter;
import dev.ftb.mods.ftbquests.integration.item_filtering.ItemMatchingSystem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Supplies two unobtainable carrier stacks for FTB Quests item tasks.
 * The task UI expands them into every matching Botany Pots tree item, while
 * inventory checks accept any one member of the corresponding item tag.
 */
public final class QuestTreeItemFilter implements ItemFilterAdapter {
    private static final QuestTreeItemFilter INSTANCE = new QuestTreeItemFilter();
    private static final TagKey<Item> BOTANY_LOGS = tag("quest_botany_logs");
    private static final TagKey<Item> BOTANY_SAPLINGS = tag("quest_botany_saplings");
    private static final TagKey<Item> BRUSHES = tag("brushes");

    private QuestTreeItemFilter() {
    }

    public static void register() {
        ItemMatchingSystem.INSTANCE.registerFilterAdapter(INSTANCE);
    }

    private static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(XicesCobblemonFix.MOD_ID, path));
    }

    @Override
    public String getName() {
        return "xices_botany_tree_quest_filter";
    }

    @Override
    public boolean isFilterStack(ItemStack stack) {
        return stack.is(QuestFilterItems.BOTANY_LOGS.get())
                || stack.is(QuestFilterItems.BOTANY_SAPLINGS.get())
                || stack.is(QuestFilterItems.BRUSHES.get());
    }

    @Override
    public boolean doesItemMatch(ItemStack filterStack, ItemStack candidate, HolderLookup.Provider lookup) {
        return getMatcher(filterStack, lookup).test(candidate);
    }

    @Override
    public Matcher getMatcher(ItemStack filterStack, HolderLookup.Provider lookup) {
        if (filterStack.is(QuestFilterItems.BOTANY_LOGS.get())) {
            return stack -> stack.is(BOTANY_LOGS);
        }
        if (filterStack.is(QuestFilterItems.BOTANY_SAPLINGS.get())) {
            return stack -> stack.is(BOTANY_SAPLINGS);
        }
        if (filterStack.is(QuestFilterItems.BRUSHES.get())) {
            return stack -> stack.is(BRUSHES);
        }
        return NO_MATCH;
    }

    @Override
    public boolean hasItemTagFilter() {
        return false;
    }

    @Override
    public ItemStack makeTagFilterStack(TagKey<Item> tag) {
        return ItemStack.EMPTY;
    }
}
