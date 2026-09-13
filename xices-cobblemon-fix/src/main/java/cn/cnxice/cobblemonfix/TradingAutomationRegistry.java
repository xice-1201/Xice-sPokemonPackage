package cn.cnxice.cobblemonfix;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public final class TradingAutomationRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(XicesCobblemonFix.MOD_ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(XicesCobblemonFix.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, XicesCobblemonFix.MOD_ID);

    public static final DeferredItem<TradeListItem> TRADE_LIST = ITEMS.register("trade_list",
            () -> new TradeListItem(new Item.Properties()));
    public static final DeferredBlock<ProxyTradingStationBlock> PROXY_TRADING_STATION =
            BLOCKS.register("proxy_trading_station", () -> new ProxyTradingStationBlock(
                    BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD)));
    public static final DeferredItem<BlockItem> PROXY_TRADING_STATION_ITEM = ITEMS.register(
            "proxy_trading_station", () -> new BlockItem(PROXY_TRADING_STATION.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<ProxyTradingStationBlockEntity>> PROXY_TRADING_STATION_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("proxy_trading_station",
                    () -> BlockEntityType.Builder.of(ProxyTradingStationBlockEntity::new,
                            PROXY_TRADING_STATION.get()).build(null));

    private TradingAutomationRegistry() {}
}
