package cn.cnxice.cobblemonfix;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.List;

public final class TradeListItem extends Item {
    public static final String RECORDED = "Recorded";
    public static final String INPUT_A = "InputA";
    public static final String INPUT_B = "InputB";
    public static final String OUTPUT = "Output";

    public TradeListItem(Properties properties) { super(properties.stacksTo(1)); }

    @Override public boolean isFoil(ItemStack stack) { return isRecorded(stack); }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip,
                                net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (!isRecorded(stack)) return;
        CompoundTag tag = customTag(stack);
        ItemStack inputA = ItemStack.parse(context.registries(), tag.getCompound(INPUT_A)).orElse(ItemStack.EMPTY);
        ItemStack inputB = tag.contains(INPUT_B)
                ? ItemStack.parse(context.registries(), tag.getCompound(INPUT_B)).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        ItemStack output = ItemStack.parse(context.registries(), tag.getCompound(OUTPUT)).orElse(ItemStack.EMPTY);
        if (!inputA.isEmpty()) tooltip.add(Component.translatable(
                "item.xices_cobblemon_fix.trade_list.input", formatStack(inputA)).withStyle(ChatFormatting.GOLD));
        if (!inputB.isEmpty()) tooltip.add(Component.translatable(
                "item.xices_cobblemon_fix.trade_list.input", formatStack(inputB)).withStyle(ChatFormatting.GOLD));
        if (!output.isEmpty()) tooltip.add(Component.translatable(
                "item.xices_cobblemon_fix.trade_list.output", formatStack(output)).withStyle(ChatFormatting.GOLD));
    }

    private static Component formatStack(ItemStack stack) {
        return Component.literal(stack.getCount() + "×").append(stack.getHoverName());
    }

    public static boolean isRecorded(ItemStack stack) {
        CompoundTag tag = customTag(stack);
        return tag.getBoolean(RECORDED) && tag.contains(INPUT_A) && tag.contains(OUTPUT);
    }

    public static CompoundTag customTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    public static void setRecorded(ItemStack stack, CompoundTag inputA, CompoundTag inputB, CompoundTag output) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(RECORDED, true);
        tag.put(INPUT_A, inputA.copy());
        if (inputB != null && !inputB.isEmpty()) tag.put(INPUT_B, inputB.copy());
        tag.put(OUTPUT, output.copy());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void clear(ItemStack stack) { stack.remove(DataComponents.CUSTOM_DATA); }
}
