package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Applies type-specific fire duration to the actual vanilla Entity method. */
@Mixin(Entity.class)
public abstract class PokemonFireDurationMixin {
    @ModifyVariable(method = "setRemainingFireTicks", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int xice$adjustFireDuration(int ticks) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof PokemonEntity pokemonEntity)) return ticks;
        Pokemon pokemon = pokemonEntity.getPokemon();
        int adjusted = ticks;
        if (hasType(pokemon, "grass")) adjusted = Math.max(0, adjusted * 2);
        if (hasType(pokemon, "water")) adjusted = Math.max(0, (adjusted + 1) / 2);
        if (hasType(pokemon, "steel")) adjusted = Math.max(0, (adjusted + 1) / 2);
        return adjusted;
    }

    private static boolean hasType(Pokemon pokemon, String id) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) {
            if (id.equalsIgnoreCase(type.getShowdownId())) return true;
        }
        return false;
    }
}
