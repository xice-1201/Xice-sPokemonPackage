"""Estimate Create recipe material values in a single, documented point system.

One point is one common raw unit or one iron/zinc nugget.  The script reads the
actual recipes shipped by this instance and repeatedly relaxes the cheapest
known recipe cost for every output.  It is an audit aid, not runtime content.
"""

from __future__ import annotations

import json
import re
import zipfile
from collections import defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "xices-cobblemon-fix/src/main/java/cn/cnxice/cobblemonfix/mixin/CreateAgesRecipeMixin.java"

BASE = {
    "minecraft:andesite": 1.0,
    "minecraft:cobblestone": 1.0,
    "minecraft:stone": 1.0,
    "minecraft:redstone": 1.0,
    "minecraft:paper": 1.0,
    "minecraft:string": 1.0,
    "minecraft:dried_kelp": 1.0,
    "minecraft:honeycomb": 2.0,
    "minecraft:slime_ball": 4.0,
    "minecraft:iron_nugget": 1.0,
    "minecraft:iron_ingot": 9.0,
    "minecraft:iron_block": 81.0,
    "minecraft:gold_nugget": 2.0,
    "minecraft:gold_ingot": 18.0,
    "minecraft:copper_ingot": 9.0,
    "minecraft:quartz": 2.0,
    "minecraft:glowstone_dust": 1.0,
    "minecraft:crying_obsidian": 12.0,
    "minecraft:obsidian": 8.0,
}

TAG = {
    "minecraft:planks": 1.0,
    "minecraft:wooden_slabs": 0.5,
    "minecraft:logs": 4.0,
    "minecraft:logs_that_burn": 4.0,
    "minecraft:wool": 4.0,
    "minecraft:stone_crafting_materials": 1.0,
    "minecraft:buttons": 0.25,
    "c:stripped_logs": 4.0,
    "c:stripped_woods": 4.0,
    "c:stones": 1.0,
    "c:cobblestones": 1.0,
    "c:nuggets/iron": 1.0,
    "c:nuggets/zinc": 1.0,
    "c:ingots/iron": 9.0,
    "c:ingots/copper": 9.0,
    "c:ingots/gold": 18.0,
    "c:ingots/zinc": 9.0,
    "c:plates/iron": 9.0,
    "c:plates/copper": 9.0,
    "c:plates/gold": 18.0,
    "c:plates/brass": 9.0,
    "c:storage_blocks/iron": 81.0,
    "c:dusts/redstone": 1.0,
    "c:glass_blocks": 1.0,
    "c:slimeballs": 4.0,
    "c:gems/quartz": 2.0,
    "c:barrels/wooden": 7.0,
    "c:dyes/white": 1.0,
    "c:strings": 1.0,
    "c:rods/wooden": 0.5,
    "create:pulpifiable": 1.0,
}


def jars() -> list[Path]:
    wanted = [ROOT / "Xice的方块宝可梦.jar"]
    for jar in (ROOT / "mods").glob("*.jar"):
        name = jar.name.lower()
        if name.startswith(("[机械动力] create-", "[机械动力：无人机物流]", "[机械动力：动力加载器]")):
            wanted.append(jar)
    return wanted


def recipes() -> list[dict]:
    found: list[dict] = []
    for jar in jars():
        with zipfile.ZipFile(jar) as archive:
            for name in archive.namelist():
                if "/recipe/" not in name or not name.endswith(".json"):
                    continue
                try:
                    found.append(json.loads(archive.read(name)))
                except (json.JSONDecodeError, KeyError, UnicodeDecodeError):
                    pass
    return found


def ingredient_cost(ingredient: object, values: dict[str, float]) -> float | None:
    if isinstance(ingredient, list):
        choices = [ingredient_cost(value, values) for value in ingredient]
        choices = [value for value in choices if value is not None]
        return min(choices) if choices else None
    if not isinstance(ingredient, dict):
        return None
    if "item" in ingredient:
        return values.get(ingredient["item"])
    if "tag" in ingredient:
        return TAG.get(ingredient["tag"])
    if "ingredients" in ingredient:  # NeoForge compound ingredient
        return ingredient_cost(ingredient["ingredients"], values)
    if "fluid" in ingredient:
        return 0.0
    return None


def recipe_cost(recipe: dict, values: dict[str, float]) -> float | None:
    recipe_type = recipe.get("type", "")
    if "crafting_shaped" in recipe_type or recipe_type == "create:mechanical_crafting":
        key = recipe.get("key", {})
        symbols = "".join(recipe.get("pattern", [])).replace(" ", "")
        inputs = [key.get(symbol) for symbol in symbols]
    elif "crafting_shapeless" in recipe_type:
        inputs = recipe.get("ingredients", [])
    elif "stonecutting" in recipe_type:
        inputs = [recipe.get("ingredient")]
    elif recipe_type.startswith("create:") and "ingredients" in recipe:
        inputs = recipe["ingredients"]
    else:
        return None
    costs = [ingredient_cost(value, values) for value in inputs]
    if any(value is None for value in costs):
        return None
    return sum(costs)  # type: ignore[arg-type]


def outputs(recipe: dict) -> list[tuple[str, int]]:
    raw = recipe.get("results")
    if raw is None:
        raw = [recipe.get("result")]
    elif isinstance(raw, dict):
        raw = [raw]
    result: list[tuple[str, int]] = []
    for entry in raw or []:
        if not isinstance(entry, dict):
            continue
        item = entry.get("id", entry.get("item"))
        if item:
            result.append((item, int(entry.get("count", 1))))
    return result


values = dict(BASE)
all_recipes = recipes()
for _ in range(100):
    changed = False
    for recipe in all_recipes:
        cost = recipe_cost(recipe, values)
        result = outputs(recipe)
        if cost is None or len(result) != 1:
            continue
        item, count = result[0]
        unit = cost / count
        if unit > 0 and unit + 1e-9 < values.get(item, float("inf")):
            values[item] = unit
            changed = True
    if not changed:
        break

# Values of the two pack-specific sequences, using their exact ingredients.
mechanism = (
    values["create:large_cogwheel"]
    + TAG["minecraft:wooden_slabs"]
    + values["create:cogwheel"]
    + values["create:cardboard"]
    + values["create:andesite_alloy"]
)
machine_run = (
    values["create:andesite_casing"]
    + mechanism
    + values["create:piston_extension_pole"]
    + values["create:cogwheel"]
)
machine_expected = (machine_run - 0.05 * mechanism) / 1.10

source = SOURCE.read_text(encoding="utf-8")
listed = re.findall(r'outputs\.put\("([^"]+)", new CutOutput\("([^"]+)", (\d+)\)\)', source)
print(f"andesite_mechanism={mechanism:.3f}")
print(f"machine_recipe_run={machine_run:.3f}")
print(f"machine_expected={machine_expected:.3f}")
print("name\titem\tinput_machines\tunit_points\tcurrent_count\tbatch_points\treturn_percent\tmax_under_15")
for name, item, count_text in listed:
    count = int(count_text)
    value = values.get(item)
    if value is None:
        print(f"{name}\t{item}\tUNKNOWN\tUNKNOWN\t{count}\tUNKNOWN\tUNKNOWN\tUNKNOWN")
        continue
    maximum = max(1, int((15.0 - 1e-9) // value))
    batch = value * count
    input_machines = 1
    print(f"{name}\t{item}\t{input_machines}\t{value:.3f}\t{count}\t{batch:.3f}\t{batch / (machine_expected * input_machines) * 100:.1f}%\t{maximum}")
