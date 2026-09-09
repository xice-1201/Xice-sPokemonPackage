package cn.cnxice.cobblemonfix.mixin;

import com.simibubi.create.compat.jei.category.SequencedAssemblyCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** Expands Create's generic "random scrap" tooltip into the normalized byproduct pool. */
@Mixin(value = SequencedAssemblyCategory.class, remap = false)
public abstract class SequencedAssemblyCategoryMixin {
    private static final int SCRAP_LEFT = 143;
    private static final int SCRAP_RIGHT = 161;
    private static final int SCRAP_TOP = 90;
    private static final int SCRAP_BOTTOM = 108;

    @Inject(method = "getTooltipStrings(Lcom/simibubi/create/content/processing/sequenced/SequencedAssemblyRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;DD)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private void xice$showEveryWeightedResult(
            SequencedAssemblyRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY,
            CallbackInfoReturnable<List<Component>> cir
    ) {
        if (recipe.resultPool.size() < 2
                || mouseX < SCRAP_LEFT || mouseX >= SCRAP_RIGHT
                || mouseY < SCRAP_TOP || mouseY >= SCRAP_BOTTOM) {
            return;
        }

        // Create treats the first pool entry as the recipe's primary result. The scrap
        // slot should only explain the alternatives and normalize their relative odds.
        List<ProcessingOutput> byproducts = recipe.resultPool.subList(1, recipe.resultPool.size());
        float totalWeight = 0.0F;
        for (ProcessingOutput output : byproducts) {
            totalWeight += output.getChance();
        }
        if (totalWeight <= 0.0F) {
            return;
        }

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("xices_cobblemon_fix.jei.sequenced_assembly.byproducts")
                .withStyle(ChatFormatting.GOLD));
        for (ProcessingOutput output : byproducts) {
            ItemStack stack = output.getStack();
            String percentage = BigDecimal.valueOf(output.getChance() * 100.0D / totalWeight)
                    .setScale(2, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString();
            tooltip.add(Component.literal("• ")
                    .append(stack.getHoverName().copy().withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" ×" + stack.getCount() + " — " + percentage + "%")
                            .withStyle(ChatFormatting.GRAY)));
        }
        cir.setReturnValue(tooltip);
    }
}
