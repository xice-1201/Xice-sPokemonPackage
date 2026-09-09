package cn.cnxice.cobblemonfix.mixin;

import cn.cnxice.cobblemonfix.WorkForceMinerAccess;
import cn.cnxice.cobblemonfix.WorkForceMinerLootManager;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.nbp.cobblemon_workforce.block.entity.PokeMinerBlockEntity;
import com.nbp.cobblemon_workforce.pokemon.PokemonWorkforceStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(PokeMinerBlockEntity.class)
public abstract class PokeMinerBlockEntityMixin implements WorkForceMinerAccess {
    private static final int[] XICE_ALL_SLOTS = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26
    };

    @Shadow @Final private List<PokeMinerBlockEntity.StoredPokemon> storedPokemon;
    @Shadow @Final private List<ItemStack> collectionBuffer;
    @Shadow public abstract boolean getNormalSlotUnlocked();

    private final NonNullList<ItemStack> xice$inventory = NonNullList.withSize(27, ItemStack.EMPTY);

    @Invoker("ensureVisiblePokemon")
    protected abstract void xice$ensureVisiblePokemon();

    @Inject(method = "accepts", at = @At("HEAD"), cancellable = true)
    private void xice$acceptAnyPokemon(Pokemon pokemon, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "slotRoleFor", at = @At("HEAD"), cancellable = true)
    private void xice$chooseProductionOrLogisticsSlot(
            Pokemon pokemon,
            CallbackInfoReturnable<PokeMinerBlockEntity.SlotRole> cir
    ) {
        boolean productionOccupied = storedPokemon.stream()
                .anyMatch(stored -> stored.getRole() == PokeMinerBlockEntity.SlotRole.ROCK);
        if (!productionOccupied) {
            cir.setReturnValue(PokeMinerBlockEntity.SlotRole.ROCK);
            return;
        }

        boolean logisticsOccupied = storedPokemon.stream()
                .anyMatch(stored -> stored.getRole() == PokeMinerBlockEntity.SlotRole.NORMAL);
        if (getNormalSlotUnlocked() && !logisticsOccupied && !xice$isGhost(pokemon)) {
            cir.setReturnValue(PokeMinerBlockEntity.SlotRole.NORMAL);
        } else {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "tickMiner", at = @At("HEAD"), cancellable = true)
    private void xice$generateMinerals(ServerLevel level, CallbackInfo ci) {
        ci.cancel();
        xice$ensureVisiblePokemon();

        PokeMinerBlockEntity.StoredPokemon worker = storedPokemon.stream()
                .filter(stored -> stored.getRole() == PokeMinerBlockEntity.SlotRole.ROCK)
                .findFirst()
                .orElse(null);
        if (worker == null
                || !PokemonWorkforceStatus.INSTANCE.isAwake(level, worker.getPcId(), worker.getPokemonId())) {
            return;
        }

        Pokemon pokemon = Cobblemon.INSTANCE.getStorage()
                .getPC(worker.getPcId(), level.registryAccess())
                .get(worker.getPokemonId());
        if (pokemon == null || pokemon.isFainted() || !xice$knowsDig(pokemon)) {
            return;
        }

        int pokemonLevel = Math.max(1, Math.min(100, pokemon.getLevel()));
        int interval = pokemonLevel <= 50
                ? 1500 - ((pokemonLevel - 1) * 625 / 49)
                : 875 - ((pokemonLevel - 50) * 375 / 50);
        if (level.getGameTime() % Math.max(500, interval) != 0L) {
            return;
        }

        int statTotal = pokemon.getStat(Stats.HP)
                + pokemon.getStat(Stats.ATTACK)
                + pokemon.getStat(Stats.DEFENCE)
                + pokemon.getStat(Stats.SPECIAL_ATTACK)
                + pokemon.getStat(Stats.SPECIAL_DEFENCE)
                + pokemon.getStat(Stats.SPEED);
        ItemStack output = WorkForceMinerLootManager.roll(pokemonLevel, statTotal, level.random);
        if (!output.isEmpty() && xice$insert(output).getCount() != output.getCount()) {
            xice$setChanged();
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void xice$loadInventory(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        CompoundTag inventoryTag = tag.getCompound("XiceMinerInventory");
        ContainerHelper.loadAllItems(inventoryTag, xice$inventory, provider);
        if (!collectionBuffer.isEmpty()) {
            for (ItemStack oldStack : collectionBuffer) {
                xice$insert(oldStack.copy());
            }
            collectionBuffer.clear();
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void xice$saveInventory(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        CompoundTag inventoryTag = new CompoundTag();
        ContainerHelper.saveAllItems(inventoryTag, xice$inventory, provider);
        tag.put("XiceMinerInventory", inventoryTag);
    }

    @Inject(method = "dropStoredItems", at = @At("TAIL"))
    private void xice$dropInventory(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        Containers.dropContents(level, pos, this);
        clearContent();
    }

    private static boolean xice$isGhost(Pokemon pokemon) {
        for (var type : pokemon.getTypes()) {
            if ("ghost".equals(type.getName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean xice$knowsDig(Pokemon pokemon) {
        for (MoveTemplate move : pokemon.getMoveSet().getMoveTemplates()) {
            if ("dig".equals(move.getName())) {
                return true;
            }
        }
        for (BenchedMove move : pokemon.getBenchedMoves()) {
            if ("dig".equals(move.getMoveTemplate().getName())) {
                return true;
            }
        }
        return false;
    }

    private ItemStack xice$insert(ItemStack source) {
        ItemStack remainder = source.copy();
        for (ItemStack existing : xice$inventory) {
            if (!remainder.isEmpty() && ItemStack.isSameItemSameComponents(existing, remainder)) {
                int moved = Math.min(remainder.getCount(), existing.getMaxStackSize() - existing.getCount());
                existing.grow(moved);
                remainder.shrink(moved);
            }
        }
        for (int slot = 0; slot < xice$inventory.size() && !remainder.isEmpty(); slot++) {
            if (xice$inventory.get(slot).isEmpty()) {
                int moved = Math.min(remainder.getCount(), remainder.getMaxStackSize());
                xice$inventory.set(slot, remainder.copyWithCount(moved));
                remainder.shrink(moved);
            }
        }
        return remainder;
    }

    @Override
    public boolean xice$logisticsEnabled() {
        return getNormalSlotUnlocked() && storedPokemon.stream()
                .anyMatch(stored -> stored.getRole() == PokeMinerBlockEntity.SlotRole.NORMAL);
    }

    @Override public int getContainerSize() { return xice$inventory.size(); }
    @Override public boolean isEmpty() { return xice$inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return xice$inventory.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(xice$inventory, slot, amount);
        if (!removed.isEmpty()) xice$setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(xice$inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        xice$inventory.set(slot, stack);
        if (stack.getCount() > getMaxStackSize(stack)) stack.setCount(getMaxStackSize(stack));
        xice$setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        PokeMinerBlockEntity self = (PokeMinerBlockEntity) (Object) this;
        return self.getLevel() != null
                && self.getLevel().getBlockEntity(self.getBlockPos()) == self
                && player.distanceToSqr(
                        self.getBlockPos().getX() + 0.5D,
                        self.getBlockPos().getY() + 0.5D,
                        self.getBlockPos().getZ() + 0.5D
                ) <= 64.0D;
    }

    @Override public void clearContent() { xice$inventory.clear(); }
    @Override public int[] getSlotsForFace(Direction side) { return xice$logisticsEnabled() ? XICE_ALL_SLOTS : new int[0]; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) { return false; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return xice$logisticsEnabled(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return false; }

    private void xice$setChanged() {
        ((PokeMinerBlockEntity) (Object) this).setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.xices_cobblemon_fix.poke_miner");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return ChestMenu.threeRows(id, inventory, this);
    }
}
