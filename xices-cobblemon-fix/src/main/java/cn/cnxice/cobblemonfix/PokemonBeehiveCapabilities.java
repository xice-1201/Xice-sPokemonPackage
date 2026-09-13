package cn.cnxice.cobblemonfix;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public final class PokemonBeehiveCapabilities {
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                PokemonBeehiveRegistry.POKEMON_BEEHIVE_BLOCK_ENTITY.get(),
                (be, side) -> be.outputFluidHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                PokemonBeehiveRegistry.POKEMON_BEEHIVE_BLOCK_ENTITY.get(),
                (be, side) -> new SidedInvWrapper(be, side == null ? Direction.DOWN : side));
    }
    private PokemonBeehiveCapabilities() {}
}
