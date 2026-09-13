package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.projectile.Snowball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonEntity.class)
public abstract class GroundPokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$groundDamageModifiers(float amount, DamageSource source) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isGroundType(entity.getPokemon())) return amount;
        if (source.is(DamageTypes.LIGHTNING_BOLT)) { entity.getPersistentData().putBoolean("xice_ground_lightning", true); return 0.0F; }
        if (source.is(DamageTypes.FREEZE)) amount *= 2.0F;
        if (source.is(DamageTypes.WITHER)) amount *= 0.5F;
        if (source.is(DamageTypes.MAGIC) && source.getEntity() == null) amount *= 0.5F;
        if (source.getDirectEntity() instanceof Snowball) amount += 2.0F;
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
            String n = attacker.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
            if (n.contains("fish") || n.contains("squid") || n.contains("guardian") || n.equals("dolphin") || n.equals("turtle") || n.equals("axolotl") || n.equals("tadpole") || n.equals("frog")) amount *= 2.0F;
            if (n.contains("spider") || n.contains("silverfish") || n.contains("endermite") || n.equals("bee")) amount *= 0.5F;
        }
        return amount;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void xice$groundLightningFire(CallbackInfo ci) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!entity.level().isClientSide() && isGroundType(entity.getPokemon()) && entity.getPersistentData().getBoolean("xice_ground_lightning")) {
            entity.clearFire();
            entity.getPersistentData().remove("xice_ground_lightning");
        }
    }

    private static boolean isGroundType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("ground".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }
}
