package cn.cnxice.cobblemonfix;

import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.MenuProvider;

/** Runtime bridge implemented by the WorkForce miner mixin. */
public interface WorkForceMinerAccess extends WorldlyContainer, MenuProvider {
    boolean xice$logisticsEnabled();
}
