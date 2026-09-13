package cn.cnxice.cobblemonfix.mixin;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
@Mixin(PokemonEntity.class)
public abstract class DarkPokemonResistanceMixin {
 @ModifyVariable(method="hurt",at=@At("HEAD"),argsOnly=true,ordinal=0)
 private float xice$darkDamage(float amount, DamageSource source) {
  if (!isDark(((PokemonEntity)(Object)this).getPokemon())) return amount;
  if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity a) { String n=a.getClass().getSimpleName(); if (arth(a)) amount*=2; if (n.equalsIgnoreCase("Vex")) amount*=.5F; if (a instanceof net.minecraft.world.entity.raid.Raider) amount*=.5F; }
  return amount;
 }
 private static boolean isDark(Pokemon p){if(p==null)return false;for(var t:p.getTypes())if("dark".equalsIgnoreCase(t.getShowdownId()))return true;return false;}
 private static boolean arth(net.minecraft.world.entity.LivingEntity e){String n=e.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);return n.contains("spider")||n.contains("silverfish")||n.contains("endermite")||n.equals("bee");}
}
