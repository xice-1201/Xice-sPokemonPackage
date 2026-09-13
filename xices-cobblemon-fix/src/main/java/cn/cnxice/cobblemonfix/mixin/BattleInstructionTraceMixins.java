package cn.cnxice.cobblemonfix.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import cn.cnxice.cobblemonfix.combat.WardenAccuracyStageMirror;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.cobblemon.mod.common.battles.interpreter.instructions.SwitchInstruction", remap = false)
class SwitchTrace {
    private static final String WARDEN_SPECIES = "xices_cobblemon_fix:warden_boss";

    @Inject(method = "invoke", at = @At("HEAD"), remap = false)
    private void x(PokemonBattle battle, CallbackInfo callback) {
        var instruction = (com.cobblemon.mod.common.battles.interpreter.instructions.SwitchInstruction) (Object) this;
        var publicMessage = instruction.getPublicMessage();
        var log = LoggerFactory.getLogger("Xice Warden");
        try {
            var sideA = publicMessage.battlePokemon(0, battle);
            var sideB = publicMessage.battlePokemon(1, battle);
            // Stat stages reset when a Pokémon enters the field. Showdown will
            // emit Dark Shroud's -unboost after this instruction, so clear the
            // Java-side mirror first and let that authoritative message refill it.
            WardenAccuracyStageMirror.clear(sideA, "switch");
            WardenAccuracyStageMirror.clear(sideB, "switch");
            log.info("[Warden debug] switch instruction battle={} publicMessage={} sideA={} sideB={}",
                    battle.getBattleId(), publicMessage,
                    sideA == null ? "null" : sideA.getEffectedPokemon().getSpecies().getResourceIdentifier(),
                    sideB == null ? "null" : sideB.getEffectedPokemon().getSpecies().getResourceIdentifier());

            // The Showdown ability is authoritative for the accuracy drop. This
            // trace must not apply a second -2 when the switch instruction runs.
            var warden = sideA != null && WARDEN_SPECIES.equals(sideA.getEffectedPokemon().getSpecies().getResourceIdentifier().toString()) ? sideA : sideB;
            if (warden != null && WARDEN_SPECIES.equals(warden.getEffectedPokemon().getSpecies().getResourceIdentifier().toString())) {
                log.info("[Warden debug] Warden switch observed; accuracy handled by Showdown Dark Shroud");
            }
        } catch (Exception exception) {
            log.warn("[Warden debug] application failed", exception);
        }
    }
}
