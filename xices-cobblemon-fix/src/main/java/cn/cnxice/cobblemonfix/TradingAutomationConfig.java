package cn.cnxice.cobblemonfix;

public final class TradingAutomationConfig {
    public static final net.neoforged.neoforge.common.ModConfigSpec.IntValue WORK_RADIUS;
    public static final net.neoforged.neoforge.common.ModConfigSpec.IntValue WORK_INTERVAL_TICKS;

    static {
        WORK_RADIUS = PokemonBeehiveConfig.WORK_RADIUS;
        WORK_INTERVAL_TICKS = PokemonBeehiveConfig.WORK_INTERVAL_TICKS;
    }

    private TradingAutomationConfig() {}
}
