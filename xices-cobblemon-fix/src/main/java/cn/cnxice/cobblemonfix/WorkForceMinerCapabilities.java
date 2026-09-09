package cn.cnxice.cobblemonfix;

import com.nbp.cobblemon_workforce.registry.ModRegistry;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public final class WorkForceMinerCapabilities {
    private WorkForceMinerCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModRegistry.INSTANCE.getPOKE_MINER_BLOCK_ENTITY().get(),
                (blockEntity, side) -> new SidedInvWrapper(
                        (WorkForceMinerAccess) (Object) blockEntity,
                        side == null ? Direction.DOWN : side
                )
        );
    }
}
