package cn.cnxice.cobblemonfix;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Translation table kept outside the Component interface mixin. */
public final class WorkForceTranslations {
    private static final Map<String, String> EXACT = Map.ofEntries(
            Map.entry("This Poke Power Generator already has the maximum number of Pokemon slots.", "该宝可梦能源发生器的宝可梦槽位已达到上限。"),
            Map.entry("Poke Miner unlocked its Normal Pokemon slot.", "宝可梦挖掘机已解锁物流槽位。"),
            Map.entry("This Poke Miner already has its Normal Pokemon slot unlocked.", "该宝可梦挖掘机已解锁物流槽位。"),
            Map.entry("Poke Crusher unlocked its Normal Pokemon slot.", "宝可梦粉碎机已解锁一般属性宝可梦槽位。"),
            Map.entry("This Poke Crusher already has its Normal Pokemon slot unlocked.", "该宝可梦粉碎机已解锁一般属性宝可梦槽位。"),
            Map.entry("This Poke Power Generator is full.", "该宝可梦能源发生器已满。"),
            Map.entry("Only Electric-type Pokemon can power this generator.", "只有电属性宝可梦可以为该发生器供能。"),
            Map.entry("Fainted Pokemon cannot power this generator.", "濒死的宝可梦无法为该发生器供能。"),
            Map.entry("Only Fairy-type Pokemon can help this breeder.", "只有妖精属性宝可梦可以协助该培育设备。"),
            Map.entry("Fainted Pokemon cannot help this breeder.", "濒死的宝可梦无法协助该培育设备。"),
            Map.entry("This Poke Breeder is full.", "该宝可梦培育设备已满。"),
            Map.entry("Only Steel-type and Normal-type Pokemon can help this crusher.", "只有钢属性和一般属性宝可梦可以协助该粉碎机。"),
            Map.entry("Fainted Pokemon cannot power this crusher.", "濒死的宝可梦无法驱动该粉碎机。"),
            Map.entry("This Poke Crusher does not have an available slot for that Pokemon type.", "该宝可梦粉碎机没有可供此属性宝可梦使用的槽位。"),
            Map.entry("This Poke Crusher is full.", "该宝可梦粉碎机已满。"),
            Map.entry("Only Water-type, Grass-type, and Normal-type Pokemon can help this farm assistant.", "只有水属性、草属性和一般属性宝可梦可以协助该农场。"),
            Map.entry("Fainted Pokemon cannot help this farm assistant.", "濒死的宝可梦无法协助该农场。"),
            Map.entry("This Farm Assistant does not have an available slot for that Pokemon type.", "该宝可梦农场没有可供此属性宝可梦使用的槽位。"),
            Map.entry("This Farm Assistant is full.", "该宝可梦农场已满。"),
            Map.entry("Only Fire-type Pokemon can heat this furnace.", "只有火属性宝可梦可以为该熔炉供热。"),
            Map.entry("Fainted Pokemon cannot heat this furnace.", "濒死的宝可梦无法为该熔炉供热。"),
            Map.entry("This Poke Furnace is full.", "该宝可梦熔炉已满。"),
            Map.entry("Only Fighting-type, Normal-type, and Grass-type Pokemon can help this wood assistant.", "只有格斗属性、一般属性和草属性宝可梦可以协助该伐木机。"),
            Map.entry("Fainted Pokemon cannot help this wood assistant.", "濒死的宝可梦无法协助该伐木机。"),
            Map.entry("This Wood Assistant does not have an available slot for that Pokemon type.", "该宝可梦伐木机没有可供此属性宝可梦使用的槽位。"),
            Map.entry("This Wood Assistant is full.", "该宝可梦伐木机已满。"),
            Map.entry("Only Rock-type and Normal-type Pokemon can help this miner.", "主槽可分配任意宝可梦；物流槽不接受幽灵属性宝可梦。"),
            Map.entry("Fainted Pokemon cannot power this miner.", "濒死的宝可梦无法驱动该挖掘机。"),
            Map.entry("This Poke Miner does not have an available slot for that Pokemon type.", "该宝可梦挖掘机没有可供此属性宝可梦使用的槽位。"),
            Map.entry("This Poke Miner is full.", "该宝可梦挖掘机已满。"),
            Map.entry("Only Normal-type Pokemon can power this vacuum.", "只有一般属性宝可梦可以驱动该物品收集器。"),
            Map.entry("Fainted Pokemon cannot power this vacuum.", "濒死的宝可梦无法驱动该物品收集器。"),
            Map.entry("This Poke Vacuum is full.", "该宝可梦物品收集器已满。"),
            Map.entry("That Pokemon is already assigned somewhere else.", "该宝可梦已被分配到其他工作地点。"),
            Map.entry("Combined Attack/Sp. Attack", "攻击与特攻合计")
    );

    private static final Pattern GENERATOR_UPGRADE = Pattern.compile("Poke Power Generator upgraded to (\\d+) Pokemon slots\\.");
    private static final Pattern SLOT_UNLOCKED = Pattern.compile("(Farm Assistant|Wood Assistant) unlocked its (Water|Normal|Plant) Pokemon slot\\.");
    private static final Pattern SLOT_ALREADY = Pattern.compile("This (Farm Assistant|Wood Assistant) already has its (Water|Normal|Plant) Pokemon slot unlocked\\.");
    private static final Pattern PRODUCING = Pattern.compile("(.+) is now producing (\\d+) RF/t from (.+)\\.");
    private static final Pattern ASSIGNED = Pattern.compile("(.+) is now (helping nearby animals breed|crushing nearby ores|helping nearby crops|heating this furnace|helping harvest wood|mining nearby stone|collecting nearby items)\\.");

    private WorkForceTranslations() {
    }

    public static String translate(String text) {
        String exact = EXACT.get(text);
        if (exact != null) return exact;

        Matcher matcher = GENERATOR_UPGRADE.matcher(text);
        if (matcher.matches()) return "宝可梦能源发生器已升级为 " + matcher.group(1) + " 个宝可梦槽位。";

        matcher = SLOT_UNLOCKED.matcher(text);
        if (matcher.matches()) return machine(matcher.group(1)) + "已解锁" + type(matcher.group(2)) + "宝可梦槽位。";

        matcher = SLOT_ALREADY.matcher(text);
        if (matcher.matches()) return "该" + machine(matcher.group(1)) + "已解锁" + type(matcher.group(2)) + "宝可梦槽位。";

        matcher = PRODUCING.matcher(text);
        if (matcher.matches()) return matcher.group(1) + "正在以 " + matcher.group(2) + " RF/t 的速率供能（依据" + stat(matcher.group(3)) + "）。";

        matcher = ASSIGNED.matcher(text);
        if (matcher.matches()) return matcher.group(1) + assignment(matcher.group(2));
        return text;
    }

    private static String machine(String value) {
        return "Farm Assistant".equals(value) ? "宝可梦农场" : "宝可梦伐木机";
    }

    private static String type(String value) {
        return switch (value) {
            case "Water" -> "水属性";
            case "Plant" -> "草属性";
            default -> "一般属性";
        };
    }

    private static String stat(String value) {
        return value.replace("Sp. Attack", "特攻").replace("Attack", "攻击");
    }

    private static String assignment(String value) {
        return switch (value) {
            case "helping nearby animals breed" -> "现在正在协助附近的动物繁殖。";
            case "crushing nearby ores" -> "现在正在粉碎附近的矿物。";
            case "helping nearby crops" -> "现在正在照料附近的作物。";
            case "heating this furnace" -> "现在正在为该熔炉供热。";
            case "helping harvest wood" -> "现在正在协助采伐树木。";
            case "mining nearby stone" -> "已被分配至宝可梦挖掘机。";
            default -> "现在正在收集附近的物品。";
        };
    }
}
