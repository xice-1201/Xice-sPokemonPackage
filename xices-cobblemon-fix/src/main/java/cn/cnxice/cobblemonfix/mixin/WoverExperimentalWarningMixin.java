package cn.cnxice.cobblemonfix.mixin;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.dimension.LevelStem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Skips the vanilla experimental-world confirmation when it is caused only by
 * WorldWeaver's custom Nether/End generators. Real vanilla experimental feature
 * flags still retain their confirmation screen.
 */
@Mixin(CreateWorldScreen.class)
public abstract class WoverExperimentalWarningMixin {
    private static final String WOVER_PACKAGE = "org.betterx.wover.";

    @Shadow
    @Final
    WorldCreationUiState uiState;

    @ModifyArg(
            method = "onCreate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;confirmWorldCreation(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;Lcom/mojang/serialization/Lifecycle;Ljava/lang/Runnable;Z)V"
            ),
            index = 4
    )
    private boolean xice$autoConfirmWoverWorld(boolean alreadyConfirmed) {
        if (alreadyConfirmed) {
            return true;
        }

        WorldCreationContext context = this.uiState.getSettings();
        if (FeatureFlags.isExperimental(context.dataConfiguration().enabledFeatures())) {
            return false;
        }

        return context.selectedDimensions().dimensions().values().stream().anyMatch(WoverExperimentalWarningMixin::xice$isWoverStem);
    }

    private static boolean xice$isWoverStem(LevelStem stem) {
        return stem.generator().getClass().getName().startsWith(WOVER_PACKAGE)
                || stem.generator().getBiomeSource().getClass().getName().startsWith(WOVER_PACKAGE);
    }
}
