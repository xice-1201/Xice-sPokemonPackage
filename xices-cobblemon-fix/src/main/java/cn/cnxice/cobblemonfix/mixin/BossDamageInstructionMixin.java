package cn.cnxice.cobblemonfix.mixin;

import cn.cnxice.cobblemonfix.combat.BossBattleRegistry;
import com.cobblemon.mod.common.pokemon.Pokemon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Applies Boss mitigation after Showdown has resolved type effectiveness. */
@Mixin(targets = "com.cobblemon.mod.common.battles.interpreter.instructions.DamageInstruction", remap = false)
public abstract class BossDamageInstructionMixin {
    private static final double BOSS_DAMAGE_MULTIPLIER = 0.20D;
    private static final Logger LOG = LoggerFactory.getLogger("Xice Boss Damage");
    private static final ThreadLocal<Boolean> DISPLAY_BOSS = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Integer> DISPLAY_FINAL_HEALTH = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> DISPLAY_MAX_HEALTH = ThreadLocal.withInitial(() -> 1);

    /**
     * DamageInstruction sends the client health packets before its deferred
     * setter callback runs. Prepare the same reduced value for those packets.
     */
    @Inject(method = "postActionEffect$lambda$0", at = @At("HEAD"), remap = false)
    private static void xice$prepareDisplayHealth(
            com.cobblemon.mod.common.battles.pokemon.BattlePokemon target,
            kotlin.jvm.internal.Ref.BooleanRef fainted,
            String damage,
            com.cobblemon.mod.common.api.battles.interpreter.Effect effect,
            com.cobblemon.mod.common.battles.pokemon.BattlePokemon expectedTarget,
            com.cobblemon.mod.common.api.battles.model.PokemonBattle battle,
            com.cobblemon.mod.common.battles.interpreter.instructions.DamageInstruction instruction,
            com.cobblemon.mod.common.battles.dispatch.CauserInstruction causer,
            CallbackInfoReturnable<?> cir) {
        DISPLAY_BOSS.set(false);
        if (target == null || target.getEffectedPokemon() == null || damage == null) return;
        java.util.UUID uuid = target.getEffectedPokemon().getUuid();
        if (!BossBattleRegistry.isActiveBoss(uuid)) return;
        String[] parts = damage.split("/", 2);
        if (parts.length != 2) return;
        try {
            int requestedHealth = Integer.parseInt(parts[0]);
            int maxHealth = Math.max(1, Integer.parseInt(parts[1]));
            int oldHealth = target.getEffectedPokemon().getCurrentHealth();
            int originalDamage = Math.max(0, oldHealth - requestedHealth);
            int reducedDamage = originalDamage > 0
                    ? Math.max(1, (int) Math.floor(originalDamage * BOSS_DAMAGE_MULTIPLIER))
                    : Math.max(1, (int) Math.floor(oldHealth * BOSS_DAMAGE_MULTIPLIER));
            int finalHealth = Math.max(0, oldHealth - reducedDamage);
            DISPLAY_BOSS.set(true);
            DISPLAY_FINAL_HEALTH.set(finalHealth);
            DISPLAY_MAX_HEALTH.set(maxHealth);
            LOG.debug("display target={} oldHp={} requestedHp={} originalDamage={} displayHp={} displayRatio={}",
                    uuid, oldHealth, requestedHealth, originalDamage, finalHealth,
                    (float) finalHealth / (float) maxHealth);
        } catch (NumberFormatException ignored) {
            // Leave malformed/non-health messages untouched.
        }
    }

    @Inject(method = "postActionEffect$lambda$0", at = @At("RETURN"), remap = false)
    private static void xice$clearDisplayHealth(
            com.cobblemon.mod.common.battles.pokemon.BattlePokemon target,
            kotlin.jvm.internal.Ref.BooleanRef fainted,
            String damage,
            com.cobblemon.mod.common.api.battles.interpreter.Effect effect,
            com.cobblemon.mod.common.battles.pokemon.BattlePokemon expectedTarget,
            com.cobblemon.mod.common.api.battles.model.PokemonBattle battle,
            com.cobblemon.mod.common.battles.interpreter.instructions.DamageInstruction instruction,
            com.cobblemon.mod.common.battles.dispatch.CauserInstruction causer,
            CallbackInfoReturnable<?> cir) {
        DISPLAY_BOSS.remove();
        DISPLAY_FINAL_HEALTH.remove();
        DISPLAY_MAX_HEALTH.remove();
    }

    @ModifyArgs(
            method = "postActionEffect$lambda$0",
            at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/net/messages/client/battle/BattleHealthChangePacket;<init>(Ljava/lang/String;FLjava/lang/Float;ILkotlin/jvm/internal/DefaultConstructorMarker;)V", ordinal = 0),
            remap = false
    )
    private static void xice$modifyAbsoluteDisplayHealth(Args args) {
        if (DISPLAY_BOSS.get()) args.set(1, DISPLAY_FINAL_HEALTH.get().floatValue());
    }

    @ModifyArgs(
            method = "postActionEffect$lambda$0",
            at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/net/messages/client/battle/BattleHealthChangePacket;<init>(Ljava/lang/String;FLjava/lang/Float;ILkotlin/jvm/internal/DefaultConstructorMarker;)V", ordinal = 1),
            remap = false
    )
    private static void xice$modifyRatioDisplayHealth(Args args) {
        if (DISPLAY_BOSS.get()) {
            args.set(1, (float) DISPLAY_FINAL_HEALTH.get() / (float) DISPLAY_MAX_HEALTH.get());
        }
    }

    @Redirect(
            method = "postActionEffect$lambda$0$1",
            at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;setCurrentHealth(I)V"),
            remap = false
    )
    private static void xice$reduceNormalDamage(Pokemon pokemon, int requestedHealth) {
        java.util.UUID uuid = pokemon.getUuid();
        boolean boss = BossBattleRegistry.isActiveBoss(uuid);
        int oldHealth = pokemon.getCurrentHealth();
        int originalDamage = Math.max(0, oldHealth - requestedHealth);
        if (!boss) {
            pokemon.setCurrentHealth(requestedHealth);
            LOG.debug("damage normal target={} boss=false oldHp={} requestedHp={} originalDamage={} finalHp={}",
                    uuid, oldHealth, requestedHealth, originalDamage, requestedHealth);
            return;
        }
        if (originalDamage == 0) {
            pokemon.setCurrentHealth(requestedHealth);
            LOG.debug("damage normal target={} boss=true oldHp={} requestedHp={} originalDamage=0 reducedDamage=0 finalHp={}",
                    uuid, oldHealth, requestedHealth, requestedHealth);
            return;
        }
        int reducedDamage = Math.max(1, (int) Math.floor(originalDamage * BOSS_DAMAGE_MULTIPLIER));
        int finalHealth = Math.max(0, oldHealth - reducedDamage);
        pokemon.setCurrentHealth(finalHealth);
        LOG.debug("damage normal target={} boss=true oldHp={} requestedHp={} originalDamage={} reducedDamage={} finalHp={}",
                uuid, oldHealth, requestedHealth, originalDamage, reducedDamage, finalHealth);
    }

    @Redirect(
            method = "postActionEffect$lambda$0$0",
            at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;setCurrentHealth(I)V"),
            remap = false
    )
    private static void xice$reduceLethalDamage(Pokemon pokemon, int ignoredZero) {
        java.util.UUID uuid = pokemon.getUuid();
        boolean boss = BossBattleRegistry.isActiveBoss(uuid);
        int oldHealth = pokemon.getCurrentHealth();
        if (!boss) {
            pokemon.setCurrentHealth(ignoredZero);
            LOG.debug("damage lethal target={} boss=false oldHp={} requestedHp=0 originalDamage={} finalHp=0",
                    uuid, oldHealth, oldHealth);
            return;
        }
        int reducedDamage = Math.max(1, (int) Math.floor(oldHealth * BOSS_DAMAGE_MULTIPLIER));
        int finalHealth = Math.max(0, oldHealth - reducedDamage);
        pokemon.setCurrentHealth(finalHealth);
        LOG.debug("damage lethal target={} boss=true oldHp={} requestedHp=0 originalDamage={} reducedDamage={} finalHp={}",
                uuid, oldHealth, oldHealth, reducedDamage, finalHealth);
    }
}
