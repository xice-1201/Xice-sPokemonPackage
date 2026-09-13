package cn.cnxice.cobblemonfix.combat;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.categories.DamageCategory;
import com.cobblemon.mod.common.api.moves.categories.DamageCategories;

/** Immutable combat classification for one of a Pokémon's currently equipped moves. */
public record PokemonCombatMove(Move move, boolean contact) {
    public boolean isMelee() { return contact; }
    public boolean isRanged() { return !contact; }
    public boolean isStatus() { return categoryName().equals("status"); }
    public boolean isPhysical() { return categoryName().equals("physical"); }
    public boolean isSpecial() { return categoryName().equals("special"); }
    private String categoryName() { return move.getDamageCategory().getName().toLowerCase(java.util.Locale.ROOT); }
    public String id() { return move.getTemplate().getName().toLowerCase(java.util.Locale.ROOT); }
}
