package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.raid.Raider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PokemonEntity.class)
public abstract class PsychicPokemonResistanceMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float xice$psychicDamageModifiers(float amount, DamageSource source) {
        PokemonEntity entity = (PokemonEntity) (Object) this;
        if (!isPsychicType(entity.getPokemon())) return amount;
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
            String n = attacker.getClass().getSimpleName();
            if (attacker instanceof Raider || n.equalsIgnoreCase("Vex") || isArthropod(attacker)) amount *= 2.0F;
        }
        return amount;
    }
    private static boolean isPsychicType(Pokemon pokemon) {
        if (pokemon == null) return false;
        for (var type : pokemon.getTypes()) if ("psychic".equalsIgnoreCase(type.getShowdownId())) return true;
        return false;
    }
    private static boolean isArthropod(net.minecraft.world.entity.LivingEntity entity) {
        String n = entity.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        return n.contains("spider") || n.contains("silverfish") || n.contains("endermite") || n.equals("bee");
    }
}
