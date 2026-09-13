package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.raid.Raider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonEntity.class)
public abstract class FairyPokemonResistanceMixin {
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void xice$fairyDragonImmunity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isFairyType(entity.getPokemon())) return;
        if (source.is(DamageTypes.DRAGON_BREATH) || source.getEntity() instanceof EnderDragon) cir.setReturnValue(false);
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$fairyDamageModifiers(float amount, DamageSource source) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isFairyType(entity.getPokemon())) return amount;
        if (source.is(DamageTypes.WITHER)) amount *= 2.0F;
        if (source.is(DamageTypes.MAGIC) && source.getEntity() == null) amount *= 2.0F;
        if (source.is(DamageTypes.FALLING_BLOCK)) amount *= 2.0F;
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
            if (isArthropod(attacker)) amount *= 0.5F;
            if (attacker instanceof Raider) amount *= 0.5F;
        }
        return amount;
    }
    private static boolean isFairyType(Pokemon p) { if (p==null) return false; for(var t:p.getTypes()) if("fairy".equalsIgnoreCase(t.getShowdownId())) return true; return false; }
    private static boolean isArthropod(net.minecraft.world.entity.LivingEntity e) { String n=e.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT); return n.contains("spider")||n.contains("silverfish")||n.contains("endermite")||n.equals("bee"); }
}
