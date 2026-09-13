package cn.cnxice.cobblemonfix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public final class ProxyTradingStationBlockEntity extends BlockEntity implements WorldlyContainer {
    private static final int INPUT_A_SLOT = 0;
    private static final int INPUT_B_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int[] ALL_SLOTS = {INPUT_A_SLOT, INPUT_B_SLOT, OUTPUT_SLOT};
    private final NonNullList<ItemStack> cache = NonNullList.withSize(3, ItemStack.EMPTY);
    private ItemStack requiredA = ItemStack.EMPTY;
    private ItemStack requiredB = ItemStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;
    private long nextWorkTick;

    public ProxyTradingStationBlockEntity(BlockPos pos, BlockState state) {
        super(TradingAutomationRegistry.PROXY_TRADING_STATION_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(net.minecraft.world.level.Level rawLevel, BlockPos pos, BlockState state,
                                  ProxyTradingStationBlockEntity station) {
        if (!(rawLevel instanceof ServerLevel level) || station.requiredA.isEmpty() || station.result.isEmpty()) return;
        long now = level.getGameTime();
        if (now < station.nextWorkTick) return;
        station.nextWorkTick = now + TradingAutomationConfig.WORK_INTERVAL_TICKS.get();
        if (!TradingAutomationHandler.hasOwnedIndeedee(level, pos, TradingAutomationConfig.WORK_RADIUS.get())) return;
        if (!station.hasRequiredInputs() || !station.canAcceptResult()) return;
        station.consumeInputs();
        if (station.cache.get(OUTPUT_SLOT).isEmpty()) station.cache.set(OUTPUT_SLOT, station.result.copy());
        else station.cache.get(OUTPUT_SLOT).grow(station.result.getCount());
        station.setChanged();
    }

    private boolean hasRequiredInputs() {
        return hasEnough(cache.get(INPUT_A_SLOT), requiredA)
                && (requiredB.isEmpty() || hasEnough(cache.get(INPUT_B_SLOT), requiredB));
    }

    private boolean canAcceptResult() {
        ItemStack output = cache.get(OUTPUT_SLOT);
        return output.isEmpty()
                || (ItemStack.isSameItemSameComponents(output, result)
                && output.getCount() + result.getCount() <= output.getMaxStackSize());
    }

    private static boolean hasEnough(ItemStack available, ItemStack required) {
        return !required.isEmpty() && ItemStack.isSameItemSameComponents(available, required)
                && available.getCount() >= required.getCount();
    }

    private void consumeInputs() {
        cache.get(INPUT_A_SLOT).shrink(requiredA.getCount());
        if (!requiredB.isEmpty()) cache.get(INPUT_B_SLOT).shrink(requiredB.getCount());
    }

    public void recordTrade(ItemStack list, HolderLookup.Provider provider) {
        CompoundTag tag = TradeListItem.customTag(list);
        ItemStack newA = ItemStack.parse(provider, tag.getCompound(TradeListItem.INPUT_A)).orElse(ItemStack.EMPTY);
        ItemStack newB = tag.contains(TradeListItem.INPUT_B)
                ? ItemStack.parse(provider, tag.getCompound(TradeListItem.INPUT_B)).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        ItemStack newResult = ItemStack.parse(provider, tag.getCompound(TradeListItem.OUTPUT)).orElse(ItemStack.EMPTY);
        if (newA.isEmpty() || newResult.isEmpty()) return;
        if (!ItemStack.isSameItemSameComponents(requiredA, newA)
                || !ItemStack.isSameItemSameComponents(requiredB, newB)
                || !ItemStack.isSameItemSameComponents(result, newResult)
                || requiredA.getCount() != newA.getCount()
                || requiredB.getCount() != newB.getCount()
                || result.getCount() != newResult.getCount()) {
            dropContents(level, worldPosition);
            cache.clear();
        }
        requiredA = newA;
        requiredB = newB;
        result = newResult;
        nextWorkTick = 0;
        setChanged();
    }

    public void dropContents(net.minecraft.world.level.Level level, BlockPos pos) {
        net.minecraft.world.Containers.dropContents(level, pos, this);
    }

    @Override public int getContainerSize() { return cache.size(); }
    @Override public boolean isEmpty() { return cache.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return cache.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(cache, slot, amount);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(cache, slot); }
    @Override public void setItem(int slot, ItemStack stack) { cache.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getCenter()) <= 64;
    }
    @Override public void clearContent() { cache.clear(); setChanged(); }
    @Override public int[] getSlotsForFace(Direction side) { return ALL_SLOTS; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == INPUT_A_SLOT) return !requiredA.isEmpty()
                && ItemStack.isSameItemSameComponents(requiredA, stack);
        if (slot == INPUT_B_SLOT) return !requiredB.isEmpty()
                && ItemStack.isSameItemSameComponents(requiredB, stack);
        return false;
    }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(slot, stack);
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return !stack.isEmpty();
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        CompoundTag inventory = new CompoundTag();
        ContainerHelper.saveAllItems(inventory, cache, provider);
        tag.put("Cache", inventory);
        if (!requiredA.isEmpty()) tag.put("RequiredA", requiredA.save(provider));
        if (!requiredB.isEmpty()) tag.put("RequiredB", requiredB.save(provider));
        if (!result.isEmpty()) tag.put("Result", result.save(provider));
        tag.putLong("NextWorkTick", nextWorkTick);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        ContainerHelper.loadAllItems(tag.getCompound("Cache"), cache, provider);
        requiredA = ItemStack.parseOptional(provider, tag.getCompound("RequiredA"));
        requiredB = ItemStack.parseOptional(provider, tag.getCompound("RequiredB"));
        result = ItemStack.parseOptional(provider, tag.getCompound("Result"));
        nextWorkTick = tag.getLong("NextWorkTick");
    }
}
