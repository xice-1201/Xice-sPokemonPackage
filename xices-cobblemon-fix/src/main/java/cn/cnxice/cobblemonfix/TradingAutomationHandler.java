package cn.cnxice.cobblemonfix;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.OriginalTrainerType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;

public final class TradingAutomationHandler {
    public static void onTrade(TradeWithVillagerEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) return;
        MerchantOffer offer = event.getMerchantOffer();
        CompoundTag inputA = saveNonEmpty(offer.getCostA(), level);
        CompoundTag inputB = saveNonEmpty(offer.getCostB(), level);
        CompoundTag output = saveNonEmpty(offer.getResult(), level);
        if (inputA.isEmpty() || output.isEmpty()) return;
        Player player = event.getEntity();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(TradingAutomationRegistry.TRADE_LIST.get()) && !TradeListItem.isRecorded(stack)) {
                TradeListItem.setRecorded(stack, inputA, inputB, output);
            }
        }
    }

    private static CompoundTag saveNonEmpty(ItemStack stack, ServerLevel level) {
        if (stack == null || stack.isEmpty()) return new CompoundTag();
        return (CompoundTag) stack.save(level.registryAccess());
    }

    public static boolean hasOwnedIndeedee(ServerLevel level, net.minecraft.core.BlockPos pos, int radius) {
        var box = new net.minecraft.world.phys.AABB(pos).inflate(radius);
        for (PokemonEntity entity : level.getEntitiesOfClass(PokemonEntity.class, box)) {
            Pokemon pokemon = entity.getPokemon();
            String species = pokemon.getSpecies().getResourceIdentifier().getPath();
            if ((species.equals("indeedee") || species.equals("indeedee_f"))
                    && pokemon.getOwnerUUID() != null
                    && pokemon.getOriginalTrainerType() == OriginalTrainerType.PLAYER
                    && !entity.isBattleClone()) return true;
        }
        return false;
    }

    private TradingAutomationHandler() {}
}
