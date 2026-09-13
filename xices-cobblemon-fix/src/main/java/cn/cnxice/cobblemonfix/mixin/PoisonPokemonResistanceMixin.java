package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonEntity.class)
public abstract class PoisonPokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$poisonDamageModifiers(float amount, DamageSource source) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isPoisonType(entity.getPokemon())) return amount;
        if (source.is(DamageTypes.WITHER)) amount *= 0.5F;
        if (source.is(DamageTypes.MAGIC) && entity.hasEffect(MobEffects.POISON)) amount = 0.0F;
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker && isVanillaArthropod(attacker)) amount *= 0.5F;
        return amount;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void xice$poisonHealing(CallbackInfo ci) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (entity.level().isClientSide() || !isPoisonType(entity.getPokemon()) || !entity.hasEffect(MobEffects.POISON) || entity.tickCount % 25 != 0) return;
        entity.heal(1.0F);
        Pokemon pokemon = entity.getPokemon();
        if (pokemon != null) pokemon.setCurrentHealth(Math.min(pokemon.getMaxHealth(), pokemon.getCurrentHealth() + 1));
    }

    private static boolean isPoisonType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("poison".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }

    private static boolean isVanillaArthropod(net.minecraft.world.entity.LivingEntity entity) {
        String n = entity.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        return n.contains("spider") || n.contains("silverfish") || n.contains("endermite") || n.equals("bee");
    }
}
