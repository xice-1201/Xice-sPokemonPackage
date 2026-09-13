package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.core.registries.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonEntity.class)
public abstract class GhostPokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$ghostDamageModifiers(float amount, DamageSource source) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isGhostType(entity.getPokemon())) return amount;
        if (source.is(DamageTypes.FALLING_BLOCK) || source.is(DamageTypes.IN_WALL)) return 0.0F;
        if (source.is(DamageTypes.WITHER)) amount *= 0.5F;
        if (source.is(DamageTypes.MAGIC) && source.getEntity() == null) amount *= 0.5F;
        if (source.getEntity() instanceof LivingEntity attacker) {
            String n = attacker.getClass().getSimpleName();
            if (isArthropod(attacker) || n.equalsIgnoreCase("Vex") || attacker instanceof Raider) amount *= 2.0F;
            int smite = smiteLevel(attacker);
            if (smite > 0) amount += 2.5F * smite;
        }
        return amount;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void xice$ghostLowHealthBuff(CallbackInfo ci) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (entity.level().isClientSide() || !isGhostType(entity.getPokemon()) || entity.tickCount % 200 != 0) return;
        if (entity.getHealth() < entity.getMaxHealth() * 0.30F) {
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, true));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0, false, true));
        }
    }

    private static int smiteLevel(LivingEntity attacker) {
        var holder = attacker.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SMITE);
        return Math.max(EnchantmentHelper.getItemEnchantmentLevel(holder, attacker.getMainHandItem()), EnchantmentHelper.getItemEnchantmentLevel(holder, attacker.getOffhandItem()));
    }
    private static boolean isGhostType(Pokemon pokemon) { if (pokemon == null) return false; for (var t : pokemon.getTypes()) if ("ghost".equalsIgnoreCase(t.getShowdownId())) return true; return false; }
    private static boolean isArthropod(LivingEntity e) { String n=e.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT); return n.contains("spider")||n.contains("silverfish")||n.contains("endermite")||n.equals("bee"); }
}
