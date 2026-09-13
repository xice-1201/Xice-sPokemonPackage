package cn.cnxice.cobblemonfix;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

/** Exposes the station cache through NeoForge's standard item capability used by Create logistics. */
public final class TradingAutomationCapabilities {
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                TradingAutomationRegistry.PROXY_TRADING_STATION_BLOCK_ENTITY.get(),
                (station, side) -> new SidedInvWrapper(
                        station,
                        side == null ? Direction.DOWN : side
                )
        );
    }

    private TradingAutomationCapabilities() {}
}
