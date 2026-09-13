package cn.cnxice.cobblemonfix.mixin;

import cn.cnxice.cobblemonfix.BattleAttributeConfig;
import cn.cnxice.cobblemonfix.combat.PokemonWorldCombatController;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Bridges effective Cobblemon stats (including IV/EV/nature) into MC attributes. */
@Mixin(PokemonEntity.class)
public abstract class PokemonBattleAttributesMixin {
    @Unique private int xice$lastHp = Integer.MIN_VALUE;
    @Unique private int xice$lastAttack = Integer.MIN_VALUE;
    @Unique private int xice$lastDefence = Integer.MIN_VALUE;
    @Unique private int xice$lastSpecialDefence = Integer.MIN_VALUE;
    @Unique private int xice$lastSpeed = Integer.MIN_VALUE;
    @Unique private float xice$healthRatio = 1.0F;

    @Inject(method = "tick", at = @At("HEAD"))
    private void xice$syncBattleAttributes(CallbackInfo ci) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        Pokemon pokemon = entity.getPokemon();
        if (pokemon == null || entity.level().isClientSide()) return;

        // Capture the faint state before Cobblemon's zero-timer wake-up clears it.
        if (pokemon.isFainted() && entity.getPersistentData().getBoolean("xice_world_combat_active")) {
            entity.getPersistentData().putBoolean("xice_world_combat_disabled", true);
            entity.setTarget(null);
            entity.getNavigation().stop();
        }

        int hp = Math.max(1, pokemon.getMaxHealth());
        int attack = Math.max(1, pokemon.getAttack());
        int defence = Math.max(1, pokemon.getDefence());
        int specialDefence = Math.max(1, pokemon.getSpecialDefence());
        int speed = Math.max(1, pokemon.getSpeed());
        boolean changed = hp != xice$lastHp || attack != xice$lastAttack || defence != xice$lastDefence
                || specialDefence != xice$lastSpecialDefence || speed != xice$lastSpeed;
        if (changed) {
            float ratio = entity.getMaxHealth() > 0.0F ? entity.getHealth() / entity.getMaxHealth() : 1.0F;
            xice$set(entity, Attributes.MAX_HEALTH, Math.max(BattleAttributeConfig.HP_MIN.get(),
                    Math.min(BattleAttributeConfig.HP_MAX.get(), 4.0 + Math.sqrt(hp) * BattleAttributeConfig.HP_SCALE.get())));
            xice$set(entity, Attributes.ATTACK_DAMAGE, Math.max(BattleAttributeConfig.ATTACK_MIN.get(),
                    Math.min(BattleAttributeConfig.ATTACK_MAX.get(), 1.0 + attack / BattleAttributeConfig.ATTACK_SCALE.get())));
            entity.getPersistentData().putDouble("xice_magic_attack_damage",
                    Math.max(BattleAttributeConfig.ATTACK_MIN.get(), Math.min(BattleAttributeConfig.ATTACK_MAX.get(),
                            1.0 + pokemon.getSpecialAttack() / BattleAttributeConfig.ATTACK_SCALE.get())));
            double baseArmor = Math.min(24.0, 2.0 + Math.sqrt(defence) * 0.8);
            double baseToughness = Math.min(8.0, Math.sqrt(defence) * 0.25);
            xice$set(entity, Attributes.ARMOR, baseArmor);
            xice$set(entity, Attributes.ARMOR_TOUGHNESS, baseToughness);
            entity.getPersistentData().putDouble("xice_base_armor", baseArmor);
            entity.getPersistentData().putDouble("xice_base_armor_toughness", baseToughness);
            double walkCoefficient = Math.max(BattleAttributeConfig.SPEED_MIN.get(),
                    Math.min(BattleAttributeConfig.SPEED_MAX.get(), 0.50 + speed / BattleAttributeConfig.SPEED_SCALE.get()));
            walkCoefficient *= 0.9;
            double movementModeMultiplier = (entity.isFlying() || entity.isInLiquid()) ? 1.2 : 1.0;
            xice$set(entity, Attributes.MOVEMENT_SPEED, walkCoefficient * movementModeMultiplier);
            double attackInterval = Math.max(BattleAttributeConfig.ATTACK_INTERVAL_MIN_TICKS.get(),
                    Math.min(BattleAttributeConfig.ATTACK_INTERVAL_MAX_TICKS.get(),
                            BattleAttributeConfig.ATTACK_INTERVAL_SCALE.get() / speed));
            xice$set(entity, Attributes.ATTACK_SPEED, 20.0 / attackInterval);
            entity.getPersistentData().putDouble("xice_attack_interval_ticks", attackInterval);
            entity.setHealth(Math.min(entity.getMaxHealth(), Math.max(0.0F, ratio * entity.getMaxHealth())));
            xice$lastHp = hp; xice$lastAttack = attack; xice$lastDefence = defence;
            xice$lastSpecialDefence = specialDefence; xice$lastSpeed = speed;
        }
        float targetRatio = pokemon.getMaxHealth() <= 0 ? 0.0F : (float) pokemon.getCurrentHealth() / pokemon.getMaxHealth();
        if (Math.abs(targetRatio - xice$healthRatio) > 0.0001F || pokemon.getCurrentHealth() == 0) {
            entity.setHealth(Math.max(0.0F, Math.min(entity.getMaxHealth(), targetRatio * entity.getMaxHealth())));
            xice$healthRatio = targetRatio;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void xice$worldCombat(CallbackInfo ci) {
        PokemonWorldCombatController.tick((PokemonEntity) (Object) this);
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$applySpecialDefence(float amount, DamageSource source) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        int specialDefence = Math.max(1, entity.getPokemon().getSpecialDefence());
        if (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC) || source.is(DamageTypes.DRAGON_BREATH)) {
            return (float) (amount / (1.0 + specialDefence / BattleAttributeConfig.SPECIAL_DEFENCE_MAGIC_SCALE.get()));
        }
        return amount;
    }

    @Unique
    private static void xice$set(PokemonEntity entity, Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) instance.setBaseValue(value);
    }
}
