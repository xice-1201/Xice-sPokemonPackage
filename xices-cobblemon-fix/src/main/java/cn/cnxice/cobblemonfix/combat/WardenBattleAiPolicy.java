package cn.cnxice.cobblemonfix.combat;

import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Deterministic entry-lock and weighted move policy for the Warden battle proxy. */
public final class WardenBattleAiPolicy {
    public enum Action { DARK_SURGE, WARDEN_PUNCH, SONIC_WAVE, SONIC_LOCK }

    private static final Map<UUID, UUID> LAST_OPPONENT = new HashMap<>();

    /** Selects a normal action without inspecting abilities, types, or accuracy stages. */
    public static Action chooseNormalAction(RandomSource random, boolean darkSurgeEligible) {
        int roll = random.nextInt(100);
        if (darkSurgeEligible) {
            if (roll < 60) return Action.DARK_SURGE;
            if (roll < 90) return Action.WARDEN_PUNCH;
            return Action.SONIC_WAVE;
        }
        return roll < 75 ? Action.WARDEN_PUNCH : Action.SONIC_WAVE;
    }

    /** Returns true once for each newly entered opposing BattlePokemon. */
    public static boolean opponentJustEntered(PokemonBattle battle, ActiveBattlePokemon active, boolean forceSwitch) {
        if (forceSwitch || battle == null || active == null) return false;
        var opponent = active.getOppositeOpponent();
        if (!(opponent instanceof ActiveBattlePokemon other) || other.getBattlePokemon() == null) return false;
        UUID battleId = battle.getBattleId();
        UUID pokemonId = other.getBattlePokemon().getUuid();
        UUID previous = LAST_OPPONENT.put(battleId, pokemonId);
        return previous == null || !previous.equals(pokemonId);
    }

    public static void clearBattle(UUID battleId) {
        if (battleId != null) LAST_OPPONENT.remove(battleId);
    }

    public static boolean darkSurgeEligible(ActiveBattlePokemon active) {
        var opponent = active.getOppositeOpponent();
        if (!(opponent instanceof ActiveBattlePokemon other) || other.getBattlePokemon() == null) return false;
        return other.getBattlePokemon().getStatChanges().getOrDefault(Stats.ACCURACY, 0) > -3;
    }

    private WardenBattleAiPolicy() { }
}
