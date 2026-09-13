package cn.cnxice.cobblemonfix.combat;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/** Stores the Rage Powder caster on each protected entity for damage redirection. */
public final class AttentionTransferHandler {
    private static final String REDIRECTOR_KEY = "xice_attention_redirector";
    private static final String SOURCE_KEY = "xice_attention_source";

    public static void apply(LivingEntity protectedEntity, LivingEntity caster) {
        apply(protectedEntity, caster, "rage_powder");
    }

    public static void apply(LivingEntity protectedEntity, LivingEntity caster, String source) {
        protectedEntity.addEffect(new MobEffectInstance(CombatEffectsRegistry.ATTENTION_TRANSFER, 220, 0, false, true), caster);
        protectedEntity.getPersistentData().putUUID(REDIRECTOR_KEY, caster.getUUID());
        protectedEntity.getPersistentData().putString(SOURCE_KEY, source);
    }

    public static void clear(LivingEntity entity) {
        entity.removeEffect(CombatEffectsRegistry.ATTENTION_TRANSFER);
        entity.getPersistentData().remove(REDIRECTOR_KEY);
        entity.getPersistentData().remove(SOURCE_KEY);
    }

    public static boolean isPowderSource(LivingEntity target) {
        return "rage_powder".equals(target.getPersistentData().getString(SOURCE_KEY));
    }

    public static LivingEntity resolveRedirector(LivingEntity target) {
        if (!target.hasEffect(CombatEffectsRegistry.ATTENTION_TRANSFER)) return null;
        UUID id = target.getPersistentData().getUUID(REDIRECTOR_KEY);
        if (id == null) return null;
        for (var player : target.level().players()) if (player.getUUID().equals(id)) return player;
        for (LivingEntity candidate : target.level().getEntitiesOfClass(LivingEntity.class,
                target.getBoundingBox().inflate(48.0), e -> e.getUUID().equals(id))) return candidate;
        return null;
    }

    public static boolean isGrassAttacker(LivingEntity attacker) {
        if (!(attacker instanceof com.cobblemon.mod.common.entity.pokemon.PokemonEntity pokemon)) return false;
        if (pokemon.getPokemon() == null) return false;
        for (var type : pokemon.getPokemon().getTypes())
            if ("grass".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }

    public static boolean isFriendlySource(LivingEntity source, LivingEntity redirector) {
        if (source == redirector) return true;
        if (source instanceof com.cobblemon.mod.common.entity.pokemon.PokemonEntity pokemon
                && redirector instanceof Player player) {
            return player.getUUID().equals(pokemon.getPokemon().getOwnerUUID());
        }
        if (source instanceof com.cobblemon.mod.common.entity.pokemon.PokemonEntity a
                && redirector instanceof com.cobblemon.mod.common.entity.pokemon.PokemonEntity b) {
            UUID ao = a.getPokemon().getOwnerUUID(), bo = b.getPokemon().getOwnerUUID();
            return ao != null && ao.equals(bo);
        }
        return false;
    }

    private AttentionTransferHandler() { }
}
