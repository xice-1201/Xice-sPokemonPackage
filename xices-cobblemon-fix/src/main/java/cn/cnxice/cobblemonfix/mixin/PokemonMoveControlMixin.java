package cn.cnxice.cobblemonfix.mixin;

import cn.cnxice.cobblemonfix.BattleAttributeConfig;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Replaces Cobblemon's species-speed multiplication with a normalized target speed. */
@Mixin(targets = "com.cobblemon.mod.common.entity.pokemon.ai.PokemonMoveControl")
public abstract class PokemonMoveControlMixin {
    @Shadow @Final private PokemonEntity pokemonEntity;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setSpeed(F)V", ordinal = 0), index = 0)
    private float xice$normalizeStrafeSpeed(float ignored) { return xice$normalizedSpeed(); }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setSpeed(F)V", ordinal = 2), index = 0)
    private float xice$normalizeMoveSpeed(float ignored) { return xice$normalizedSpeed(); }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setSpeed(F)V", ordinal = 3), index = 0)
    private float xice$normalizeFinalSpeed(float ignored) { return xice$normalizedSpeed(); }

    private float xice$normalizedSpeed() {
        int speed = Math.max(1, pokemonEntity.getPokemon().getSpeed());
        double coefficient = Math.max(BattleAttributeConfig.SPEED_MIN.get(),
                Math.min(BattleAttributeConfig.SPEED_MAX.get(), 0.50 + speed / BattleAttributeConfig.SPEED_SCALE.get()));
        double target = 0.2 * 0.9 * coefficient;
        if (pokemonEntity.isFlying() || pokemonEntity.isInLiquid()) target *= 1.2;
        return (float) target;
    }
}
