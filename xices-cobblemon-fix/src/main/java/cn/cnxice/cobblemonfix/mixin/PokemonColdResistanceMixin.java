package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Applies ice-type immunity to vanilla Entity cold checks. */
@Mixin(Entity.class)
public abstract class PokemonColdResistanceMixin {
    @Inject(method = "canFreeze", at = @At("HEAD"), cancellable = true)
    private void xice$iceFreezeImmunity(CallbackInfoReturnable<Boolean> cir) {
        if (isIcePokemon()) cir.setReturnValue(false);
    }

    private boolean isIcePokemon() {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof PokemonEntity pokemonEntity)) return false;
        Pokemon pokemon = pokemonEntity.getPokemon();
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) {
            if ("ice".equalsIgnoreCase(type.getShowdownId())) return true;
        }
        return false;
    }
}
