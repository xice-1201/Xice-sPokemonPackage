package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.events.pokemon.PokedexDataChangedEvent;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import winterwolfsv.cobblemon_quests.events.CobblemonQuestsEventHandler;

/**
 * Adapts Cobblemon Quests 1.2.0's old ENCOUNTERED/CAUGHT Pokédex states to
 * Cobblemon 1.8.0's SEEN/OWNED states.
 */
@Mixin(value = CobblemonQuestsEventHandler.class, remap = false)
public abstract class CobblemonQuestsPokedexMixin {
    @Inject(method = "pokeDexChanged", at = @At("HEAD"), cancellable = true)
    private void xice$handleCobblemon18PokedexChange(PokedexDataChangedEvent event, CallbackInfo ci) {
        ci.cancel();

        try {
            Pokemon pokemon = event.getDataSource().getPokemon();
            PokedexEntryProgress previousOrCurrent = event.getPokedexManager()
                    .getKnowledgeForSpecies(pokemon.getSpecies().getResourceIdentifier());
            PokedexEntryProgress learned = event.getKnowledge();

            long amount;
            if (learned == PokedexEntryProgress.OWNED) {
                amount = previousOrCurrent == PokedexEntryProgress.SEEN ? 1L : 2L;
            } else if (learned == PokedexEntryProgress.SEEN) {
                amount = 0L;
            } else {
                return;
            }

            LivingEntity owner = pokemon.getOwnerEntity();
            PokemonEntity entity = pokemon.getEntity();
            Level level = owner != null ? owner.level() : entity != null ? entity.level() : null;
            if (level == null) {
                return;
            }

            Player player = level.getPlayerByUUID(event.getPlayerUUID());
            if (player instanceof ServerPlayer serverPlayer) {
                ((CobblemonQuestsEventHandler) (Object) this)
                        .processTasksForTeam(pokemon, "register", amount, serverPlayer);
            }
        } catch (RuntimeException ignored) {
            // The upstream handler also treats malformed/ownerless dex updates as non-fatal.
        }
    }
}
