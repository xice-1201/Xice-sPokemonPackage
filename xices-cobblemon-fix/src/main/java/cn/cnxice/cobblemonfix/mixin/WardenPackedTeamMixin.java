package cn.cnxice.cobblemonfix.mixin;

import cn.cnxice.cobblemonfix.BattleAttributeConfig;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import java.util.List;
import java.util.HashSet;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Inspect the actual packed team, without touching the Graal service lifecycle. */
@Mixin(value = BattleRegistry.class, remap = false)
public abstract class WardenPackedTeamMixin {
    @Inject(method = "packTeam(Ljava/util/List;)Ljava/lang/String;", at = @At("RETURN"), cancellable = true, remap = false)
    private void xice$verifyAbility(List<? extends BattlePokemon> team, CallbackInfoReturnable<String> cir) {
        var ids = new HashSet<String>();
        for (var entry : team) {
            var pokemon = entry.getEffectedPokemon();
            if ("xices_cobblemon_fix:warden_boss".equals(pokemon.getSpecies().getResourceIdentifier().toString()))
                ids.add(pokemon.getUuid().toString());
        }
        if (ids.isEmpty()) return;
        String before = cir.getReturnValue();
        String[] entries = before.split("\\]", -1);
        boolean changed = false;
        for (int i = 0; i < entries.length; i++) {
            String[] fields = entries[i].split("\\|", -1);
            if (fields.length < 9 || !ids.contains(fields[2])) continue;
            String original = fields[7];
            if (!"xicedarkshroud".equals(original)) {
                fields[7] = "xicedarkshroud";
                entries[i] = String.join("|", fields);
                changed = true;
            }
            if (BattleAttributeConfig.WARDEN_BATTLE_DEBUG_LOG.get())
                LoggerFactory.getLogger("Xice Warden").info(
                    "[Warden debug] packTeam uuid={} abilityBefore={} abilityAfter={} corrected={} packedEntry={}",
                    fields[2], original, fields[7], !original.equals(fields[7]), entries[i]);
        }
        if (changed) cir.setReturnValue(String.join("]", entries));
    }
}
