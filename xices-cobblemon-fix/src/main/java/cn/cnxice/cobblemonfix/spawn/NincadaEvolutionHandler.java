package cn.cnxice.cobblemonfix.spawn;

import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public final class NincadaEvolutionHandler {
    private static final float SHEDINJA_CHANCE = 0.10F;

    private NincadaEvolutionHandler() {
    }

    public static void onEvolutionComplete(EvolutionCompleteEvent event) {
        Pokemon source = event.getSourcePokemon();
        Pokemon evolved = event.getPokemon();
        if (!"nincada".equals(source.getSpecies().getResourceIdentifier().getPath())
                || !"ninjask".equals(evolved.getSpecies().getResourceIdentifier().getPath())) {
            return;
        }

        SpawnLocation location = findSpawnLocation(evolved);
        if (location == null || location.level().getRandom().nextFloat() >= SHEDINJA_CHANCE) {
            return;
        }

        IVs copiedIvs = new IVs();
        for (Map.Entry<?, ?> rawEntry : source.getIvs()) {
            @SuppressWarnings("unchecked")
            Map.Entry<com.cobblemon.mod.common.api.pokemon.stats.Stat, Integer> entry =
                    (Map.Entry<com.cobblemon.mod.common.api.pokemon.stats.Stat, Integer>) rawEntry;
            copiedIvs.set(entry.getKey(), entry.getValue());
        }

        PokemonProperties properties = new PokemonProperties();
        properties.setSpecies("shedinja");
        properties.setLevel(source.getLevel());
        properties.setIvs(copiedIvs);
        properties.setEvs(new EVs());
        properties.setFriendship(0);
        properties.setDmaxLevel(0);
        properties.setGmaxFactor(false);

        PokemonEntity shedinja = properties.createEntity(location.level());
        Vec3 position = location.position();
        shedinja.moveTo(position.x, position.y, position.z,
                location.level().getRandom().nextFloat() * 360.0F, 0.0F);
        location.level().addFreshEntity(shedinja);
    }

    private static SpawnLocation findSpawnLocation(Pokemon evolved) {
        PokemonEntity entity = evolved.getEntity();
        if (entity != null && entity.level() instanceof ServerLevel serverLevel) {
            return new SpawnLocation(serverLevel, entity.position());
        }

        ServerPlayer owner = evolved.getOwnerPlayer();
        if (owner != null) {
            return new SpawnLocation(owner.serverLevel(), owner.position());
        }
        return null;
    }

    private record SpawnLocation(ServerLevel level, Vec3 position) {
    }
}
