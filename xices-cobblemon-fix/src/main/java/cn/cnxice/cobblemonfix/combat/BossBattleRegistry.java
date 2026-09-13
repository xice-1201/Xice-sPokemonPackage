package cn.cnxice.cobblemonfix.combat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Runtime-only ownership of Boss damage reduction inside active battles. */
public final class BossBattleRegistry {
    private static final Map<UUID, UUID> BOSS_POKEMON_TO_BATTLE = new ConcurrentHashMap<>();

    public static void register(UUID battleId, UUID pokemonId) {
        if (battleId != null && pokemonId != null) BOSS_POKEMON_TO_BATTLE.put(pokemonId, battleId);
    }

    public static boolean isActiveBoss(UUID pokemonId) {
        return pokemonId != null && BOSS_POKEMON_TO_BATTLE.containsKey(pokemonId);
    }

    public static void unregisterBattle(UUID battleId) {
        if (battleId != null) BOSS_POKEMON_TO_BATTLE.entrySet().removeIf(entry -> battleId.equals(entry.getValue()));
    }

    private BossBattleRegistry() { }
}
