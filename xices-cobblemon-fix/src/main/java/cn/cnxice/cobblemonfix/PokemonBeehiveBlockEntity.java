package cn.cnxice.cobblemonfix;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Collections;

public final class PokemonBeehiveBlockEntity extends BlockEntity implements WorldlyContainer {
    public static final int GLASS_BOTTLE_SLOT = 0;
    public static final int HONEY_BOTTLE_SLOT = 1;
    private static final int[] INPUT = {GLASS_BOTTLE_SLOT};
    private static final int[] OUTPUT = {HONEY_BOTTLE_SLOT};

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private final List<Worker> workers = new ArrayList<>(3);
    private @Nullable Worker logisticsPokemon;
    private boolean logisticsUpgrade;
    private long nextProductionTick;

    private final FluidTank honey = new FluidTank(PokemonBeehiveConfig.HONEY_CAPACITY_MB.get(),
            stack -> BuiltInRegistries.FLUID.getKey(stack.getFluid()).equals(
                    ResourceLocation.fromNamespaceAndPath("create", "honey"))) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    public PokemonBeehiveBlockEntity(BlockPos pos, BlockState state) {
        super(PokemonBeehiveRegistry.POKEMON_BEEHIVE_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(net.minecraft.world.level.Level rawLevel, BlockPos pos, BlockState state, PokemonBeehiveBlockEntity be) {
        if (!(rawLevel instanceof ServerLevel level)) return;
        be.honey.setCapacity(PokemonBeehiveConfig.HONEY_CAPACITY_MB.get());
        long time = level.getGameTime();
        if (!be.workers.isEmpty() && time >= be.nextProductionTick) {
            int flowers = be.countFlowers(level, pos, PokemonBeehiveConfig.FLOWER_RADIUS.get());
            int amount = 0;
            for (Worker worker : be.workers) {
                amount += worker.species().equals("vespiquen") ? 200 : 50;
            }
            if (amount <= 0) return;
            var fluid = BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("create", "honey"));
            be.honey.fill(new FluidStack(fluid, Math.max(1, amount)), IFluidHandler.FluidAction.EXECUTE);
            int interval = Math.max(20, PokemonBeehiveConfig.PRODUCTION_INTERVAL_TICKS.get() - flowers);
            be.nextProductionTick = time + interval;
            be.setChanged();
        }
        be.bottleHoney();
    }

    private int countFlowers(ServerLevel level, BlockPos center, int radius) {
        int count = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; x++) for (int y = -radius; y <= radius; y++)
            for (int z = -radius; z <= radius; z++) {
                cursor.setWithOffset(center, x, y, z);
                if (level.getBlockState(cursor).is(BlockTags.FLOWERS)) count++;
            }
        return count;
    }

    private void bottleHoney() {
        if (!logisticsEnabled() || !items.get(GLASS_BOTTLE_SLOT).is(Items.GLASS_BOTTLE)) return;
        ItemStack output = items.get(HONEY_BOTTLE_SLOT);
        if (!output.isEmpty() && (!output.is(Items.HONEY_BOTTLE) || output.getCount() >= output.getMaxStackSize())) return;
        int cost = PokemonBeehiveConfig.BOTTLE_COST_MB.get();
        if (honey.getFluidAmount() < cost) return;
        items.get(GLASS_BOTTLE_SLOT).shrink(1);
        if (output.isEmpty()) items.set(HONEY_BOTTLE_SLOT, new ItemStack(Items.HONEY_BOTTLE));
        else output.grow(1);
        honey.drain(cost, IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    public FluidTank honeyTank() { return honey; }
    public IFluidHandler outputFluidHandler() {
        return new IFluidHandler() {
            @Override public int getTanks() { return honey.getTanks(); }
            @Override public FluidStack getFluidInTank(int tank) { return honey.getFluidInTank(tank); }
            @Override public int getTankCapacity(int tank) { return honey.getTankCapacity(tank); }
            @Override public boolean isFluidValid(int tank, FluidStack stack) { return false; }
            @Override public int fill(FluidStack resource, FluidAction action) { return 0; }
            @Override public FluidStack drain(FluidStack resource, FluidAction action) { return honey.drain(resource, action); }
            @Override public FluidStack drain(int maxDrain, FluidAction action) { return honey.drain(maxDrain, action); }
        };
    }
    public boolean logisticsEnabled() { return logisticsUpgrade && logisticsPokemon != null; }
    public List<Worker> workers() { return List.copyOf(workers); }
    public boolean addWorker(Worker worker) {
        if (workers.size() >= 3 || !(worker.species().equals("vespiquen") || worker.species().equals("combee"))) return false;
        if (workers.stream().anyMatch(w -> w.pokemonId().equals(worker.pokemonId()))) return false;
        workers.add(worker); setChanged(); return true;
    }
    public boolean setLogisticsPokemon(Worker worker, boolean ghostType) {
        if (!logisticsUpgrade || ghostType) return false;
        logisticsPokemon = worker; setChanged(); return true;
    }
    public boolean unlockLogistics() {
        if (logisticsUpgrade) return false;
        logisticsUpgrade = true;
        setChanged();
        return true;
    }
    public boolean hasLogisticsUpgrade() { return logisticsUpgrade; }

    public boolean canAddPokemon(Pokemon pokemon) {
        String species = pokemon.getSpecies().getName().toLowerCase(java.util.Locale.ROOT);
        if ((species.equals("combee") || species.equals("vespiquen")) && workers.size() < 3) return true;
        return logisticsUpgrade && logisticsPokemon == null && !isGhost(pokemon);
    }

    public boolean addPokemon(net.minecraft.server.level.ServerPlayer player, Pokemon pokemon, UUID pcId, UUID linkId) {
        String species = pokemon.getSpecies().getName().toLowerCase(java.util.Locale.ROOT);
        Worker worker = new Worker(player.getUUID(), pcId, pokemon.getUuid(), species);
        boolean added = (species.equals("combee") || species.equals("vespiquen")) && workers.size() < 3
                ? addWorker(worker)
                : setLogisticsPokemon(worker, isGhost(pokemon));
        if (added) pokemon.setTetheringId(linkId);
        return added;
    }

    public boolean removePokemon(UUID pokemonId, ServerLevel level) {
        Worker found = workers.stream().filter(w -> w.pokemonId().equals(pokemonId)).findFirst().orElse(null);
        if (found != null) workers.remove(found);
        else if (logisticsPokemon != null && logisticsPokemon.pokemonId().equals(pokemonId)) {
            found = logisticsPokemon; logisticsPokemon = null;
        }
        if (found == null) return false;
        Pokemon pokemon = Cobblemon.INSTANCE.getStorage().getPC(found.pcId(), level.registryAccess()).get(found.pokemonId());
        if (pokemon != null) pokemon.setTetheringId(null);
        setChanged();
        return true;
    }

    public void removeAllPokemon(ServerLevel level) {
        List<UUID> ids = new ArrayList<>();
        workers.forEach(w -> ids.add(w.pokemonId()));
        if (logisticsPokemon != null) ids.add(logisticsPokemon.pokemonId());
        ids.forEach(id -> removePokemon(id, level));
    }

    private static boolean isGhost(Pokemon pokemon) {
        for (var type : pokemon.getTypes()) if ("ghost".equals(type.getName())) return true;
        return false;
    }

    public List<OpenPasturePacket.PasturePokemonDataDTO> toPastureDtos(ServerLevel level) {
        List<OpenPasturePacket.PasturePokemonDataDTO> result = new ArrayList<>();
        List<Worker> all = new ArrayList<>(workers);
        if (logisticsPokemon != null) all.add(logisticsPokemon);
        for (Worker worker : all) {
            Pokemon pokemon = Cobblemon.INSTANCE.getStorage().getPC(worker.pcId(), level.registryAccess()).get(worker.pokemonId());
            if (pokemon == null) continue;
            result.add(new OpenPasturePacket.PasturePokemonDataDTO(
                    pokemon.getUuid(), worker.ownerId(), pokemon.getDisplayName(false),
                    level.getServer().getProfileCache().get(worker.ownerId()).map(p -> p.getName()).orElse("?"),
                    pokemon.getSpecies().getResourceIdentifier(), pokemon.getAspects(), pokemon.getHeldItem$common().copy(),
                    pokemon.getLevel(), false, Collections.emptySet()));
        }
        return result;
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        CompoundTag inventory = new CompoundTag();
        ContainerHelper.saveAllItems(inventory, items, provider);
        tag.put("Inventory", inventory);
        tag.put("Honey", honey.writeToNBT(provider, new CompoundTag()));
        tag.putBoolean("LogisticsUpgrade", logisticsUpgrade);
        tag.putLong("NextProductionTick", nextProductionTick);
        ListTag list = new ListTag(); workers.forEach(w -> list.add(w.toTag())); tag.put("Workers", list);
        if (logisticsPokemon != null) tag.put("LogisticsPokemon", logisticsPokemon.toTag());
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        ContainerHelper.loadAllItems(tag.getCompound("Inventory"), items, provider);
        honey.readFromNBT(provider, tag.getCompound("Honey"));
        logisticsUpgrade = tag.getBoolean("LogisticsUpgrade");
        nextProductionTick = tag.getLong("NextProductionTick");
        workers.clear();
        ListTag list = tag.getList("Workers", 10);
        for (int i = 0; i < list.size() && workers.size() < 3; i++) workers.add(Worker.fromTag(list.getCompound(i)));
        logisticsPokemon = tag.contains("LogisticsPokemon", 10) ? Worker.fromTag(tag.getCompound("LogisticsPokemon")) : null;
    }

    public record Worker(UUID ownerId, UUID pcId, UUID pokemonId, String species) {
        CompoundTag toTag() { CompoundTag t = new CompoundTag(); t.putUUID("Owner", ownerId); t.putUUID("PC", pcId); t.putUUID("Pokemon", pokemonId); t.putString("Species", species); return t; }
        static Worker fromTag(CompoundTag t) { return new Worker(t.getUUID("Owner"), t.getUUID("PC"), t.getUUID("Pokemon"), t.getString("Species")); }
    }

    @Override public int getContainerSize() { return 2; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack s = ContainerHelper.removeItem(items, slot, amount); if (!s.isEmpty()) setChanged(); return s; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getCenter()) <= 64; }
    @Override public void clearContent() { items.clear(); setChanged(); }
    @Override public int[] getSlotsForFace(Direction side) { return logisticsEnabled() ? (side == Direction.DOWN ? OUTPUT : INPUT) : new int[0]; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return logisticsEnabled() && slot == GLASS_BOTTLE_SLOT && stack.is(Items.GLASS_BOTTLE); }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) { return canPlaceItem(slot, stack); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return logisticsEnabled() && slot == HONEY_BOTTLE_SLOT; }
}
