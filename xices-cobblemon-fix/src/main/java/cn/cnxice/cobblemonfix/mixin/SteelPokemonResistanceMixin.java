package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PokemonEntity.class)
public abstract class SteelPokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$steelDamageModifiers(float amount, DamageSource source) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isSteelType(entity.getPokemon())) return amount;
        if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.LAVA)) amount *= 2.0F;
        if (source.is(DamageTypes.FREEZE)) amount *= 0.5F;
        if (source.is(DamageTypes.WITHER)) return 0.0F;
        if (source.is(DamageTypes.MAGIC) && entity.hasEffect(MobEffects.POISON)) return 0.0F;
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
            String n = attacker.getClass().getSimpleName();
            if (isArthropod(attacker)) amount *= 0.5F;
            if (attacker instanceof EnderDragon || n.equalsIgnoreCase("IronGolem")) amount *= 0.5F;
        }
        return amount;
    }
    private static boolean isSteelType(Pokemon p) { if (p==null) return false; for(var t:p.getTypes()) if("steel".equalsIgnoreCase(t.getShowdownId())) return true; return false; }
    private static boolean isArthropod(net.minecraft.world.entity.LivingEntity e) { String n=e.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT); return n.contains("spider")||n.contains("silverfish")||n.contains("endermite")||n.equals("bee"); }
}
