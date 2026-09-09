package cn.cnxice.cobblemonfix;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Internal carrier items used only by FTB Quests item-filter tasks. */
public final class QuestFilterItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(XicesCobblemonFix.MOD_ID);

    public static final DeferredItem<Item> BOTANY_LOGS = ITEMS.registerSimpleItem(
            "quest_botany_logs",
            new Item.Properties()
    );
    public static final DeferredItem<Item> BOTANY_SAPLINGS = ITEMS.registerSimpleItem(
            "quest_botany_saplings",
            new Item.Properties()
    );
    public static final DeferredItem<Item> BRUSHES = ITEMS.registerSimpleItem(
            "quest_brushes",
            new Item.Properties()
    );

    private QuestFilterItems() {
    }
}
