package cn.cnxice.cobblemonfix.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Makes Create: Ages' recipe overrides compatible with NeoForge 1.21.1.
 *
 * <p>Create: Ages 0.6.1 uses the obsolete {@code neoforge:override} recipe
 * placeholder to remove recipes. It also ships optional-integration recipes
 * without load conditions. Both forms otherwise produce a large number of
 * recipe parsing errors.</p>
 */
@Mixin(RecipeManager.class)
public abstract class CreateAgesRecipeMixin {
    private static final String CREATE_AGES = "createages";
    private static final String OVERRIDE_RECIPE = "neoforge:override";
    private static final String ANDESITE_MECHANISM = "createages:andesite_mechanism";
    private static final String ANDESITE_TEMPLATE = "createages:andesite_template";
    private static final String ANDESITE_MACHINE = "createages:andesite_machine";
    private static final String COPPER_MACHINE = "createages:copper_machine";
    private static final String ZINC_MECHANISM = "createages:zinc_mechanism";
    private static final String ZINC_MACHINE = "createages:zinc_machine";
    private static final String INCOMPLETE_ZINC_MECHANISM = "createages:incomplete_zinc_mechanism";
    private static final String INCOMPLETE_ZINC_MACHINE = "createages:incomplete_zinc_machine";
    private static final Set<String> DISABLED_ECONOMY_CONTENT = Set.of(
            "createages:package_assembler",
            "createages:port_station",
            "createages:shop",
            "createages:andesite_package",
            "createages:supply_package",
            "createages:machine_package",
            "createages:resource_bundle",
            "createages:mechanism_bundle",
            "createages:rarity_bundle",
            "createages:ae2_bundle"
    );
    private static final ResourceLocation CUSTOM_ANDESITE_MACHINE_RECIPE =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "andesite_machine_sequenced_assembly");
    private static final ResourceLocation CUSTOM_COPPER_MACHINE_RECIPE =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "copper_machine_sequenced_assembly");
    private static final ResourceLocation CUSTOM_ZINC_MACHINE_RECIPE =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "zinc_machine_sequenced_assembly");
    private static final ResourceLocation CUSTOM_ZINC_MECHANISM_RECIPE =
            ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "zinc_mechanism_sequenced_assembly");
    private static final Set<ResourceLocation> ALLOWED_WORKFORCE_RECIPES = Set.of(
            xice$recipe("workforce/poke_miner"),
            xice$recipe("workforce/slot_upgrade_sequenced_assembly"),
            xice$recipe("workforce/water_slot_upgrade_stonecutting"),
            xice$recipe("workforce/normal_slot_upgrade_stonecutting"),
            xice$recipe("workforce/plant_slot_upgrade_stonecutting"),
            xice$recipe("workforce/water_slot_upgrade_cutting"),
            xice$recipe("workforce/normal_slot_upgrade_cutting"),
            xice$recipe("workforce/plant_slot_upgrade_cutting")
    );
    private static final Set<ResourceLocation> DISABLED_RECIPES = Set.of(
            ResourceLocation.fromNamespaceAndPath("twilightforest", "uncrafting_table"),
            ResourceLocation.fromNamespaceAndPath(CREATE_AGES, "andesite_age/crafting/kinetics/andesite_machine"),
            ResourceLocation.fromNamespaceAndPath(CREATE_AGES, "andesite_age/crafting/kinetics/andesite_mechanism"),
            ResourceLocation.fromNamespaceAndPath(CREATE_AGES, "andesite_age/sequence/andesite_mechanism")
    );
    private static final Set<String> OPTIONAL_RECIPE_NAMESPACES = Set.of(
            "createaddition",
            "createcasing",
            "createcobblestone",
            "functionalstorage",
            "sophisticatedbackpacks",
            "sophisticatedstorage",
            "storagedrawers",
            "waterwheelbearing"
    );
    private static final Map<ResourceLocation, ResourceLocation> RESTORED_CREATE_RECIPES = Map.ofEntries(
            xice$restoredRecipe("crafting/kinetics/cart_assembler"),
            xice$restoredRecipe("crafting/kinetics/chain_conveyor"),
            xice$restoredRecipe("crafting/kinetics/contraption_controls"),
            xice$restoredRecipe("crafting/kinetics/deployer"),
            xice$restoredRecipe("crafting/kinetics/encased_fan"),
            xice$restoredRecipe("crafting/kinetics/gantry_carriage"),
            xice$restoredRecipe("crafting/kinetics/mechanical_bearing"),
            xice$restoredRecipe("crafting/kinetics/mechanical_drill"),
            xice$restoredRecipe("crafting/kinetics/mechanical_mixer"),
            xice$restoredRecipe("crafting/kinetics/mechanical_piston"),
            xice$restoredRecipe("crafting/kinetics/mechanical_press"),
            xice$restoredRecipe("crafting/kinetics/mechanical_roller"),
            xice$restoredRecipe("crafting/kinetics/mechanical_saw"),
            xice$restoredRecipe("crafting/kinetics/millstone"),
            xice$restoredRecipe("crafting/logistics/package_frogport"),
            xice$restoredRecipe("crafting/logistics/packager"),
            xice$restoredRecipe("crafting/kinetics/propeller"),
            xice$restoredRecipe("crafting/kinetics/rope_pulley"),
            xice$restoredRecipe("crafting/kinetics/steam_engine"),
            xice$restoredRecipe("crafting/kinetics/whisk"),
            xice$restoredRecipe("crafting/kinetics/windmill_bearing"),
            xice$restoredRecipe("sequenced_assembly/precision_mechanism")
    );
    private static final Map<String, CutOutput> ANDESITE_MACHINE_OUTPUTS = xice$andesiteMachineOutputs();
    private static final Map<String, CutOutput> ZINC_MACHINE_OUTPUTS = xice$zincMachineOutputs();

    @ModifyVariable(method = "apply", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Map<ResourceLocation, JsonElement> xice$filterInvalidCreateAgesRecipes(Map<ResourceLocation, JsonElement> recipes) {
        Map<ResourceLocation, JsonElement> patched = new HashMap<>(recipes);
        if (ModList.get().isLoaded(CREATE_AGES)) {
            RESTORED_CREATE_RECIPES.forEach((originalId, bundledId) -> {
                JsonElement restored = patched.get(bundledId);
                if (restored != null) {
                    patched.put(originalId, restored.deepCopy());
                    patched.put(bundledId, xice$falseCondition());
                }
            });
        }
        patched.replaceAll((id, json) -> {
            JsonElement modernized = xice$modernizeCreateCompressedRecipe(id, json);
            return xice$shouldDisable(id, modernized) ? xice$falseCondition() : modernized;
        });
        xice$addAndesiteMachineCuttingRecipes(patched);
        xice$addCopperMachineCuttingRecipes(patched);
        xice$addZincMachineCuttingRecipes(patched);
        xice$addHopperBotanyPotRecipes(patched);
        return patched;
    }

    /** Converts Create Compressed 2.2.0's Create 5 recipe fields to Create 6. */
    private static JsonElement xice$modernizeCreateCompressedRecipe(ResourceLocation id, JsonElement json) {
        if (!"create_compressed".equals(id.getNamespace()) || !json.isJsonObject()) {
            return json;
        }

        JsonObject recipe = json.getAsJsonObject().deepCopy();
        JsonElement results = recipe.get("results");
        if (results != null && results.isJsonArray()) {
            for (JsonElement result : results.getAsJsonArray()) {
                if (result.isJsonObject()) {
                    JsonObject resultObject = result.getAsJsonObject();
                    if (!resultObject.has("id") && resultObject.has("item")) {
                        resultObject.add("id", resultObject.remove("item"));
                    }
                }
            }
        }

        JsonElement result = recipe.get("result");
        if (result != null && result.isJsonObject()) {
            JsonObject resultObject = result.getAsJsonObject();
            if (!resultObject.has("id") && resultObject.has("item")) {
                resultObject.add("id", resultObject.remove("item"));
            }
        }

        JsonElement ingredients = recipe.get("ingredients");
        if (ingredients != null && ingredients.isJsonArray()) {
            for (JsonElement ingredient : ingredients.getAsJsonArray()) {
                if (ingredient.isJsonObject()) {
                    JsonObject ingredientObject = ingredient.getAsJsonObject();
                    if (ingredientObject.has("fluid") && ingredientObject.has("amount") && !ingredientObject.has("type")) {
                        ingredientObject.addProperty("type", "neoforge:single");
                        ingredientObject.remove("nbt");
                    }
                }
            }
        }
        return recipe;
    }

    private static boolean xice$shouldDisable(ResourceLocation id, JsonElement json) {
        if (DISABLED_RECIPES.contains(id)) {
            return true;
        }

        if (ALLOWED_WORKFORCE_RECIPES.contains(id)) {
            return false;
        }

        // WorkForce machines and upgrades stay visible and functional, but the
        // pack assigns every acquisition route explicitly. Remove both the
        // mod's own recipes and any cross-mod recipe that consumes or produces
        // a WorkForce item.
        if ("cobblemon_workforce".equals(id.getNamespace())
                || xice$containsNamespace(json, "cobblemon_workforce")) {
            return true;
        }

        // Waxed pots are disabled pack-wide. Botany Pots Tiers currently has no
        // recipe path at all: its pots remain usable, but their acquisition will
        // be assigned explicitly by the quest/progression layer later.
        if (xice$containsBotanyPot(json, true)
                || "botanypotstiers".equals(id.getNamespace())
                || xice$containsNamespace(json, "botanypotstiers")) {
            return true;
        }

        // Replace both the normal and "quick" Botany Pots hopper recipes with
        // one consistent Create-based recipe injected after this filtering pass.
        if ("botanypots".equals(id.getNamespace()) && xice$isBaseHopperPotOutput(json)) {
            return true;
        }

        if (CREATE_AGES.equals(id.getNamespace()) && xice$containsString(json, ANDESITE_MECHANISM)) {
            return true;
        }

        if (CREATE_AGES.equals(id.getNamespace()) && xice$containsString(json, ANDESITE_TEMPLATE)) {
            return true;
        }

        // The custom sequence is the only recipe allowed to produce an Andesite Machine.
        // Every other recipe containing one is an Ages progression/crafting use and must go;
        // cutting recipes are injected only after this filtering pass.
        if (!CUSTOM_ANDESITE_MACHINE_RECIPE.equals(id) && xice$containsString(json, ANDESITE_MACHINE)) {
            return true;
        }

        // Same policy for Copper Machines: only the pack's custom sequence may create one.
        // All Create: Ages remnants that consume or produce copper machines are removed here;
        // pack cutting recipes are injected after filtering, so they stay available.
        if (!CUSTOM_COPPER_MACHINE_RECIPE.equals(id) && xice$containsString(json, COPPER_MACHINE)) {
            return true;
        }

        // Create: Ages' Zinc Machine progression is disabled in the pack.
        // This removes the machine, its sequenced assembly, and every recipe
        // consuming/producing it or its zinc mechanism. Recipes restored from
        // Create remain available because they use the original Create IDs.
        if (!CUSTOM_ZINC_MACHINE_RECIPE.equals(id)
                && !CUSTOM_ZINC_MECHANISM_RECIPE.equals(id)
                && (xice$containsString(json, ZINC_MACHINE)
                || xice$containsString(json, ZINC_MECHANISM)
                || xice$containsString(json, INCOMPLETE_ZINC_MACHINE)
                || xice$containsString(json, INCOMPLETE_ZINC_MECHANISM))) {
            return true;
        }

        if (DISABLED_ECONOMY_CONTENT.stream().anyMatch(value -> xice$containsString(json, value))) {
            return true;
        }

        if (!json.isJsonObject()) {
            return false;
        }

        JsonObject object = json.getAsJsonObject();
        // Create: Ages' own reload hook converts some legacy override markers
        // into empty objects before RecipeManager receives them.
        if (object.size() == 0) {
            return true;
        }

        JsonElement type = object.get("type");
        if (type != null && type.isJsonPrimitive() && OVERRIDE_RECIPE.equals(type.getAsString())) {
            return true;
        }

        if (OPTIONAL_RECIPE_NAMESPACES.contains(id.getNamespace()) && !ModList.get().isLoaded(id.getNamespace())) {
            return true;
        }

        return CREATE_AGES.equals(id.getNamespace()) && xice$referencesMissingOptionalMod(json, null);
    }

    private static boolean xice$referencesMissingOptionalMod(JsonElement element, String key) {
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                if (xice$referencesMissingOptionalMod(entry.getValue(), entry.getKey())) {
                    return true;
                }
            }
            return false;
        }

        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (xice$referencesMissingOptionalMod(child, key)) {
                    return true;
                }
            }
            return false;
        }

        if (!(element instanceof JsonPrimitive primitive)
                || !primitive.isString()
                || !("item".equals(key) || "id".equals(key))) {
            return false;
        }

        ResourceLocation location = ResourceLocation.tryParse(primitive.getAsString());
        return location != null
                && OPTIONAL_RECIPE_NAMESPACES.contains(location.getNamespace())
                && !ModList.get().isLoaded(location.getNamespace());
    }

    private static boolean xice$containsString(JsonElement element, String value) {
        if (element.isJsonObject()) {
            for (JsonElement child : element.getAsJsonObject().asMap().values()) {
                if (xice$containsString(child, value)) {
                    return true;
                }
            }
            return false;
        }
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (xice$containsString(child, value)) {
                    return true;
                }
            }
            return false;
        }
        return element.isJsonPrimitive()
                && element.getAsJsonPrimitive().isString()
                && value.equals(element.getAsString());
    }

    private static boolean xice$containsNamespace(JsonElement element, String namespace) {
        if (element.isJsonObject()) {
            for (JsonElement child : element.getAsJsonObject().asMap().values()) {
                if (xice$containsNamespace(child, namespace)) {
                    return true;
                }
            }
            return false;
        }
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (xice$containsNamespace(child, namespace)) {
                    return true;
                }
            }
            return false;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            return false;
        }
        ResourceLocation location = ResourceLocation.tryParse(element.getAsString());
        return location != null && namespace.equals(location.getNamespace());
    }

    private static boolean xice$containsBotanyPot(JsonElement element, boolean waxed) {
        if (element.isJsonObject()) {
            for (JsonElement child : element.getAsJsonObject().asMap().values()) {
                if (xice$containsBotanyPot(child, waxed)) {
                    return true;
                }
            }
            return false;
        }
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (xice$containsBotanyPot(child, waxed)) {
                    return true;
                }
            }
            return false;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            return false;
        }
        ResourceLocation location = ResourceLocation.tryParse(element.getAsString());
        return location != null
                && ("botanypots".equals(location.getNamespace()) || "botanypotstiers".equals(location.getNamespace()))
                && (!waxed || location.getPath().contains("_waxed_botany_pot"));
    }

    private static boolean xice$isBaseHopperPotOutput(JsonElement json) {
        if (!json.isJsonObject()) {
            return false;
        }
        JsonElement result = json.getAsJsonObject().get("result");
        if (result == null || !result.isJsonObject()) {
            return false;
        }
        JsonObject resultObject = result.getAsJsonObject();
        JsonElement id = resultObject.has("id") ? resultObject.get("id") : resultObject.get("item");
        if (id == null || !id.isJsonPrimitive()) {
            return false;
        }
        ResourceLocation output = ResourceLocation.tryParse(id.getAsString());
        return output != null
                && "botanypots".equals(output.getNamespace())
                && output.getPath().endsWith("_hopper_botany_pot");
    }

    private static Map.Entry<ResourceLocation, ResourceLocation> xice$restoredRecipe(String path) {
        return Map.entry(
                ResourceLocation.fromNamespaceAndPath("create", path),
                ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", "restore/create/" + path)
        );
    }

    private static ResourceLocation xice$recipe(String path) {
        return ResourceLocation.fromNamespaceAndPath("xices_cobblemon_fix", path);
    }

    private static void xice$addAndesiteMachineCuttingRecipes(Map<ResourceLocation, JsonElement> recipes) {
        xice$addMachineCuttingRecipes(recipes, ANDESITE_MACHINE, "andesite_machine", ANDESITE_MACHINE_OUTPUTS);
    }

    private static void xice$addCopperMachineCuttingRecipes(Map<ResourceLocation, JsonElement> recipes) {
        xice$addMachineCuttingRecipes(recipes, COPPER_MACHINE, "copper_machine", COPPER_MACHINE_OUTPUTS);
    }

    private static void xice$addZincMachineCuttingRecipes(Map<ResourceLocation, JsonElement> recipes) {
        xice$addMachineCuttingRecipes(recipes, ZINC_MACHINE, "zinc_machine", ZINC_MACHINE_OUTPUTS);
    }

    private static void xice$addMachineCuttingRecipes(
            Map<ResourceLocation, JsonElement> recipes,
            String inputItem,
            String recipePrefix,
            Map<String, CutOutput> outputs
    ) {
        outputs.forEach((name, output) -> {
            ResourceLocation item = ResourceLocation.parse(output.item());
            if (!"minecraft".equals(item.getNamespace()) && !ModList.get().isLoaded(item.getNamespace())) {
                return;
            }

            JsonObject ingredient = new JsonObject();
            ingredient.addProperty("item", inputItem);
            JsonObject result = new JsonObject();
            result.addProperty("id", output.item());
            result.addProperty("count", output.count());
            if (output.components() != null && !output.components().entrySet().isEmpty()) {
                result.add("components", output.components().deepCopy());
            }

            JsonObject stonecutting = new JsonObject();
            stonecutting.addProperty("type", "minecraft:stonecutting");
            stonecutting.add("ingredient", ingredient.deepCopy());
            stonecutting.add("result", result.deepCopy());
            recipes.put(ResourceLocation.fromNamespaceAndPath(
                    "xices_cobblemon_fix",
                    recipePrefix + "/stonecutting/" + name
            ), stonecutting);

            com.google.gson.JsonArray ingredients = new com.google.gson.JsonArray();
            ingredients.add(ingredient.deepCopy());
            com.google.gson.JsonArray results = new com.google.gson.JsonArray();
            results.add(result.deepCopy());
            JsonObject cutting = new JsonObject();
            cutting.addProperty("type", "create:cutting");
            cutting.add("ingredients", ingredients);
            cutting.addProperty("processing_time", 50);
            cutting.add("results", results);
            recipes.put(ResourceLocation.fromNamespaceAndPath(
                    "xices_cobblemon_fix",
                    recipePrefix + "/cutting/" + name
            ), cutting);
        });
    }

    private static void xice$addHopperBotanyPotRecipes(Map<ResourceLocation, JsonElement> recipes) {
        for (ResourceLocation hopperId : BuiltInRegistries.ITEM.keySet()) {
            if (!"botanypots".equals(hopperId.getNamespace())
                    || !hopperId.getPath().endsWith("_hopper_botany_pot")) {
                continue;
            }

            String normalPath = hopperId.getPath().substring(
                    0, hopperId.getPath().length() - "_hopper_botany_pot".length()
            ) + "_botany_pot";
            ResourceLocation normalId = ResourceLocation.fromNamespaceAndPath("botanypots", normalPath);
            if (!BuiltInRegistries.ITEM.containsKey(normalId)) {
                continue;
            }

            JsonArray ingredients = new JsonArray();
            ingredients.add(xice$itemIngredient("create:mechanical_harvester"));
            ingredients.add(xice$itemIngredient(normalId.toString()));
            ingredients.add(xice$itemIngredient("minecraft:hopper"));

            JsonObject result = new JsonObject();
            result.addProperty("id", hopperId.toString());
            result.addProperty("count", 1);

            JsonObject recipe = new JsonObject();
            recipe.addProperty("type", "minecraft:crafting_shapeless");
            recipe.addProperty("category", "misc");
            recipe.add("ingredients", ingredients);
            recipe.add("result", result);
            recipes.put(
                    ResourceLocation.fromNamespaceAndPath(
                            "xices_cobblemon_fix",
                            "botanypots/hopper/" + hopperId.getPath().substring(0,
                                    hopperId.getPath().length() - "_hopper_botany_pot".length())
                    ),
                    recipe
            );
        }
    }

    private static JsonObject xice$itemIngredient(String item) {
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", item);
        return ingredient;
    }

    private static Map<String, CutOutput> xice$andesiteMachineOutputs() {
        Map<String, CutOutput> outputs = new LinkedHashMap<>();
        outputs.put("shaft", new CutOutput("create:shaft", 32));
        outputs.put("cogwheel", new CutOutput("create:cogwheel", 8));
        outputs.put("large_cogwheel", new CutOutput("create:large_cogwheel", 4));
        outputs.put("gearbox", new CutOutput("create:gearbox", 1));
        outputs.put("vertical_gearbox", new CutOutput("create:vertical_gearbox", 1));
        outputs.put("clutch", new CutOutput("create:clutch", 2));
        outputs.put("gearshift", new CutOutput("create:gearshift", 1));
        outputs.put("encased_chain_drive", new CutOutput("create:encased_chain_drive", 1));
        outputs.put("adjustable_chain_gearshift", new CutOutput("create:adjustable_chain_gearshift", 1));
        outputs.put("chain_conveyor", new CutOutput("create:chain_conveyor", 1));
        outputs.put("water_wheel", new CutOutput("create:water_wheel", 1));
        outputs.put("large_water_wheel", new CutOutput("create:large_water_wheel", 1));
        outputs.put("encased_fan", new CutOutput("create:encased_fan", 1));
        outputs.put("nozzle", new CutOutput("create:nozzle", 1));
        outputs.put("turntable", new CutOutput("create:turntable", 16));
        outputs.put("hand_crank", new CutOutput("create:hand_crank", 2));
        outputs.put("cuckoo_clock", new CutOutput("create:cuckoo_clock", 1));
        outputs.put("millstone", new CutOutput("create:millstone", 1));
        outputs.put("crushing_wheel", new CutOutput("create:crushing_wheel", 1));
        outputs.put("mechanical_press", new CutOutput("create:mechanical_press", 1));
        outputs.put("mechanical_mixer", new CutOutput("create:mechanical_mixer", 1));
        outputs.put("basin", new CutOutput("create:basin", 1));
        outputs.put("depot", new CutOutput("create:depot", 1));
        outputs.put("weighted_ejector", new CutOutput("create:weighted_ejector", 1));
        outputs.put("speedometer", new CutOutput("create:speedometer", 1));
        outputs.put("stressometer", new CutOutput("create:stressometer", 1));
        outputs.put("metal_bracket", new CutOutput("create:metal_bracket", 2));
        outputs.put("mechanical_piston", new CutOutput("create:mechanical_piston", 2));
        outputs.put("piston_extension_pole", new CutOutput("create:piston_extension_pole", 32));
        outputs.put("gantry_carriage", new CutOutput("create:gantry_carriage", 1));
        outputs.put("gantry_shaft", new CutOutput("create:gantry_shaft", 16));
        outputs.put("windmill_bearing", new CutOutput("create:windmill_bearing", 8));
        outputs.put("mechanical_bearing", new CutOutput("create:mechanical_bearing", 2));
        outputs.put("rope_pulley", new CutOutput("create:rope_pulley", 1));
        outputs.put("cart_assembler", new CutOutput("create:cart_assembler", 1));
        outputs.put("linear_chassis", new CutOutput("create:linear_chassis", 2));
        outputs.put("secondary_linear_chassis", new CutOutput("create:secondary_linear_chassis", 2));
        outputs.put("radial_chassis", new CutOutput("create:radial_chassis", 2));
        outputs.put("contraption_controls", new CutOutput("create:contraption_controls", 1));
        outputs.put("mechanical_drill", new CutOutput("create:mechanical_drill", 1));
        outputs.put("mechanical_saw", new CutOutput("create:mechanical_saw", 1));
        outputs.put("deployer", new CutOutput("create:deployer", 1));
        outputs.put("portable_storage_interface", new CutOutput("create:portable_storage_interface", 1));
        outputs.put("mechanical_harvester", new CutOutput("create:mechanical_harvester", 1));
        outputs.put("mechanical_plough", new CutOutput("create:mechanical_plough", 1));
        outputs.put("mechanical_roller", new CutOutput("create:mechanical_roller", 1));
        outputs.put("andesite_casing", new CutOutput("create:andesite_casing", 2));
        outputs.put("andesite_funnel", new CutOutput("create:andesite_funnel", 8));
        outputs.put("andesite_tunnel", new CutOutput("create:andesite_tunnel", 4));
        outputs.put("item_hatch", new CutOutput("create:item_hatch", 1));
        outputs.put("packager", new CutOutput("create:packager", 1));
        outputs.put("repackager", new CutOutput("create:repackager", 1));
        outputs.put("package_frogport", new CutOutput("create:package_frogport", 1));
        outputs.put("white_postbox", new CutOutput("create:white_postbox", 1));
        outputs.put("stock_link", new CutOutput("create:stock_link", 1));
        outputs.put("stock_ticker", new CutOutput("create:stock_ticker", 1));
        outputs.put("display_board", new CutOutput("create:display_board", 1));
        outputs.put("analog_lever", new CutOutput("create:analog_lever", 2));
        outputs.put("andesite_alloy", new CutOutput("create:andesite_alloy", 8));
        outputs.put("minecart_coupling", new CutOutput("create:minecart_coupling", 1));
        outputs.put("bee_port", new CutOutput("create_mobile_packages:bee_port", 1));
        outputs.put("robo_bee", new CutOutput("create_mobile_packages:robo_bee", 1));
        outputs.put("empty_andesite_chunk_loader", new CutOutput("create_power_loader:empty_andesite_chunk_loader", 1));
        outputs.put("encased_chain_cogwheel", new CutOutput("create_connected:encased_chain_cogwheel", 1));
        outputs.put("crank_wheel", new CutOutput("create_connected:crank_wheel", 2));
        outputs.put("large_crank_wheel", new CutOutput("create_connected:large_crank_wheel", 1));
        outputs.put("inverted_clutch", new CutOutput("create_connected:inverted_clutch", 2));
        outputs.put("inverted_gearshift", new CutOutput("create_connected:inverted_gearshift", 1));
        outputs.put("parallel_gearbox", new CutOutput("create_connected:parallel_gearbox", 1));
        outputs.put("vertical_parallel_gearbox", new CutOutput("create_connected:vertical_parallel_gearbox", 1));
        outputs.put("six_way_gearbox", new CutOutput("create_connected:six_way_gearbox", 1));
        outputs.put("vertical_six_way_gearbox", new CutOutput("create_connected:vertical_six_way_gearbox", 1));
        outputs.put("cross_connector", new CutOutput("create_connected:cross_connector", 1));
        outputs.put("shear_pin", new CutOutput("create_connected:shear_pin", 8));
        outputs.put("overstress_clutch", new CutOutput("create_connected:overstress_clutch", 1));
        outputs.put("centrifugal_clutch", new CutOutput("create_connected:centrifugal_clutch", 1));
        outputs.put("freewheel_clutch", new CutOutput("create_connected:freewheel_clutch", 1));
        outputs.put("brake", new CutOutput("create_connected:brake", 1));
        outputs.put("basin_lid", new CutOutput("createdieselgenerators:basin_lid", 1));
        outputs.put("andesite_girder", new CutOutput("createdieselgenerators:andesite_girder", 16));
        return Map.copyOf(outputs);
    }

    private static final Map<String, CutOutput> COPPER_MACHINE_OUTPUTS = xice$copperMachineOutputs();

    private static Map<String, CutOutput> xice$copperMachineOutputs() {
        Map<String, CutOutput> outputs = new LinkedHashMap<>();
        // Copper backtanks are non-stackable, so the recipe result must never exceed 1.
        outputs.put("copper_backtank", new CutOutput("create:copper_backtank", 1));
        outputs.put("fluid_pipe", new CutOutput("create:fluid_pipe", 16));
        outputs.put("mechanical_pump", new CutOutput("create:mechanical_pump", 4));
        outputs.put("smart_fluid_pipe", new CutOutput("create:smart_fluid_pipe", 8));
        outputs.put("fluid_valve", new CutOutput("create:fluid_valve", 4));
        outputs.put("copper_valve_handle", new CutOutput("create:copper_valve_handle", 8));
        outputs.put("fluid_tank", new CutOutput("create:fluid_tank", 4));
        outputs.put("hose_pulley", new CutOutput("create:hose_pulley", 1));
        outputs.put("item_drain", new CutOutput("create:item_drain", 4));
        outputs.put("spout", new CutOutput("create:spout", 4));
        outputs.put("portable_fluid_interface", new CutOutput("create:portable_fluid_interface", 2));
        outputs.put("steam_engine", new CutOutput("create:steam_engine", 1));
        outputs.put("steam_whistle", new CutOutput("create:steam_whistle", 4));
        outputs.put("copper_casing", new CutOutput("create:copper_casing", 8));
        outputs.put("fluid_hatch", new CutOutput("create_dragons_plus:fluid_hatch", 4));
        outputs.put("experience_hatch", new CutOutput("create_enchantment_industry:experience_hatch", 2));
        outputs.put("experience_lantern", new CutOutput("create_enchantment_industry:experience_lantern", 2));
        outputs.put("printer", new CutOutput("create_enchantment_industry:printer", 1));
        outputs.put("pumpjack_hole", new CutOutput("createdieselgenerators:pumpjack_hole", 1));
        outputs.put("fluid_vessel", new CutOutput("create_connected:fluid_vessel", 4));
        return Map.copyOf(outputs);
    }

    private static Map<String, CutOutput> xice$zincMachineOutputs() {
        Map<String, CutOutput> outputs = new LinkedHashMap<>();
        outputs.put("iron_door", new CutOutput("minecraft:iron_door", 1));
        outputs.put("chain", new CutOutput("minecraft:chain", 2));
        outputs.put("anvil", new CutOutput("minecraft:anvil", 1));
        outputs.put("cauldron", new CutOutput("minecraft:cauldron", 1));
        outputs.put("hopper", new CutOutput("minecraft:hopper", 1));
        outputs.put("minecart", new CutOutput("minecraft:minecart", 1));
        outputs.put("zinc_hand", new CutOutput("createages:zinc_hand", 1));
        outputs.put("zinc_casing", new CutOutput("createages:zinc_casing", 2));
        outputs.put("chute", new CutOutput("create:chute", 4));
        outputs.put("empty_blaze_burner", new CutOutput("create:empty_blaze_burner", 1));
        outputs.put("track", new CutOutput("create:track", 16));
        outputs.put("item_vault", new CutOutput("create:item_vault", 1));
        outputs.put("distillation_controller", new CutOutput("createdieselgenerators:distillation_controller", 1));
        outputs.put("oil_scanner", new CutOutput("createdieselgenerators:oil_scanner", 1));
        outputs.put("pumpjack_bearing", new CutOutput("createdieselgenerators:pumpjack_bearing", 1));
        outputs.put("pumpjack_crank", new CutOutput("createdieselgenerators:pumpjack_crank", 1));
        outputs.put("pumpjack_head", new CutOutput("createdieselgenerators:pumpjack_head", 1));
        outputs.put("canister", new CutOutput("createdieselgenerators:canister", 1));
        outputs.put("oil_barrel", new CutOutput("createdieselgenerators:oil_barrel", 1));
        outputs.put("engine_turbocharger", new CutOutput("createdieselgenerators:engine_turbocharger", 1));
        outputs.put("bowl_mold", xice$moldCutOutput("createdieselgenerators:bowl", 2));
        outputs.put("lines_mold", xice$moldCutOutput("createdieselgenerators:lines", 2));
        outputs.put("chain_mold", xice$moldCutOutput("createdieselgenerators:chain", 2));
        outputs.put("bar_mold", xice$moldCutOutput("createdieselgenerators:bar", 2));
        outputs.put("portable_stock_ticker", new CutOutput("create_mobile_packages:portable_stock_ticker", 1));
        outputs.put("item_silo", new CutOutput("create_connected:item_silo", 1));
        return Map.copyOf(outputs);
    }

    private static CutOutput xice$moldCutOutput(String moldType, int count) {
        JsonObject components = new JsonObject();
        components.addProperty("createdieselgenerators:mold_type", moldType);
        return new CutOutput("createdieselgenerators:mold", count, components);
    }

    private record CutOutput(String item, int count, JsonObject components) {
        private CutOutput(String item, int count) {
            this(item, count, null);
        }
    }

    private static JsonObject xice$falseCondition() {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "neoforge:false");

        com.google.gson.JsonArray conditions = new com.google.gson.JsonArray();
        conditions.add(condition);

        JsonObject recipe = new JsonObject();
        recipe.add("neoforge:conditions", conditions);
        return recipe;
    }
}
