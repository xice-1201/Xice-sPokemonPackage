package cn.cnxice.smartphoneprogression;

import com.mojang.logging.LogUtils;
import com.nbp.cobblemon_smartphone.upgrade.SmartphoneUpgrade;
import com.nbp.cobblemon_smartphone.upgrade.SmartphoneUpgradeRegistry;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SmartphoneProgression.MOD_ID)
public final class SmartphoneProgression {
    public static final String MOD_ID = "smartphoneprogression";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String HEAL = "upgrade_heal";
    public static final String PC = "upgrade_pc";
    public static final String POKEDEX = "upgrade_pokedex";
    public static final String ENDER = "upgrade_ender";
    public static final String SCANNER = "upgrade_scanner";

    public SmartphoneProgression() {
        registerUpgrade("heal", HEAL);
        registerUpgrade("pc", PC);
        registerUpgrade("pokedex", POKEDEX);
        registerUpgrade("ender", ENDER);
        registerUpgrade("scanner", SCANNER);
        NeoForge.EVENT_BUS.addListener(TeraPouchPickupHandler::onPickup);
        LOGGER.info("Smartphone Progression loaded with per-device utility modules");
    }

    private static void registerUpgrade(String id, String nbtKey) {
        SmartphoneUpgradeRegistry.INSTANCE.register(new SmartphoneUpgrade(
                MOD_ID + ":" + id,
                nbtKey,
                null,
                Component.translatable("upgrade." + MOD_ID + "." + id)
        ));
    }
}
