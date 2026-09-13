package cn.cnxice.cobblemonfix.combat;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/** Reads only the equipped MoveSet and classifies it using the generated contact table. */
public final class PokemonCombatMoveSelector {
    private static final List<String> CONTACT_MOVES = loadContactMoves();
    private static final Set<String> CONTACT_FALLBACKS = Set.of("dizzypunch", "tackle", "suckerpunch");
    private static final Set<String> STATUS_FALLBACKS = Set.of("hypnosis");
    private static final Map<String, Map<String, Integer>> TYPE_CHART = loadTypeChart();

    public static List<PokemonCombatMove> equippedMoves(Pokemon pokemon) {
        List<PokemonCombatMove> result = new ArrayList<>();
        for (Move move : pokemon.getMoveSet().getMoves()) {
            if (move != null) {
                String id = normalize(move.getTemplate().getName());
                result.add(new PokemonCombatMove(move, CONTACT_MOVES.contains(id) || CONTACT_FALLBACKS.contains(id)));
            }
        }
        return result;
    }

    public static PokemonCombatMove firstStatus(List<PokemonCombatMove> moves) {
        return moves.stream().filter(PokemonCombatMove::isStatus).findFirst().orElse(null);
    }

    public static PokemonCombatMove firstMelee(List<PokemonCombatMove> moves) {
        return moves.stream().filter(m -> !m.isStatus() && m.isMelee()).findFirst().orElse(null);
    }

    public static PokemonCombatMove firstRanged(List<PokemonCombatMove> moves) {
        // Status moves are never attacks, even if a malformed/custom category is present.
        return moves.stream().filter(m -> !m.isStatus()
                && !STATUS_FALLBACKS.contains(normalize(m.move().getTemplate().getName()))
                && m.isRanged()).findFirst().orElse(null);
    }

    /** Returns the Showdown effectiveness multiplier for one attacking type and all defender types. */
    public static double effectiveness(PokemonCombatMove move, Pokemon pokemon) {
        if (pokemon == null) return 1.0;
        String attack = move.move().getType().getShowdownId().toLowerCase(java.util.Locale.ROOT);
        double multiplier = 1.0;
        for (var type : pokemon.getTypes()) {
            Integer value = TYPE_CHART.getOrDefault(type.getShowdownId().toLowerCase(java.util.Locale.ROOT), Map.of()).get(attack);
            if (value != null) multiplier *= value == 1 ? 2.0 : value == 2 ? 0.5 : value == 3 ? 0.0 : 1.0;
        }
        return multiplier;
    }

    private static String normalize(String name) {
        int separator = name.lastIndexOf(':');
        if (separator >= 0) name = name.substring(separator + 1);
        return name.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private static List<String> loadContactMoves() {
        List<String> result = new ArrayList<>();
        try (var stream = PokemonCombatMoveSelector.class.getResourceAsStream("/data/xices_cobblemon_fix/moves/contact_moves.json")) {
            if (stream == null) return result;
            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            for (String key : root.getAsJsonObject("moves").keySet()) result.add(normalize(key.substring(key.indexOf(':') + 1)));
        } catch (Exception ignored) { }
        return result;
    }

    private static Map<String, Map<String, Integer>> loadTypeChart() {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        try (var stream = PokemonCombatMoveSelector.class.getResourceAsStream("/data/xices_cobblemon_fix/moves/type_chart.json")) {
            if (stream == null) return result;
            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            for (String defender : root.keySet()) {
                Map<String, Integer> row = new HashMap<>();
                for (String attacker : root.getAsJsonObject(defender).keySet()) row.put(attacker, root.getAsJsonObject(defender).get(attacker).getAsInt());
                result.put(defender, row);
            }
        } catch (Exception ignored) { }
        return result;
    }

    private PokemonCombatMoveSelector() { }
}
