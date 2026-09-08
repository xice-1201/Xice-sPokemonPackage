package cn.cnxice.smartphoneprogression;

import com.nbp.cobblemon_smartphone.upgrade.SmartphoneUpgradeHelperKt;
import com.nbp.cobblemon_smartphone.util.SmartphoneHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ProgressionChecks {
    private ProgressionChecks() {
    }

    public static boolean contextPhoneHas(String upgradeKey) {
        ItemStack stack = SmartphoneHelper.INSTANCE.getContextSmartphone();
        return stack != null && !stack.isEmpty() && SmartphoneUpgradeHelperKt.hasUpgrade(stack, upgradeKey);
    }

    public static boolean playerHas(String upgradeKey, ServerPlayer player) {
        return SmartphoneHelper.INSTANCE.hasUpgradeOnAnySmartphone(player, upgradeKey, null);
    }
}
