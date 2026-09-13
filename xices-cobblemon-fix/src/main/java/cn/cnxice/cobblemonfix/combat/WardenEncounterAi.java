package cn.cnxice.cobblemonfix.combat;

import com.cobblemon.mod.common.api.battles.model.ai.BattleAI;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.*;
import com.cobblemon.mod.common.battles.ai.RandomBattleAI;

/** One instance per encounter; called by Cobblemon's normal request handler. */
public final class WardenEncounterAi implements BattleAI {
    private final BattleAI fallback = new RandomBattleAI();
    @Override
    public ShowdownActionResponse choose(ActiveBattlePokemon active, PokemonBattle battle,
            BattleSide side, ShowdownMoveset moveset, boolean forceSwitch) {
        var target = active.getOppositeOpponent();
        int targetAccuracyStage = target instanceof ActiveBattlePokemon opponent && opponent.getBattlePokemon() != null
                ? opponent.getBattlePokemon().getStatChanges().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.ACCURACY, 0)
                : 0;
        boolean entryLock = WardenBattleAiPolicy.opponentJustEntered(battle, active, forceSwitch);
        boolean lockAvailable = moveset.getMoves().stream().anyMatch(m -> "soniclock".equals(m.getId()) && m.canBeUsed());
        boolean darkSurgeEligible = WardenBattleAiPolicy.darkSurgeEligible(active);
        String desired = entryLock && lockAvailable ? "soniclock" : switch (WardenBattleAiPolicy.chooseNormalAction(net.minecraft.util.RandomSource.create(),
                darkSurgeEligible)) {
            case DARK_SURGE -> "darksurge";
            case WARDEN_PUNCH -> "wardenpunch";
            case SONIC_WAVE -> "sonicwave";
            case SONIC_LOCK -> "soniclock";
        };
        WardenBossEncounterHandler.debug("AI request battle=" + battle.getBattleId()
                + " actor=" + active.getActor().getUuid() + " turn=" + battle.getTurn()
                + " forceSwitch=" + forceSwitch + " desired=" + desired + " available="
                + " targetAccuracyStage=" + targetAccuracyStage + " darkSurgeEligible=" + darkSurgeEligible
                + " entryLock=" + entryLock + " lockAvailable=" + lockAvailable
                + moveset.getMoves().stream().map(m -> m.getId() + ":pp=" + m.getPp()
                        + ":disabled=" + m.getDisabled()).toList());
        if (!forceSwitch && target != null) {
            for (var move : moveset.getMoves()) {
                if (desired.equals(move.getId()) && move.canBeUsed()) {
                    WardenBossEncounterHandler.debug("AI action battle=" + battle.getBattleId() + " move=" + desired);
                    String targetPnx = "darksurge".equals(desired) ? null : target.getPNX();
                    return new MoveActionResponse(desired, targetPnx, "");
                }
            }
            if (lockAvailable) {
                for (var move : moveset.getMoves()) {
                    if ("soniclock".equals(move.getId()) && move.canBeUsed()) {
                        WardenBossEncounterHandler.debug("AI forced fallback battle=" + battle.getBattleId() + " move=soniclock");
                        return new MoveActionResponse("soniclock", target.getPNX(), "");
                    }
                }
            }
        }
        var response = fallback.choose(active, battle, side, moveset, forceSwitch);
        WardenBossEncounterHandler.debug("AI fallback battle=" + battle.getBattleId() + " response=" + response);
        return response;
    }
}
