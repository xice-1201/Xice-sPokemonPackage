package cn.cnxice.cobblemonfix.combat;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;

/**
 * Mirrors Showdown's confirmed accuracy stages for the Java-side Warden AI.
 * This never broadcasts a battle message and never changes Showdown's state.
 */
public final class WardenAccuracyStageMirror {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger("Xice Warden");

    public static void applyBoost(BattlePokemon pokemon, String statKey, int stages, boolean boost) {
        if (pokemon == null || !"accuracy".equalsIgnoreCase(statKey)) return;
        int delta = boost ? stages : -stages;
        int oldStage = pokemon.getStatChanges().getOrDefault(Stats.ACCURACY, 0);
        int newStage = Math.max(-6, Math.min(6, oldStage + delta));
        pokemon.getStatChanges().put(Stats.ACCURACY, newStage);
        debug("accuracy mirror pokemon={} old={} delta={} new={} source={}", pokemon.getUuid(), oldStage, delta, newStage,
                boost ? "boost" : "unboost");
    }

    public static void setBoost(BattlePokemon pokemon, String statKey, int stage) {
        if (pokemon == null || !"accuracy".equalsIgnoreCase(statKey)) return;
        int newStage = Math.max(-6, Math.min(6, stage));
        int oldStage = pokemon.getStatChanges().getOrDefault(Stats.ACCURACY, 0);
        pokemon.getStatChanges().put(Stats.ACCURACY, newStage);
        debug("accuracy mirror pokemon={} old={} new={} source=setboost", pokemon.getUuid(), oldStage, newStage);
    }

    public static void clear(BattlePokemon pokemon, String source) {
        if (pokemon == null) return;
        int oldStage = pokemon.getStatChanges().getOrDefault(Stats.ACCURACY, 0);
        pokemon.getStatChanges().remove(Stats.ACCURACY);
        if (oldStage != 0) debug("accuracy mirror pokemon={} old={} new=0 source={}", pokemon.getUuid(), oldStage, source);
    }

    public static void clearNegative(BattlePokemon pokemon, String source) {
        if (pokemon == null) return;
        int oldStage = pokemon.getStatChanges().getOrDefault(Stats.ACCURACY, 0);
        if (oldStage < 0) {
            pokemon.getStatChanges().remove(Stats.ACCURACY);
            debug("accuracy mirror pokemon={} old={} new=0 source={}", pokemon.getUuid(), oldStage, source);
        }
    }

    public static void clearAll(Iterable<? extends com.cobblemon.mod.common.battles.ActiveBattlePokemon> active, String source) {
        for (var slot : active) if (slot != null) clear(slot.getBattlePokemon(), source);
    }

    public static void applyBoostMessage(BattleMessage message, com.cobblemon.mod.common.api.battles.model.PokemonBattle battle,
                                         boolean boost) {
        try {
            BattlePokemon pokemon = message.battlePokemon(0, battle);
            String statKey = message.argumentAt(1);
            int stages = Integer.parseInt(message.argumentAt(2));
            applyBoost(pokemon, statKey, stages, boost);
        } catch (RuntimeException ignored) {
            debug("accuracy mirror ignored malformed boost message id={} raw={}", message.getId(), message.getRawMessage());
        }
    }

    public static void applySetBoostMessage(BattleMessage message, com.cobblemon.mod.common.api.battles.model.PokemonBattle battle) {
        try {
            BattlePokemon pokemon = message.battlePokemon(0, battle);
            setBoost(pokemon, message.argumentAt(1), Integer.parseInt(message.argumentAt(2)));
        } catch (RuntimeException ignored) {
            debug("accuracy mirror ignored malformed setboost message id={} raw={}", message.getId(), message.getRawMessage());
        }
    }

    private static void debug(String format, Object... args) {
        if (cn.cnxice.cobblemonfix.BattleAttributeConfig.WARDEN_BATTLE_DEBUG_LOG.get()) LOG.info("[Warden debug] " + format, args);
    }

    private WardenAccuracyStageMirror() {}
}
