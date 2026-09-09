package cn.cnxice.cobblemonfix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Data-pack driven output table for the redesigned WorkForce miner. */
public final class WorkForceMinerLootManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private static final WorkForceMinerLootManager INSTANCE = new WorkForceMinerLootManager();
    private static volatile List<Entry> entries = List.of();

    private WorkForceMinerLootManager() {
        super(GSON, "xice_workforce_miner_loot");
    }

    public static void onReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler) {
        List<Entry> loaded = new ArrayList<>();
        objects.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(file -> {
            try {
                JsonObject root = GsonHelper.convertToJsonObject(file.getValue(), file.getKey().toString());
                if (GsonHelper.getAsBoolean(root, "replace", false)) {
                    loaded.clear();
                }
                JsonArray array = GsonHelper.getAsJsonArray(root, "entries");
                for (JsonElement element : array) {
                    JsonObject object = GsonHelper.convertToJsonObject(element, "entry");
                    ResourceLocation id = ResourceLocation.parse(GsonHelper.getAsString(object, "item"));
                    if (!BuiltInRegistries.ITEM.containsKey(id)) {
                        LOGGER.warn("Ignoring unknown WorkForce miner output {} in {}", id, file.getKey());
                        continue;
                    }
                    loaded.add(new Entry(
                            BuiltInRegistries.ITEM.get(id),
                            Math.max(1, GsonHelper.getAsInt(object, "minimum_level", 1)),
                            Math.max(0.0D, GsonHelper.getAsDouble(object, "base_weight", 1.0D)),
                            Math.max(1, GsonHelper.getAsInt(object, "minimum_count", 1)),
                            Math.max(1, GsonHelper.getAsInt(object, "maximum_count", 1)),
                            Math.max(0, GsonHelper.getAsInt(object, "bonus_count_stat_step", 0)),
                            Math.max(0, GsonHelper.getAsInt(object, "maximum_bonus_count", 0))
                    ));
                }
            } catch (RuntimeException exception) {
                LOGGER.error("Could not read WorkForce miner output table {}", file.getKey(), exception);
            }
        });
        entries = List.copyOf(loaded);
        LOGGER.info("Loaded {} WorkForce miner output entries", entries.size());
    }

    public static ItemStack roll(int pokemonLevel, int statTotal, RandomSource random) {
        List<Entry> eligible = entries.stream()
                .filter(entry -> entry.weight() > 0.0D)
                .toList();
        if (eligible.isEmpty()) {
            return ItemStack.EMPTY;
        }

        double totalWeight = eligible.stream().mapToDouble(Entry::weight).sum();
        double target = random.nextDouble() * totalWeight;
        Entry selected = eligible.getLast();
        for (Entry entry : eligible) {
            target -= entry.weight();
            if (target < 0.0D) {
                selected = entry;
                break;
            }
        }
        if (pokemonLevel < selected.minimumLevel()) {
            return new ItemStack(Items.COBBLESTONE);
        }
        int lower = Math.min(selected.minimumCount(), selected.maximumCount());
        int upper = Math.max(selected.minimumCount(), selected.maximumCount());
        int amount = lower + (upper > lower ? random.nextInt(upper - lower + 1) : 0);
        if (selected.bonusCountStatStep() > 0) {
            amount += Math.min(selected.maximumBonusCount(), statTotal / selected.bonusCountStatStep());
        }
        return new ItemStack(selected.item(), Math.min(amount, selected.item().getDefaultMaxStackSize()));
    }

    private record Entry(
            Item item,
            int minimumLevel,
            double weight,
            int minimumCount,
            int maximumCount,
            int bonusCountStatStep,
            int maximumBonusCount
    ) {}
}
