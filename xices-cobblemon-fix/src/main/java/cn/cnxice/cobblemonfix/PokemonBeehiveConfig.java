package cn.cnxice.cobblemonfix;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Server-side tuning values for the independent Pokémon Beehive. */
public final class PokemonBeehiveConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue FLOWER_RADIUS;
    public static final ModConfigSpec.IntValue PRODUCTION_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue BASE_HONEY_MB;
    public static final ModConfigSpec.IntValue HONEY_CAPACITY_MB;
    public static final ModConfigSpec.IntValue BOTTLE_COST_MB;
    public static final ModConfigSpec.IntValue FLOWER_BONUS_PERCENT;
    public static final ModConfigSpec.IntValue MAX_FLOWERS;
    public static final ModConfigSpec.IntValue WORK_RADIUS;
    public static final ModConfigSpec.IntValue WORK_INTERVAL_TICKS;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("pokemon_beehive");
        FLOWER_RADIUS = b.comment("Flower search radius in blocks.").defineInRange("flower_radius", 8, 1, 32);
        PRODUCTION_INTERVAL_TICKS = b.comment("Base ticks between production cycles.").defineInRange("production_interval_ticks", 40, 20, 72000);
        BASE_HONEY_MB = b.comment("Legacy base value; bee-specific production values are used.").defineInRange("base_honey_mb", 0, 0, 1000);
        HONEY_CAPACITY_MB = b.comment("Internal honey capacity, in mB.").defineInRange("honey_capacity_mb", 8000, 250, 100000);
        BOTTLE_COST_MB = b.comment("Honey consumed per bottle conversion, in mB.").defineInRange("bottle_cost_mb", 250, 1, 1000);
        FLOWER_BONUS_PERCENT = b.comment("Legacy value; flowers now reduce the interval by one tick each.").defineInRange("flower_bonus_percent", 0, 0, 100);
        MAX_FLOWERS = b.comment("Legacy value; all flowers in range are counted.").defineInRange("max_flowers", 1024, 0, 4096);
        b.push("proxy_trading_station");
        WORK_RADIUS = b.comment("Radius in blocks in which an owned Indeedee family Pokemon enables the station.")
                .defineInRange("work_radius", 16, 1, 128);
        WORK_INTERVAL_TICKS = b.comment("Ticks between proxy trading station operations.")
                .defineInRange("work_interval_ticks", 10, 1, 72000);
        b.pop();
        b.pop();
        SPEC = b.build();
    }

    private PokemonBeehiveConfig() {}
}
