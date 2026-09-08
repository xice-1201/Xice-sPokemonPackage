package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.gui.ProfileTransformType;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.arcaryx.cobblemonintegrations.jei.DropsJeiCategory", remap = false)
public abstract class DropsJeiCategoryMixin {
    private static final int DEFAULT_POSE_TYPE_BIT = 1 << 3;
    private static final int DEFAULT_VERTICAL_OFFSET_BIT = 1 << 6;
    private static final int DEFAULT_PROFILE_TRANSFORM_BIT = 1 << 7;
    private static final int DEFAULT_ANIMATE_BIT = 1 << 8;
    private static final int DEFAULT_RED_BIT = 1 << 9;
    private static final int DEFAULT_GREEN_BIT = 1 << 10;
    private static final int DEFAULT_BLUE_BIT = 1 << 11;
    private static final int DEFAULT_ALPHA_BIT = 1 << 12;
    private static final int DEFAULT_X_ROTATION_BIT = 1 << 13;
    private static final int DEFAULT_Y_ROTATION_BIT = 1 << 14;

    @Redirect(
            method = "draw",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/cobblemon/mod/common/client/gui/PokemonGuiUtilsKt;drawProfilePokemon$default(Lcom/cobblemon/mod/common/pokemon/RenderablePokemon;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Quaternionf;Lcom/cobblemon/mod/common/entity/PoseType;Lcom/cobblemon/mod/common/client/render/models/blockbench/PosableState;FFZZFFFFFFILjava/lang/Object;)V",
                    remap = false
            )
    )
    private void xicesFix$drawProfilePokemon(
            RenderablePokemon pokemon,
            PoseStack poseStack,
            Quaternionf rotation,
            PoseType poseType,
            PosableState state,
            float scale,
            float verticalOffset,
            boolean useProfileTransform,
            boolean animate,
            float red,
            float green,
            float blue,
            float alpha,
            float xRotation,
            float yRotation,
            int oldDefaultMask,
            Object marker
    ) {
        PoseType resolvedPoseType = (oldDefaultMask & DEFAULT_POSE_TYPE_BIT) != 0
                ? PoseType.PROFILE
                : poseType;
        float resolvedVerticalOffset = (oldDefaultMask & DEFAULT_VERTICAL_OFFSET_BIT) != 0
                ? 20.0F
                : verticalOffset;
        ProfileTransformType resolvedTransformType = (oldDefaultMask & DEFAULT_PROFILE_TRANSFORM_BIT) != 0
                ? ProfileTransformType.PROFILE
                : (useProfileTransform ? ProfileTransformType.PROFILE : ProfileTransformType.NONE);
        boolean resolvedAnimate = (oldDefaultMask & DEFAULT_ANIMATE_BIT) != 0 ? false : animate;
        float resolvedRed = (oldDefaultMask & DEFAULT_RED_BIT) != 0 ? 1.0F : red;
        float resolvedGreen = (oldDefaultMask & DEFAULT_GREEN_BIT) != 0 ? 1.0F : green;
        float resolvedBlue = (oldDefaultMask & DEFAULT_BLUE_BIT) != 0 ? 1.0F : blue;
        float resolvedAlpha = (oldDefaultMask & DEFAULT_ALPHA_BIT) != 0 ? 1.0F : alpha;
        float resolvedXRotation = (oldDefaultMask & DEFAULT_X_ROTATION_BIT) != 0 ? 0.0F : xRotation;
        float resolvedYRotation = (oldDefaultMask & DEFAULT_Y_ROTATION_BIT) != 0 ? 0.0F : yRotation;

        PokemonGuiUtilsKt.drawProfilePokemon(
                pokemon,
                poseStack,
                rotation,
                resolvedPoseType,
                state,
                scale,
                resolvedVerticalOffset,
                resolvedTransformType,
                resolvedAnimate,
                resolvedRed,
                resolvedGreen,
                resolvedBlue,
                resolvedAlpha,
                resolvedXRotation,
                resolvedYRotation,
                13
        );
    }
}
