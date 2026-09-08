package cn.cnxice.cobblemonfix.spawn;

import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Gender;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;

public final class SpawnCompanionHandler {
    private static final TagKey<Structure> VILLAGES = TagKey.create(
            Registries.STRUCTURE,
            ResourceLocation.fromNamespaceAndPath("minecraft", "village")
    );
    private static final ResourceLocation MANSION = ResourceLocation.fromNamespaceAndPath("minecraft", "mansion");

    private SpawnCompanionHandler() {
    }

    public static void onPokemonSpawn(SpawnEvent<PokemonEntity> event) {
        PokemonEntity source = event.getEntity();
        if (!(source.level() instanceof ServerLevel level)) {
            return;
        }

        String species = source.getPokemon().getSpecies().getResourceIdentifier().getPath();
        int pokemonLevel = source.getPokemon().getLevel();
        Vec3 position = source.position();
        RandomSource random = level.getRandom();

        switch (species) {
            case "plusle" -> schedule(level, position, pokemonLevel, "minun", 1);
            case "minun" -> schedule(level, position, pokemonLevel, "plusle", 1);
            case "miltank" -> schedule(level, position, pokemonLevel, "tauros", 1);
            case "tauros" -> schedule(level, position, pokemonLevel, "miltank", 1);
            case "durant" -> {
                int totalDurant = 4 + random.nextInt(5);
                schedule(level, position, pokemonLevel, "durant", totalDurant - 1);
                if (totalDurant >= 5) {
                    schedule(level, position, pokemonLevel, "heatmor", 1);
                }
            }
            case "dondozo" -> schedule(level, position, pokemonLevel, "tatsugiri", 1 + random.nextInt(3));
            case "indeedee" -> scheduleIndeedeeGroup(event, level, position, pokemonLevel, random);
            case "carbink" -> schedule(level, position, pokemonLevel, "carbink", 2 + random.nextInt(4));
            case "falinks" -> schedule(level, position, pokemonLevel, "falinks", 2 + random.nextInt(3));
            case "lapras" -> schedule(level, position, pokemonLevel, "lapras", 1 + random.nextInt(3));
            case "luvdisc" -> schedule(level, position, pokemonLevel, "luvdisc", 1);
            case "veluza" -> schedule(level, position, pokemonLevel, "veluza", 2 + random.nextInt(4));
            case "pachirisu" -> schedule(level, position, pokemonLevel, "pachirisu", 1 + random.nextInt(3));
            case "chatot" -> schedule(level, position, pokemonLevel, "chatot", 1 + random.nextInt(4));
            case "emolga" -> schedule(level, position, pokemonLevel, "emolga", 1 + random.nextInt(3));
            case "klawf" -> schedule(level, position, pokemonLevel, "klawf", 1 + random.nextInt(3));
            case "kangaskhan" -> schedule(level, position, pokemonLevel, "kangaskhan", 1 + random.nextInt(3));
            case "spinda" -> schedule(level, position, pokemonLevel, "spinda", 1 + random.nextInt(4));
            case "bouffalant" -> schedule(level, position, pokemonLevel, "bouffalant", 3 + random.nextInt(5));
            case "squawkabilly" -> scheduleSquawkabillyGroup(level, position, pokemonLevel, source, random);
            case "cyclizar" -> schedule(level, position, pokemonLevel, "cyclizar", 1 + random.nextInt(4));
            case "flamigo" -> schedule(level, position, pokemonLevel, "flamigo", 3 + random.nextInt(5));
            case "comfey" -> schedule(level, position, pokemonLevel, "comfey", 1 + random.nextInt(3));
            default -> {
            }
        }
    }

    private static void scheduleSquawkabillyGroup(
            ServerLevel level,
            Vec3 position,
            int pokemonLevel,
            PokemonEntity source,
            RandomSource random
    ) {
        String color = source.getPokemon().getAspects().stream()
                .filter(aspect -> aspect.startsWith("squawkabilly-color-"))
                .map(aspect -> aspect.substring("squawkabilly-color-".length()))
                .findFirst()
                .orElse("green");
        scheduleProperties(
                level,
                position,
                pokemonLevel,
                "squawkabilly squawkabilly_color=" + color,
                2 + random.nextInt(4)
        );
    }

    private static void scheduleIndeedeeGroup(
            SpawnEvent<PokemonEntity> event,
            ServerLevel level,
            Vec3 position,
            int pokemonLevel,
            RandomSource random
    ) {
        var spawnPosition = event.getSpawnablePosition();
        var blockPosition = spawnPosition.getPosition();
        var cache = spawnPosition.getStructureCache(blockPosition);
        var manager = level.structureManager();
        boolean village = cache.check(manager, blockPosition, VILLAGES);
        boolean mansion = cache.check(manager, blockPosition, MANSION);
        if (!village && !mansion) {
            return;
        }

        int total = 4 + random.nextInt(5);
        int majority = (int) Math.round(total * 0.75D);
        int maleTarget = village ? majority : total - majority;
        int femaleTarget = total - maleTarget;
        Gender sourceGender = event.getEntity().getPokemon().getGender();
        int malesToSpawn = maleTarget - (sourceGender == Gender.MALE ? 1 : 0);
        int femalesToSpawn = femaleTarget - (sourceGender == Gender.FEMALE ? 1 : 0);

        level.getServer().execute(() -> {
            for (int index = 0; index < Math.max(0, malesToSpawn); index++) {
                spawnNearby(level, position, pokemonLevel, "indeedee", Gender.MALE);
            }
            for (int index = 0; index < Math.max(0, femalesToSpawn); index++) {
                spawnNearby(level, position, pokemonLevel, "indeedee", Gender.FEMALE);
            }
        });
    }

    private static void schedule(ServerLevel level, Vec3 position, int pokemonLevel, String species, int count) {
        level.getServer().execute(() -> {
            for (int index = 0; index < count; index++) {
                spawnNearby(level, position, pokemonLevel, species);
            }
        });
    }

    private static void scheduleProperties(
            ServerLevel level,
            Vec3 position,
            int pokemonLevel,
            String propertiesText,
            int count
    ) {
        level.getServer().execute(() -> {
            for (int index = 0; index < count; index++) {
                PokemonProperties properties = PokemonProperties.Companion.parse(propertiesText);
                properties.setLevel(pokemonLevel);
                spawnNearby(level, position, properties);
            }
        });
    }

    private static void spawnNearby(ServerLevel level, Vec3 origin, int pokemonLevel, String species) {
        spawnNearby(level, origin, pokemonLevel, species, null);
    }

    private static void spawnNearby(
            ServerLevel level,
            Vec3 origin,
            int pokemonLevel,
            String species,
            Gender gender
    ) {
        PokemonProperties properties = new PokemonProperties();
        properties.setSpecies(species);
        properties.setLevel(pokemonLevel);
        if (gender != null) {
            properties.setGender(gender);
        }
        spawnNearby(level, origin, properties);
    }

    private static void spawnNearby(ServerLevel level, Vec3 origin, PokemonProperties properties) {
        PokemonEntity entity = properties.createEntity(level);

        RandomSource random = level.getRandom();
        Vec3 selected = origin;
        for (int attempt = 0; attempt < 8; attempt++) {
            Vec3 candidate = origin.add(random.nextDouble() * 5.0 - 2.5, 0.0, random.nextDouble() * 5.0 - 2.5);
            entity.moveTo(candidate.x, candidate.y, candidate.z, random.nextFloat() * 360.0F, 0.0F);
            if (level.noCollision(entity)) {
                selected = candidate;
                break;
            }
        }
        entity.moveTo(selected.x, selected.y, selected.z, random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(entity);
    }
}
