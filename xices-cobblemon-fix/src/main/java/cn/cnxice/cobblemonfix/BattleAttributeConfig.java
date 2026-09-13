package cn.cnxice.cobblemonfix;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Server-side balance knobs for the Pokémon -> Minecraft attribute bridge. */
public final class BattleAttributeConfig {
    public static final ModConfigSpec.BooleanValue WARDEN_BATTLE_DEBUG_LOG;
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue HP_SCALE;
    public static final ModConfigSpec.DoubleValue HP_MIN;
    public static final ModConfigSpec.DoubleValue HP_MAX;
    public static final ModConfigSpec.DoubleValue ATTACK_SCALE;
    public static final ModConfigSpec.DoubleValue ATTACK_MIN;
    public static final ModConfigSpec.DoubleValue ATTACK_MAX;
    public static final ModConfigSpec.DoubleValue SPECIAL_DEFENCE_MAGIC_SCALE;
    public static final ModConfigSpec.DoubleValue SPEED_SCALE;
    public static final ModConfigSpec.DoubleValue SPEED_MIN;
    public static final ModConfigSpec.DoubleValue SPEED_MAX;
    public static final ModConfigSpec.DoubleValue ATTACK_INTERVAL_MIN_TICKS;
    public static final ModConfigSpec.DoubleValue ATTACK_INTERVAL_MAX_TICKS;
    public static final ModConfigSpec.DoubleValue ATTACK_INTERVAL_SCALE;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        WARDEN_BATTLE_DEBUG_LOG = b.comment("输出监守者对战调试日志").define("warden_battle_debug_log", true);
        b.comment("Pokémon battle stats are compressed before entering Minecraft's attribute system.").push("pokemon_attribute_mapping");
        HP_SCALE = b.comment("Max health = clamp(4 + sqrt(actual HP) * hp_scale, hp_min, hp_max).").defineInRange("hp_scale", 2.0, 0.0, 20.0);
        HP_MIN = b.defineInRange("hp_min", 4.0, 1.0, 1000.0);
        HP_MAX = b.defineInRange("hp_max", 80.0, 1.0, 1000.0);
        ATTACK_SCALE = b.comment("Attack damage = clamp(1 + actual Attack / attack_scale, attack_min, attack_max). 32/18 keeps elite attackers near Sharpness V netherite sword and iron golem damage.").defineInRange("attack_scale", 32.0, 1.0, 1000.0);
        ATTACK_MIN = b.defineInRange("attack_min", 1.0, 0.0, 1000.0);
        ATTACK_MAX = b.defineInRange("attack_max", 18.0, 0.0, 1000.0);
        SPECIAL_DEFENCE_MAGIC_SCALE = b.comment("Magic damage multiplier = 1 / (1 + actual Special Defence / this value). Around 500 Special Defence gives about 85% reduction, with no hard stat cap.").defineInRange("special_defence_magic_scale", 90.0, 1.0, 10000.0);
        SPEED_SCALE = b.comment("Normalized walk coefficient = 0.9 * clamp(0.50 + actual Speed / speed_scale, speed_min, speed_max). Swimming and flying use 1.2x this coefficient.").defineInRange("speed_scale", 250.0, 1.0, 10000.0);
        SPEED_MIN = b.defineInRange("speed_min", 0.50, 0.01, 2.0);
        SPEED_MAX = b.defineInRange("speed_max", 2.0, 0.01, 8.0);
        ATTACK_INTERVAL_SCALE = b.defineInRange("attack_interval_scale", 5000.0, 1.0, 10000.0);
        ATTACK_INTERVAL_MIN_TICKS = b.comment("Derived attack cooldown in ticks = clamp(attack_interval_scale / actual Speed, min, max). Extreme speed is about 10 ticks.").defineInRange("attack_interval_min_ticks", 10.0, 1.0, 200.0);
        ATTACK_INTERVAL_MAX_TICKS = b.defineInRange("attack_interval_max_ticks", 30.0, 1.0, 200.0);
        b.pop();
        SPEC = b.build();
    }

    private BattleAttributeConfig() {}
}
