package cn.cnxice.cobblemonfix.combat;

import cn.cnxice.cobblemonfix.XicesCobblemonFix;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public final class CombatEffectsRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, XicesCobblemonFix.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> ATTENTION_TRANSFER = EFFECTS.register(
            "attention_transfer", AttentionTransferEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> BATTLE_FOCUS = EFFECTS.register(
            "battle_focus", BattleFocusEffect::new);

    private CombatEffectsRegistry() { }
}
