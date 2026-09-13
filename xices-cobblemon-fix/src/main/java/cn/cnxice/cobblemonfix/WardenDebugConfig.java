package cn.cnxice.cobblemonfix;
import net.neoforged.neoforge.common.ModConfigSpec;
public final class WardenDebugConfig { public static final ModConfigSpec SPEC; public static final ModConfigSpec.BooleanValue ENABLED; static { var b=new ModConfigSpec.Builder(); ENABLED=b.comment("输出监守者对战调试日志").define("warden_battle_debug_log", true); SPEC=b.build(); } }
