package cn.cnxice.smartphoneprogression;

import com.github.yajatkaul.mega_showdown.components.InventoryStorage;
import com.github.yajatkaul.mega_showdown.components.MegaShowdownDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Map;

public final class TeraPouchPickupHandler {
    private static final int POUCH_SIZE = 19;
    private static final Map<ResourceLocation, Integer> SHARD_SLOTS = Map.ofEntries(
            Map.entry(id("bug_tera_shard"), 0),
            Map.entry(id("fighting_tera_shard"), 1),
            Map.entry(id("fairy_tera_shard"), 2),
            Map.entry(id("electric_tera_shard"), 3),
            Map.entry(id("dragon_tera_shard"), 4),
            Map.entry(id("dark_tera_shard"), 5),
            Map.entry(id("water_tera_shard"), 6),
            Map.entry(id("flying_tera_shard"), 7),
            Map.entry(id("fire_tera_shard"), 8),
            Map.entry(id("ghost_tera_shard"), 9),
            Map.entry(id("grass_tera_shard"), 10),
            Map.entry(id("ground_tera_shard"), 11),
            Map.entry(id("ice_tera_shard"), 12),
            Map.entry(id("normal_tera_shard"), 13),
            Map.entry(id("poison_tera_shard"), 14),
            Map.entry(id("psychic_tera_shard"), 15),
            Map.entry(id("rock_tera_shard"), 16),
            Map.entry(id("steel_tera_shard"), 17),
            Map.entry(id("stellar_tera_shard"), 18)
    );

    private TeraPouchPickupHandler() {
    }

    public static void onPickup(ItemEntityPickupEvent.Pre event) {
        if (event.getPlayer().level().isClientSide()) {
            return;
        }

        ItemEntity entity = event.getItemEntity();
        ItemStack shards = entity.getItem();
        Integer shardSlot = SHARD_SLOTS.get(BuiltInRegistries.ITEM.getKey(shards.getItem()));
        if (shardSlot == null || shards.isEmpty()) {
            return;
        }

        int originalCount = shards.getCount();
        boolean[] hasEquippedPouch = {false};
        CuriosApi.getCuriosInventory(event.getPlayer()).ifPresent(inventory ->
                inventory.getStacksHandler("portable").ifPresent(handler -> {
                    for (int index = 0; index < handler.getStacks().getSlots() && !shards.isEmpty(); index++) {
                        ItemStack candidate = handler.getStacks().getStackInSlot(index);
                        if (isTeraPouch(candidate)) {
                            hasEquippedPouch[0] = true;
                            insert(candidate, shards, shardSlot, event.getPlayer().level().registryAccess());
                        }
                    }
                }));

        int absorbed = originalCount - shards.getCount();
        if (!hasEquippedPouch[0]) {
            return;
        }

        if (absorbed > 0) {
            event.getPlayer().take(entity, absorbed);
            event.getPlayer().awardStat(Stats.ITEM_PICKED_UP.get(shards.getItem()), absorbed);
            if (shards.isEmpty()) {
                entity.discard();
            } else {
                entity.setItem(shards);
            }
        }
        event.setCanPickup(TriState.FALSE);
    }

    private static void insert(ItemStack pouch, ItemStack shards, int slot,
                               HolderLookup.Provider registries) {
        DataComponentType<InventoryStorage> component = MegaShowdownDataComponents.INVENTORY.get();
        InventoryStorage storage = pouch.getOrDefault(component, InventoryStorage.defaultStorage(POUCH_SIZE));
        SimpleContainer contents = storage.getInventory(registries);
        ItemStack stored = contents.getItem(slot);
        int capacity = stored.isEmpty() ? shards.getMaxStackSize() : stored.getMaxStackSize() - stored.getCount();
        int moved = Math.min(capacity, shards.getCount());
        if (moved <= 0 || (!stored.isEmpty() && !ItemStack.isSameItemSameComponents(stored, shards))) {
            return;
        }

        if (stored.isEmpty()) {
            ItemStack inserted = shards.copy();
            inserted.setCount(moved);
            contents.setItem(slot, inserted);
        } else {
            stored.grow(moved);
            contents.setChanged();
        }
        shards.shrink(moved);
        pouch.set(component, storage.save(registries, contents));
    }

    private static boolean isTeraPouch(ItemStack stack) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return "mega_showdown".equals(key.getNamespace()) && key.getPath().startsWith("tera_pouch_");
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("mega_showdown", path);
    }
}
