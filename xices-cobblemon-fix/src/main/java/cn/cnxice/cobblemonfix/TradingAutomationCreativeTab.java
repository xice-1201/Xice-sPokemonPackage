package cn.cnxice.cobblemonfix;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public final class TradingAutomationCreativeTab {
    public static void add(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(TradingAutomationRegistry.PROXY_TRADING_STATION_ITEM.get());
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(TradingAutomationRegistry.TRADE_LIST.get());
        }
    }

    private TradingAutomationCreativeTab() {}
}
