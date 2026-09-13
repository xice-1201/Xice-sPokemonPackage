package cn.cnxice.cobblemonfix.combat;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.ArrayList;

/** First-pass overworld combat loop driven exclusively by the equipped moveset. */
public final class PokemonWorldCombatController {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final double MELEE_RANGE = 3.0;
    private static final double RANGED_RANGE = 12.0;
    private static final double RANGED_PREFERRED_MIN = 10.0;
    private static final double OWNER_BUFF_RANGE = 32.0;

    public static void tick(PokemonEntity entity) {
        if (entity.level().isClientSide() || entity.isDeadOrDying() || entity.getPokemon() == null) return;
        // A Pokémon participating in Cobblemon's turn-based battle must never run the
        // separate overworld combat controller at the same time.
        if (entity.getBattle() != null) {
            entity.setTarget(null);
            entity.getNavigation().stop();
            return;
        }
        // A Pokémon that fainted during overworld combat must be recalled before it can fight again.
        if (entity.getPersistentData().getBoolean("xice_world_combat_disabled")) {
            entity.setTarget(null);
            entity.getNavigation().stop();
            return;
        }
        if (entity.getPokemon().isFainted()) {
            if (entity.getPersistentData().getBoolean("xice_world_combat_active")) {
                entity.getPersistentData().putBoolean("xice_world_combat_disabled", true);
                entity.setTarget(null);
                entity.getNavigation().stop();
            }
            return;
        }
        int cooldown = entity.getPersistentData().getInt("xice_world_attack_cooldown");
        if (cooldown > 0) {
            entity.getPersistentData().putInt("xice_world_attack_cooldown", cooldown - 1);
        }

        List<PokemonCombatMove> moves = PokemonCombatMoveSelector.equippedMoves(entity.getPokemon());
        if (moves.isEmpty()) return;
        if (!entity.getPersistentData().getBoolean("xice_combat_class_logged")) {
            LOGGER.info("[Xice combat] {} equipped moves: {}", entity.getPokemon().getDisplayName(false).getString(),
                    moves.stream().map(m -> m.id() + "=" + (m.isStatus() ? "status" : (m.isMelee() ? "melee" : "ranged"))
                            + "/" + (m.isPhysical() ? "physical" : m.isSpecial() ? "special" : "other"))
                            .toList());
            entity.getPersistentData().putBoolean("xice_combat_class_logged", true);
        }

        LivingEntity target = selectTarget(entity);
        if (target == null || !target.isAlive()) return;
        entity.getPersistentData().putBoolean("xice_world_combat_active", true);
        if (entity.getTarget() != target) entity.setTarget(target);

        // Buffs are combat actions: never cast them while the Pokémon is idle.
        List<LivingEntity> buffTargets = buffTargets(entity);
        PokemonCombatMove ragePowder = moves.stream().filter(PokemonWorldCombatController::isRagePowder).findFirst().orElse(null);
        PokemonCombatMove followMe = moves.stream().filter(PokemonWorldCombatController::isFollowMe).findFirst().orElse(null);
        PokemonCombatMove spotlight = moves.stream().filter(PokemonWorldCombatController::isSpotlight).findFirst().orElse(null);
        if (ragePowder != null && cooldown <= 0 && shouldUseRagePowder(entity)) {
            List<LivingEntity> attentionTargets = teamTargets(entity);
            for (LivingEntity member : attentionTargets) AttentionTransferHandler.clear(member);
            for (LivingEntity member : attentionTargets) if (member != entity) AttentionTransferHandler.apply(member, entity);
            setCooldown(entity);
            return;
        }
        if (followMe != null && cooldown <= 0 && shouldUseRagePowder(entity)) {
            List<LivingEntity> attentionTargets = teamTargets(entity);
            for (LivingEntity member : attentionTargets) AttentionTransferHandler.clear(member);
            AttentionTransferHandler.apply(entity, entity, "follow_me");
            setCooldown(entity);
            return;
        }
        if (spotlight != null && cooldown <= 0) {
            LivingEntity selected = selectSpotlightTarget(entity);
            if (selected != null) {
                for (LivingEntity member : teamTargets(entity)) AttentionTransferHandler.clear(member);
                AttentionTransferHandler.apply(selected, entity, "spotlight");
                setCooldown(entity);
                return;
            }
        }
        PokemonCombatMove status = moves.stream().filter(m -> m.isStatus() && !isRedirectMove(m)).findFirst().orElse(null);
        boolean psychic = isPsychicType(entity);
        boolean fairy = isFairyType(entity);
        boolean hasBuff = !buffTargets.isEmpty() && buffTargets.stream().allMatch(memberTarget ->
                psychic ? (memberTarget.hasEffect(MobEffects.DAMAGE_BOOST) && memberTarget.hasEffect(MobEffects.MOVEMENT_SPEED))
                        : fairy ? memberTarget.hasEffect(MobEffects.REGENERATION) : memberTarget.hasEffect(MobEffects.DAMAGE_RESISTANCE));
        if (status != null && !hasBuff && cooldown <= 0) {
            for (LivingEntity buffTarget : buffTargets) {
                if (psychic) {
                    buffTarget.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0, false, true), entity);
                    buffTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0, false, true), entity);
                } else if (fairy) buffTarget.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, false, true), entity);
                else buffTarget.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0, false, true), entity);
            }
            setCooldown(entity);
            return;
        }
        double distance = entity.distanceTo(target);
        PokemonCombatMove melee = PokemonCombatMoveSelector.firstMelee(moves);
        PokemonCombatMove ranged = PokemonCombatMoveSelector.firstRanged(moves);

        if (distance <= MELEE_RANGE && melee != null) {
            entity.getNavigation().stop();
            if (cooldown <= 0) attack(entity, target, melee);
            return;
        }

        if (ranged != null && distance <= RANGED_RANGE && entity.getSensing().hasLineOfSight(target)) {
            if (distance >= RANGED_PREFERRED_MIN || melee == null) entity.getNavigation().stop();
            if (cooldown <= 0) attack(entity, target, ranged);
            return;
        }

        if (melee != null || distance > RANGED_RANGE) {
            entity.getNavigation().moveTo(target, 1.0);
        }
    }

    private static List<LivingEntity> buffTargets(PokemonEntity entity) {
        List<LivingEntity> result = new ArrayList<>();
        ServerPlayer owner = entity.getPokemon().getOwnerPlayer();
        if (owner == null) {
            result.add(entity);
            return result;
        }
        result.add(owner);
        var party = Cobblemon.INSTANCE.getStorage().getParty(owner);
        for (var pokemon : party) {
            if (pokemon == null || pokemon.isFainted()) continue;
            PokemonEntity member = pokemon.getEntity();
            if (member == null || member == entity || member.level() != entity.level() || !member.isAlive()
                    || member.getPersistentData().getBoolean("xice_world_combat_disabled")) continue;
            result.add(member);
        }
        return result;
    }

    private static boolean isRagePowder(PokemonCombatMove move) {
        String id = move.id();
        int colon = id.lastIndexOf(':');
        if (colon >= 0) id = id.substring(colon + 1);
        return id.replaceAll("[^a-z0-9]", "").equals("ragepowder");
    }

    private static boolean isFollowMe(PokemonCombatMove move) { return move.id().replaceAll("[^a-z0-9]", "").endsWith("followme"); }
    private static boolean isSpotlight(PokemonCombatMove move) { return move.id().replaceAll("[^a-z0-9]", "").endsWith("spotlight"); }
    private static boolean isRedirectMove(PokemonCombatMove move) { return isRagePowder(move) || isFollowMe(move) || isSpotlight(move); }

    private static LivingEntity selectSpotlightTarget(PokemonEntity entity) {
        List<LivingEntity> team = teamTargets(entity);
        boolean hasInjured = team.stream().anyMatch(e -> e != entity && healthRatio(e) <= 0.5F &&
                !e.hasEffect(CombatEffectsRegistry.ATTENTION_TRANSFER));
        boolean condition = hasInjured ? team.stream().anyMatch(e -> healthRatio(e) > 0.5F)
                : shouldUseRagePowder(entity);
        if (!condition) return null;
        return team.stream().filter(e -> healthRatio(e) > 0.5F)
                .max(java.util.Comparator.comparingDouble(PokemonWorldCombatController::healthRatio)).orElse(null);
    }

    private static float healthRatio(LivingEntity entity) {
        return entity.getMaxHealth() <= 0 ? 0.0F : entity.getHealth() / entity.getMaxHealth();
    }

    private static boolean shouldUseRagePowder(PokemonEntity entity) {
        ServerPlayer owner = entity.getPokemon().getOwnerPlayer();
        if (owner == null) return false;
        if (!owner.hasEffect(CombatEffectsRegistry.ATTENTION_TRANSFER)) return true;
        boolean foundTeammate = false;
        for (LivingEntity member : teamTargets(entity)) {
            if (member == owner || member == entity) continue;
            foundTeammate = true;
            if (member.hasEffect(CombatEffectsRegistry.ATTENTION_TRANSFER)) return false;
        }
        return foundTeammate;
    }

    private static List<LivingEntity> teamTargets(PokemonEntity entity) {
        List<LivingEntity> result = new ArrayList<>();
        ServerPlayer owner = entity.getPokemon().getOwnerPlayer();
        if (owner == null) return result;
        result.add(owner);
        for (var pokemon : Cobblemon.INSTANCE.getStorage().getParty(owner)) {
            if (pokemon == null || pokemon.isFainted()) continue;
            PokemonEntity member = pokemon.getEntity();
            if (member == null || !member.isAlive() || member.getPersistentData().getBoolean("xice_world_combat_disabled")) continue;
            result.add(member);
        }
        return result;
    }

    /** Wolf-like target inheritance: assist the owner's attack, then defend owner/self. */
    private static LivingEntity selectTarget(PokemonEntity entity) {
        ServerPlayer owner = entity.getPokemon().getOwnerPlayer();
        if (owner != null) {
            LivingEntity target = owner.getLastHurtMob();
            if (validTarget(entity, target, owner)) return target;
            target = owner.getLastHurtByMob();
            if (validTarget(entity, target, owner)) return target;
        } else if (isFightingType(entity)) {
            // Wild Fighting Pokémon naturally challenge nearby illagers.
            List<Raider> raiders = entity.level().getEntitiesOfClass(Raider.class,
                    entity.getBoundingBox().inflate(24.0), LivingEntity::isAlive);
            Raider nearest = null;
            double best = Double.MAX_VALUE;
            for (Raider raider : raiders) {
                double distance = entity.distanceToSqr(raider);
                if (distance < best) { best = distance; nearest = raider; }
            }
            if (nearest != null) return nearest;
        } else if (isDarkType(entity)) {
            List<LivingEntity> candidates = entity.level().getEntitiesOfClass(LivingEntity.class,
                    entity.getBoundingBox().inflate(24.0), PokemonWorldCombatController::isDarkWildTarget);
            LivingEntity nearest = null; double best = Double.MAX_VALUE;
            for (LivingEntity candidate : candidates) { double d = entity.distanceToSqr(candidate); if (d < best) { best = d; nearest = candidate; } }
            if (nearest != null) return nearest;
        }
        LivingEntity target = entity.getTarget();
        if (validTarget(entity, target, owner)) return target;
        // Wild Pokémon retaliate against the entity that most recently hurt them.
        target = entity.getLastHurtByMob();
        return validTarget(entity, target, owner) ? target : null;
    }

    private static boolean validTarget(PokemonEntity entity, LivingEntity target, ServerPlayer owner) {
        if (target == null || !target.isAlive() || target == entity || target == owner) return false;
        if (target instanceof PokemonEntity other) {
            if (other == entity) return false;
            if (owner != null && owner.getUUID().equals(other.getPokemon().getOwnerUUID())) return false;
            if (other.getPokemon().getOwnerUUID() != null && entity.getPokemon().getOwnerUUID() != null
                    && other.getPokemon().getOwnerUUID().equals(entity.getPokemon().getOwnerUUID())) return false;
        }
        return entity.distanceToSqr(target) <= 24.0 * 24.0;
    }

    private static void attack(PokemonEntity attacker, LivingEntity target, PokemonCombatMove move) {
        String elementalType = move.move().getType().getShowdownId().toLowerCase(java.util.Locale.ROOT);
        if ("electric".equals(elementalType) && hasNearbyLightningRod(target)) {
            setCooldown(attacker);
            return;
        }
        double damage = move.isPhysical()
                ? attacker.getAttributeValue(Attributes.ATTACK_DAMAGE)
                : attacker.getPersistentData().getDouble("xice_magic_attack_damage");
        if (damage <= 0.0) damage = 1.0;
        if (attacker.getPokemon().getTypes().iterator().hasNext()) {
            for (var type : attacker.getPokemon().getTypes()) {
                if (type == move.move().getType()) { damage *= 1.25; break; }
            }
        }
        damage *= PokemonCombatMoveSelector.effectiveness(move, target instanceof PokemonEntity p ? p.getPokemon() : null);
        if ("electric".equals(elementalType) && isVanillaAquatic(target)) damage *= 2.0;
        if ("fire".equals(elementalType)) {
            if (isSnowOrIronGolem(target) || isVanillaArthropod(target)) damage *= 2.0;
            if (isBlaze(target) || isVanillaAquatic(target)) damage *= 0.5;
        }
        if ("water".equals(elementalType)) {
            if (isBlaze(target)) damage *= 2.0;
            if (isVanillaAquatic(target)) damage *= 0.5;
        }
        if ("grass".equals(elementalType)) {
            if (isBlaze(target) || isVanillaArthropod(target) || isEnderDragon(target)) damage *= 0.5;
            if (isVanillaAquatic(target)) damage *= 2.0;
        }
        if ("ice".equals(elementalType)) {
            if (isBlaze(target) || isVanillaAquatic(target) || isSnowOrIronGolem(target)) damage *= 0.5;
            if (isEnderDragon(target)) damage *= 2.0;
        }
        if ("fighting".equals(elementalType)) {
            if (isSnowGolem(target) || target instanceof Raider) damage *= 2.0;
            if (isVanillaArthropod(target)) damage *= 0.5;
        }
        if ("normal".equals(elementalType)) {
            if (isAllayOrVex(target)) damage = 0.0;
            else if (isIronGolem(target)) damage *= 0.5;
        }
        if ("grass".equals(elementalType) || "ice".equals(elementalType)) if (isIronGolem(target)) damage *= 0.5;
        if ("fighting".equals(elementalType)) {
            if (isAllayOrVex(target)) damage = 0.0;
            else if (isIronGolem(target)) damage *= 2.0;
        }
        if ("poison".equals(elementalType)) {
            if (isIronGolem(target)) damage = 0.0;
            else if (isAllayOrVex(target)) damage *= 0.5;
        }
        if ("fire".equals(elementalType) || "water".equals(elementalType) || "electric".equals(elementalType)) if (isEnderDragon(target)) damage *= 0.5;
        if ("ground".equals(elementalType)) {
            if (isBlaze(target) || isIronGolem(target)) damage *= 2.0;
            if (isVanillaArthropod(target)) damage *= 0.5;
        }
        if ("flying".equals(elementalType)) {
            if (isVanillaArthropod(target)) damage *= 2.0;
            if (isIronGolem(target)) damage *= 0.5;
        }
        if ("psychic".equals(elementalType)) {
            if (target instanceof Raider || isVex(target)) damage = 0.0;
            else if (isIronGolem(target)) damage *= 0.5;
        }
        if ("bug".equals(elementalType)) {
            if (isBlaze(target) || isAllayOrVex(target) || isIronGolem(target)) damage *= 0.5;
            if (target instanceof Raider) damage *= 2.0;
        }
        if ("rock".equals(elementalType)) {
            if (isBlaze(target) || isSnowGolem(target) || isVanillaArthropod(target)) damage *= 2.0;
            if (isIronGolem(target)) damage *= 0.5;
        }
        if ("ghost".equals(elementalType)) {
            if (isAllayOrVex(target)) damage *= 2.0;
            else if (target instanceof Raider) damage *= 0.5;
        }
        if ("dragon".equals(elementalType)) {
            if (isEnderDragon(target)) damage *= 2.0;
            if (isIronGolem(target)) damage *= 0.5;
        }
        if ("dark".equals(elementalType)) {
            if (isAllayOrVex(target)) damage *= 2.0;
            if (target instanceof Raider) damage *= 0.5;
        }
        if ("steel".equals(elementalType)) {
            if (isBlaze(target) || isVanillaAquatic(target) || isIronGolem(target)) damage *= 0.5;
            if (isSnowGolem(target)) damage *= 2.0;
        }
        if ("fairy".equals(elementalType)) {
            if (isBlaze(target) || isIronGolem(target)) damage *= 0.5;
            if (isEnderDragon(target) || target instanceof Raider) damage *= 2.0;
        }
        if (move.isSpecial()) {
            target.hurt(attacker.damageSources().indirectMagic(attacker, attacker), (float) damage);
        } else {
            target.hurt(attacker.damageSources().mobAttack(attacker), (float) damage);
        }
        if ("fire".equals(elementalType)) target.setTicksFrozen(0);
        setCooldown(attacker);
    }

    private static boolean hasNearbyLightningRod(LivingEntity target) {
        BlockPos center = target.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-16, -16, -16), center.offset(16, 16, 16))) {
            if (target.level().getBlockState(pos).is(Blocks.LIGHTNING_ROD)) return true;
        }
        return false;
    }

    private static boolean isSnowOrIronGolem(LivingEntity entity) {
        String n = entity.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        return n.equals("snowgolem") || n.equals("irongolem");
    }

    private static boolean isSnowGolem(LivingEntity entity) {
        return entity.getClass().getSimpleName().equalsIgnoreCase("SnowGolem");
    }

    private static boolean isIronGolem(LivingEntity entity) { return entity.getClass().getSimpleName().equalsIgnoreCase("IronGolem"); }
    private static boolean isAllayOrVex(LivingEntity entity) {
        String n = entity.getClass().getSimpleName();
        return n.equalsIgnoreCase("Allay") || n.equalsIgnoreCase("Vex");
    }
    private static boolean isVex(LivingEntity entity) { return entity.getClass().getSimpleName().equalsIgnoreCase("Vex"); }
    private static boolean isPsychicType(PokemonEntity entity) {
        for (var type : entity.getPokemon().getTypes()) if ("psychic".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }
    private static boolean isFairyType(PokemonEntity entity) { for (var t : entity.getPokemon().getTypes()) if ("fairy".equalsIgnoreCase(t.getShowdownId())) return true; return false; }

    private static boolean isFightingType(PokemonEntity entity) {
        for (var type : entity.getPokemon().getTypes()) {
            if ("fighting".equalsIgnoreCase(type.getShowdownId())) return true;
        }
        return false;
    }
    private static boolean isDarkType(PokemonEntity entity) { for (var t : entity.getPokemon().getTypes()) if ("dark".equalsIgnoreCase(t.getShowdownId())) return true; return false; }
    private static boolean isDarkWildTarget(LivingEntity entity) { String n=entity.getClass().getSimpleName(); return n.equalsIgnoreCase("IronGolem")||n.equalsIgnoreCase("SnowGolem")||n.equalsIgnoreCase("Villager"); }

    private static boolean isBlaze(LivingEntity entity) {
        return entity.getClass().getSimpleName().equalsIgnoreCase("Blaze");
    }

    private static boolean isEnderDragon(LivingEntity entity) {
        return entity.getClass().getSimpleName().equalsIgnoreCase("EnderDragon");
    }

    private static boolean isVanillaArthropod(LivingEntity entity) {
        String n = entity.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        return n.contains("spider") || n.contains("silverfish") || n.contains("endermite") || n.equals("bee");
    }

    private static boolean isVanillaAquatic(LivingEntity entity) {
        String n = entity.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        return n.contains("fish") || n.contains("squid") || n.contains("guardian") || n.equals("dolphin")
                || n.equals("turtle") || n.equals("axolotl") || n.equals("tadpole") || n.equals("frog");
    }

    private static void setCooldown(PokemonEntity entity) {
        int ticks = (int) Math.ceil(entity.getPersistentData().getDouble("xice_attack_interval_ticks"));
        entity.getPersistentData().putInt("xice_world_attack_cooldown", Math.max(10, ticks));
    }

    private PokemonWorldCombatController() { }
}
