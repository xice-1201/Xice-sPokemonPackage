from __future__ import annotations

import json
import shutil
import zipfile
from collections import defaultdict, deque
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
COBBLEMON_JAR = ROOT / "mods" / "[方块宝可梦] Cobblemon-neoforge-1.8.0+1.21.1.jar"
OUTPUT = ROOT / "saves" / "新的世界" / "datapacks" / "Xice通用宝可梦生成规则"

RANK_TO_BUCKET = {0: "common", 1: "uncommon", 2: "rare", 3: "ultra-rare"}

PSEUDO_FAMILIES = {
    "dratini", "dragonair", "dragonite",
    "larvitar", "pupitar", "tyranitar",
    "bagon", "shelgon", "salamence",
    "beldum", "metang", "metagross",
    "gible", "gabite", "garchomp",
    "deino", "zweilous", "hydreigon",
    "goomy", "sliggoo", "goodra",
    "jangmoo", "hakamoo", "kommoo",
    "dreepy", "drakloak", "dragapult",
    "frigibax", "arctibax", "baxcalibur",
}

FOSSIL_EXTRAS = {
    "dracozolt", "arctozolt", "dracovish", "arctovish",
}

COBBLEMON_RUINS = [
    "cobblemon:ruins/ancient_dais_ruins",
    "cobblemon:ruins/crumbling_arch_ruins",
    "cobblemon:ruins/decaying_crypt_ruins",
    "cobblemon:ruins/deserted_gimmi_tower",
    "cobblemon:ruins/deserted_house_ruins",
    "cobblemon:ruins/deserted_monument_ruins",
    "cobblemon:ruins/deserted_tower_ruins",
    "cobblemon:ruins/deserted_town_center_ruins",
    "cobblemon:ruins/fallen_statue_ruins",
    "cobblemon:ruins/frozen_altar_ruins",
    "cobblemon:ruins/frozen_gimmi_tower",
    "cobblemon:ruins/hidden_bunker_ruins",
    "cobblemon:ruins/luna_henge_ruins",
    "cobblemon:ruins/lush_gimmi_tower",
    "cobblemon:ruins/lush_monument_ruins",
    "cobblemon:ruins/meteor_battleground_ruins",
    "cobblemon:ruins/mossy_oubliette_ruins",
    "cobblemon:ruins/old_garden_ruins",
    "cobblemon:ruins/overgrown_trial_ruins",
    "cobblemon:ruins/rooted_arch_ruins",
    "cobblemon:ruins/rooted_gimmi_tower",
    "cobblemon:ruins/sol_henge_ruins",
    "cobblemon:ruins/stonjourner_henge_ruins",
    "cobblemon:ruins/submerged_forge_ruins",
    "cobblemon:ruins/sunscorched_gimmi_tower",
    "cobblemon:ruins/sunscorched_shaded_ruins",
    "cobblemon:ruins/temperate_gimmi_tower",
    "cobblemon:ruins/toppled_pillars_ruins",
    "cobblemon:ruins/unstable_cave_ruins",
]

MEGA_SHOWDOWN_STRUCTURES = [
    "mega_showdown:archaeological_site",
    "mega_showdown:mega_site",
    "mega_showdown:megaroid",
    "mega_showdown:observatory",
    "mega_showdown:wishing_weald",
]

SPIRITOMB_STRUCTURES = [
    "minecraft:ancient_city",
    "minecraft:desert_pyramid",
    "minecraft:mineshaft",
    "minecraft:mineshaft_mesa",
    "minecraft:nether_fossil",
    "minecraft:shipwreck_beached",
    "minecraft:trail_ruins",
    "twilightforest:dark_tower",
    "twilightforest:final_castle",
    "twilightforest:small_hollow_hill",
    "twilightforest:medium_hollow_hill",
    "twilightforest:large_hollow_hill",
    "twilightforest:lich_tower",
] + COBBLEMON_RUINS + MEGA_SHOWDOWN_STRUCTURES

SHIPWRECK_AND_UNDERWATER_RUINS = [
    "minecraft:shipwreck",
    "minecraft:shipwreck_beached",
    "minecraft:ocean_ruin_cold",
    "minecraft:ocean_ruin_warm",
    "cobblemon:shipwreck_coves/lush_shipwreck_cove",
    "cobblemon:shipwreck_coves/magma_shipwreck_cove",
    "cobblemon:shipwreck_coves/submerged_shipwreck_cove",
    "cobblemon:ruins/submerged_forge_ruins",
]

EXCLUSIVE_CUSTOM_SPAWN_SPECIES = {
    "spiritomb", "rotom", "relicanth", "dhelmise", "sigilyph",
    "volbeat", "illumise", "zangoose", "seviper", "lunatone", "solrock",
    "throh", "sawk", "heatmor", "durant", "oranguru", "passimian",
    "stonjourner", "eiscue",
    "castform", "oricorio", "wishiwashi", "morpeko", "minior",
    "indeedee", "unown", "mimikyu",
    "sableye", "mawile", "kecleon", "absol", "carbink", "klefki", "falinks",
    "lapras", "luvdisc", "alomomola", "pyukumuku", "bruxish", "cramorant",
    "pincurchin", "veluza",
    "pinsir", "heracross", "shuckle", "tropius", "pachirisu", "chatot",
    "carnivine", "emolga", "komala",
    "delibird", "skarmory", "torkoal", "maractus", "cryogonal", "druddigon",
    "hawlucha", "klawf",
    "kangaskhan", "spinda", "audino", "bouffalant", "furfrou", "dedenne",
    "togedemaru", "squawkabilly", "cyclizar", "flamigo",
    "stunfisk", "comfey", "turtonator", "drampa", "bombirdier", "orthworm",
}

QUEST_GROVE_BUCKET_OVERRIDES = {
    "ditto": "ultra-rare",
    "smeargle": "rare",
}

DIRECT_BUCKET_OVERRIDES = {
    "dondozo": "rare",
    "tatsugiri": "ultra-rare",
}

COMPANION_EVENT_SPECIES = {
    "plusle", "minun", "miltank", "tauros", "durant", "dondozo", "tatsugiri", "indeedee",
    "carbink", "falinks",
    "lapras", "luvdisc", "veluza",
    "pachirisu", "chatot", "emolga",
    "klawf",
    "kangaskhan", "spinda", "bouffalant", "squawkabilly", "cyclizar", "flamigo",
    "comfey",
}

REMOVE_POKE_SNACK_REQUIREMENT = {
    "plusle", "minun", "miltank", "tauros",
}

ROTOM_REDSTONE_COMPONENTS = [
    "minecraft:redstone_wire",
    "minecraft:redstone_block",
    "minecraft:redstone_ore",
    "minecraft:deepslate_redstone_ore",
    "minecraft:redstone_torch",
    "minecraft:redstone_wall_torch",
    "minecraft:redstone_lamp",
    "minecraft:repeater",
    "minecraft:comparator",
    "minecraft:observer",
    "minecraft:piston",
    "minecraft:sticky_piston",
    "minecraft:dispenser",
    "minecraft:dropper",
    "minecraft:hopper",
    "minecraft:lever",
    "minecraft:target",
    "minecraft:daylight_detector",
    "minecraft:tripwire_hook",
    "minecraft:trapped_chest",
    "minecraft:lightning_rod",
    "minecraft:sculk_sensor",
    "minecraft:calibrated_sculk_sensor",
    "minecraft:crafter",
    "minecraft:note_block",
    "#minecraft:buttons",
    "#minecraft:pressure_plates",
]

INFRASTRUCTURE_COMPONENTS = ROTOM_REDSTONE_COMPONENTS + [
    "create:shaft",
    "create:cogwheel",
    "create:large_cogwheel",
    "create:andesite_encased_cogwheel",
    "create:andesite_encased_large_cogwheel",
    "create:brass_encased_cogwheel",
    "create:brass_encased_large_cogwheel",
    "create:gearbox",
    "create:clutch",
    "create:gearshift",
    "create:sequenced_gearshift",
    "create:encased_chain_drive",
    "create:adjustable_chain_gearshift",
    "create:mechanical_press",
    "create:mechanical_mixer",
    "create:mechanical_saw",
    "create:mechanical_drill",
    "create:deployer",
    "create:mechanical_harvester",
    "create:mechanical_plough",
    "create:millstone",
    "create:crushing_wheel",
    "create:water_wheel",
    "create:large_water_wheel",
    "create:windmill_bearing",
    "create:mechanical_bearing",
    "create:steam_engine",
    "create:flywheel",
    "create:rotation_speed_controller",
]

CRYSTAL_SOURCES = [
    "minecraft:amethyst_block",
    "minecraft:budding_amethyst",
    "minecraft:small_amethyst_bud",
    "minecraft:medium_amethyst_bud",
    "minecraft:large_amethyst_bud",
    "minecraft:amethyst_cluster",
]

CORAL_REEF_BLOCKS = [
    "minecraft:tube_coral_block", "minecraft:brain_coral_block",
    "minecraft:bubble_coral_block", "minecraft:fire_coral_block",
    "minecraft:horn_coral_block", "minecraft:tube_coral",
    "minecraft:brain_coral", "minecraft:bubble_coral", "minecraft:fire_coral",
    "minecraft:horn_coral", "minecraft:tube_coral_fan",
    "minecraft:brain_coral_fan", "minecraft:bubble_coral_fan",
    "minecraft:fire_coral_fan", "minecraft:horn_coral_fan",
]

HONEY_SOURCES = [
    "minecraft:bee_nest", "minecraft:beehive",
    "minecraft:honey_block", "minecraft:honeycomb_block",
]

BERRY_SOURCES = ["#cobblemon:berries"]

TREE_SOURCES = ["#minecraft:logs", "#minecraft:leaves"]

IRON_SOURCES = ["minecraft:iron_ore", "minecraft:deepslate_iron_ore", "minecraft:raw_iron_block"]

MAGMA_SOURCES = ["minecraft:lava", "minecraft:magma_block", "minecraft:fire", "minecraft:soul_fire"]

CACTUS_SOURCES = ["minecraft:cactus"]

HEALING_SOURCES = ["cobblemon:healing_machine"]


def species_id_from_path(path: str) -> str:
    return Path(path).stem.lower()


def result_species(value: str) -> str:
    token = value.strip().split()[0].lower()
    return token.split(":", 1)[-1]


def pokemon_from_properties(value: str) -> str:
    return result_species(value)


def iter_evolutions(species: dict):
    yield from species.get("evolutions", []) or []
    for form in species.get("forms", []) or []:
        yield from form.get("evolutions", []) or []


def edge_kind(evolution: dict) -> str:
    variant = evolution.get("variant", "")
    requirements = {
        requirement.get("variant", "")
        for requirement in evolution.get("requirements", []) or []
    }
    if "friendship" in requirements:
        return "friendship"
    if variant == "trade":
        return "trade"
    if variant == "item_interact" or "held_item" in requirements:
        return "item"
    if variant == "level_up":
        # Level, form/gender properties and stat comparisons are passive parts
        # of an otherwise ordinary level-up. Active/situational requirements
        # (move use, biome, weather, party, structure, travel, etc.) are left
        # untouched until the pack author defines a separate policy for them.
        passive_requirements = {
            "level", "properties", "property_range", "stat_compare", "stat_equal"
        }
        return "ordinary" if requirements <= passive_requirements else "other"
    return "other"


def main() -> None:
    if not COBBLEMON_JAR.is_file():
        raise FileNotFoundError(COBBLEMON_JAR)

    species_by_id: dict[str, dict] = {}
    spawn_files: dict[str, dict] = {}

    with zipfile.ZipFile(COBBLEMON_JAR) as jar:
        for name in jar.namelist():
            if name.startswith("data/cobblemon/species/") and name.endswith(".json"):
                species_by_id[species_id_from_path(name)] = json.loads(jar.read(name))
            elif name.startswith("data/cobblemon/spawn_pool_world/") and name.endswith(".json"):
                spawn_files[name] = json.loads(jar.read(name))

    edges: list[tuple[str, str, str]] = []
    incoming: dict[str, list[tuple[str, str]]] = defaultdict(list)
    outgoing: dict[str, list[tuple[str, str]]] = defaultdict(list)
    friendship_targets: set[str] = set()

    for source, species in species_by_id.items():
        for evolution in iter_evolutions(species):
            raw_result = evolution.get("result")
            if not isinstance(raw_result, str):
                continue
            target = result_species(raw_result)
            if target not in species_by_id or target == source:
                continue
            kind = edge_kind(evolution)
            edges.append((source, target, kind))
            incoming[target].append((source, kind))
            outgoing[source].append((target, kind))
            if kind == "friendship":
                friendship_targets.add(target)

    governed_edges = [edge for edge in edges if edge[2] != "other"]
    governed_incoming: dict[str, list[tuple[str, str]]] = defaultdict(list)
    governed_outgoing: dict[str, list[tuple[str, str]]] = defaultdict(list)
    for source, target, kind in governed_edges:
        governed_incoming[target].append((source, kind))
        governed_outgoing[source].append((target, kind))

    evolution_species = set(governed_incoming) | set(governed_outgoing)
    ranks: dict[str, int] = {}
    queue: deque[str] = deque()

    # Only evolutionary families are assigned a generic stage rarity. Species with
    # no evolution relationship keep Cobblemon's original bucket.
    for species_id in evolution_species:
        if not governed_incoming.get(species_id):
            ranks[species_id] = 0
            queue.append(species_id)

    # Propagate stage rarity. Item/trade evolutions jump two tiers; all other
    # level-up evolutions advance one tier. Friendship targets are assigned a
    # conceptual tier for downstream branches, but are suppressed from spawning.
    for _ in range(len(species_by_id) + 1):
        changed = False
        for source, target, kind in governed_edges:
            if source not in ranks:
                continue
            jump = 2 if kind in {"item", "trade"} else 1
            candidate = min(3, ranks[source] + jump)
            if candidate > ranks.get(target, -1):
                ranks[target] = candidate
                changed = True
        if not changed:
            break

    labels_by_id = {
        species_id: set(species.get("labels", []) or [])
        for species_id, species in species_by_id.items()
    }
    starter_roots = {sid for sid, labels in labels_by_id.items() if "starter" in labels}
    starter_families = set(starter_roots)
    starter_queue = deque(starter_roots)
    while starter_queue:
        source = starter_queue.popleft()
        for target, _kind in outgoing.get(source, []):
            if target not in starter_families:
                starter_families.add(target)
                starter_queue.append(target)

    fossils = ({sid for sid, labels in labels_by_id.items() if "fossil" in labels}
               | (FOSSIL_EXTRAS & set(species_by_id)))
    legendary = {sid for sid, labels in labels_by_id.items() if "legendary" in labels}
    mythical = {sid for sid, labels in labels_by_id.items() if "mythical" in labels}
    ultra_beasts = {sid for sid, labels in labels_by_id.items() if "ultra_beast" in labels}
    paradox = {sid for sid, labels in labels_by_id.items() if "paradox" in labels}
    naturally_disabled_specials = fossils | legendary | mythical | ultra_beasts | paradox
    forced_ultra_rare = (
        starter_families | (PSEUDO_FAMILIES & set(species_by_id))
    ) - naturally_disabled_specials
    disabled = naturally_disabled_specials | friendship_targets

    final_bucket: dict[str, str] = {
        species_id: RANK_TO_BUCKET[rank]
        for species_id, rank in ranks.items()
    }
    for species_id in forced_ultra_rare:
        final_bucket[species_id] = "ultra-rare"

    if OUTPUT.exists():
        shutil.rmtree(OUTPUT)
    data_root = OUTPUT / "data" / "cobblemon" / "spawn_pool_world"
    data_root.mkdir(parents=True, exist_ok=True)

    changed_files = 0
    changed_entries = 0
    removed_entries = 0
    removed_herd_members = 0

    for resource_path, document in spawn_files.items():
        changed = False
        new_spawns = []
        is_top_level = resource_path.count("/") == 3

        for spawn in document.get("spawns", []) or []:
            if is_top_level and isinstance(spawn.get("pokemon"), str):
                species_id = pokemon_from_properties(spawn["pokemon"])
                if species_id in disabled or species_id in EXCLUSIVE_CUSTOM_SPAWN_SPECIES:
                    removed_entries += 1
                    changed = True
                    continue
                if species_id in REMOVE_POKE_SNACK_REQUIREMENT:
                    condition = spawn.get("condition")
                    if isinstance(condition, dict) and condition.pop("isPokeSnack", None) is not None:
                        changed = True
                if species_id in QUEST_GROVE_BUCKET_OVERRIDES:
                    bucket = QUEST_GROVE_BUCKET_OVERRIDES[species_id]
                    if spawn.get("bucket") != bucket:
                        spawn["bucket"] = bucket
                        changed_entries += 1
                        changed = True
                    condition = spawn.setdefault("condition", {})
                    excluded = condition.setdefault("excludedStructures", [])
                    if "twilightforest:quest_grove" not in excluded:
                        excluded.append("twilightforest:quest_grove")
                        changed = True
                    new_spawns.append(spawn)
                    continue
                if species_id in DIRECT_BUCKET_OVERRIDES:
                    bucket = DIRECT_BUCKET_OVERRIDES[species_id]
                    if spawn.get("bucket") != bucket:
                        spawn["bucket"] = bucket
                        changed_entries += 1
                        changed = True
                    if species_id == "tatsugiri" and spawn.get("weight") != 1.0:
                        spawn["weight"] = 1.0
                        changed = True
                    new_spawns.append(spawn)
                    continue
                bucket = final_bucket.get(species_id)
                if bucket and spawn.get("bucket") != bucket:
                    spawn["bucket"] = bucket
                    changed_entries += 1
                    changed = True

            # Herds have one bucket for the whole group, so generic per-species
            # rarity cannot be applied to members. We only remove species that
            # must never generate naturally.
            if isinstance(spawn.get("herdablePokemon"), list):
                members = []
                for member in spawn["herdablePokemon"]:
                    value = member.get("pokemon")
                    if (isinstance(value, str)
                            and pokemon_from_properties(value) in (
                                disabled | EXCLUSIVE_CUSTOM_SPAWN_SPECIES | COMPANION_EVENT_SPECIES
                            )):
                        removed_herd_members += 1
                        changed = True
                        continue
                    members.append(member)
                spawn["herdablePokemon"] = members
                if not members:
                    removed_entries += 1
                    changed = True
                    continue

            new_spawns.append(spawn)

        if changed:
            document["spawns"] = new_spawns
            if not new_spawns:
                document["enabled"] = False
            relative = Path(resource_path).relative_to("data/cobblemon/spawn_pool_world")
            target = data_root / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(json.dumps(document, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
            changed_files += 1

    custom_spawns = {
        "enabled": True,
        "neededInstalledMods": [],
        "neededUninstalledMods": [],
        "spawns": [
            {
                "id": "xice-spiritomb-structures",
                "pokemon": "spiritomb",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "rare",
                "level": "25-50",
                "weight": 100.0,
                "condition": {"structures": SPIRITOMB_STRUCTURES},
            },
            {
                "id": "xice-rotom-powered-structures",
                "pokemon": "rotom",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "15-40",
                "weight": 100.0,
                "condition": {
                    "structures": [
                        "minecraft:ancient_city",
                        "minecraft:jungle_pyramid",
                        "twilightforest:dark_tower",
                        "twilightforest:final_castle",
                        "mega_showdown:observatory",
                    ],
                    "nearbyRedstoneRadius": 8,
                },
            },
            {
                "id": "xice-ditto-quest-grove",
                "pokemon": "ditto",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "4-29",
                "weight": 100.0,
                "condition": {"structures": ["twilightforest:quest_grove"]},
            },
            {
                "id": "xice-smeargle-quest-grove",
                "pokemon": "smeargle",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "1-25",
                "weight": 100.0,
                "condition": {"structures": ["twilightforest:quest_grove"]},
            },
            {
                "id": "xice-relicanth-wrecks-and-underwater-ruins",
                "pokemon": "relicanth",
                "presets": ["water"],
                "type": "pokemon",
                "spawnablePositionType": "submerged",
                "bucket": "rare",
                "level": "24-49",
                "weight": 100.0,
                "condition": {"structures": SHIPWRECK_AND_UNDERWATER_RUINS},
            },
            {
                "id": "xice-dhelmise-wrecks-and-underwater-ruins",
                "pokemon": "dhelmise",
                "presets": ["water"],
                "type": "pokemon",
                "spawnablePositionType": "submerged",
                "bucket": "common",
                "level": "27-52",
                "weight": 100.0,
                "condition": {"structures": SHIPWRECK_AND_UNDERWATER_RUINS},
            },
            {
                "id": "xice-sigilyph-desert-pyramid",
                "pokemon": "sigilyph",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "rare",
                "level": "24-49",
                "weight": 100.0,
                "condition": {"structures": ["minecraft:desert_pyramid"]},
            },
            {
                "id": "xice-volbeat-forests-plains",
                "pokemon": "volbeat",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "18-43",
                "weight": 60.0,
                "condition": {
                    "biomes": ["#cobblemon:is_forest", "#cobblemon:is_plains"],
                    "timeRange": "night",
                },
            },
            {
                "id": "xice-illumise-forests-plains",
                "pokemon": "illumise",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "18-43",
                "weight": 60.0,
                "condition": {
                    "biomes": ["#cobblemon:is_forest", "#cobblemon:is_plains"],
                    "timeRange": "night",
                },
            },
            {
                "id": "xice-zangoose-warm-open-land",
                "pokemon": "zangoose",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "21-46",
                "weight": 100.0,
                "condition": {"biomes": ["#cobblemon:is_savanna", "#cobblemon:is_plains"]},
            },
            {
                "id": "xice-seviper-warm-wet-land",
                "pokemon": "seviper",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "21-46",
                "weight": 100.0,
                "condition": {"biomes": ["#cobblemon:is_swamp", "#cobblemon:is_jungle"]},
            },
            {
                "id": "xice-lunatone-night-rocklands",
                "pokemon": "lunatone",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "21-46",
                "weight": 100.0,
                "condition": {
                    "biomes": ["#cobblemon:is_badlands", "#cobblemon:is_mountain", "#cobblemon:is_peak"],
                    "timeRange": "night",
                },
            },
            {
                "id": "xice-solrock-day-rocklands",
                "pokemon": "solrock",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "21-46",
                "weight": 100.0,
                "condition": {
                    "biomes": ["#cobblemon:is_badlands", "#cobblemon:is_mountain", "#cobblemon:is_peak"],
                    "timeRange": "day",
                },
            },
            {
                "id": "xice-throh-cold-highlands",
                "pokemon": "throh",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "12-47",
                "weight": 100.0,
                "condition": {"biomes": ["#cobblemon:is_cold"], "minY": 80},
            },
            {
                "id": "xice-sawk-warm-highlands",
                "pokemon": "sawk",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "12-47",
                "weight": 100.0,
                "condition": {
                    "biomes": ["#cobblemon:is_savanna", "#cobblemon:is_badlands"],
                    "minY": 80,
                },
            },
            {
                "id": "xice-heatmor-ant-habitat",
                "pokemon": "heatmor",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "23-48",
                "weight": 70.0,
                "condition": {
                    "biomes": ["#cobblemon:is_badlands", "#cobblemon:is_hills", "#cobblemon:nether/is_mountain"]
                },
            },
            {
                "id": "xice-durant-ant-habitat",
                "pokemon": "durant",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "23-48",
                "weight": 100.0,
                "condition": {
                    "biomes": ["#cobblemon:is_badlands", "#cobblemon:is_hills", "#cobblemon:nether/is_mountain"]
                },
            },
            {
                "id": "xice-oranguru-dense-forest",
                "pokemon": "oranguru",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "20-45",
                "weight": 80.0,
                "condition": {"biomes": ["#cobblemon:is_jungle", "#cobblemon:is_bamboo"]},
            },
            {
                "id": "xice-passimian-open-forest",
                "pokemon": "passimian",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "20-45",
                "weight": 80.0,
                "condition": {
                    "biomes": ["#cobblemon:is_forest", "#cobblemon:is_plains", "#cobblemon:is_savanna"]
                },
            },
            {
                "id": "xice-stonjourner-warm-rocklands",
                "pokemon": "stonjourner",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "grounded",
                "bucket": "uncommon",
                "level": "22-47",
                "weight": 80.0,
                "condition": {
                    "biomes": ["#cobblemon:is_badlands", "#cobblemon:is_peak"],
                    "minY": 64,
                },
            },
            {
                "id": "xice-eiscue-freezing-coasts",
                "pokemon": "eiscue",
                "presets": ["natural"],
                "type": "pokemon",
                "spawnablePositionType": "surface",
                "bucket": "uncommon",
                "level": "22-47",
                "weight": 80.0,
                "condition": {
                    "biomes": [
                        "#cobblemon:is_frozen_ocean", "#cobblemon:is_cold_ocean",
                        "#cobblemon:is_glacial", "#cobblemon:is_tundra"
                    ]
                },
            },
        ],
    }

    # Single-stage ecological rules. Battle-only transformations (Wishiwashi's
    # School Form and Morpeko's Hangry Mode) are intentionally left to their
    # abilities; the spawn table controls habitat, level and initial form only.
    custom_spawns["spawns"].extend([
        {
            "id": "xice-castform-normal-clear",
            "pokemon": "castform forecast_form=normal",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "10-35", "weight": 80.0,
            "condition": {
                "dimensions": ["minecraft:overworld"], "isRaining": False,
                "biomes": ["#cobblemon:is_plains", "#cobblemon:is_forest"],
            },
        },
        {
            "id": "xice-castform-sunny-hot",
            "pokemon": "castform forecast_form=sunny",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "10-35", "weight": 100.0,
            "condition": {
                "dimensions": ["minecraft:overworld"], "isRaining": False,
                "biomes": ["#cobblemon:is_desert", "#cobblemon:is_savanna", "#cobblemon:is_badlands"],
            },
        },
        {
            "id": "xice-castform-rainy-rain",
            "pokemon": "castform forecast_form=rainy",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "10-35", "weight": 100.0,
            "condition": {
                "dimensions": ["minecraft:overworld"], "isRaining": True,
                "biomes": ["#cobblemon:is_plains", "#cobblemon:is_forest", "#cobblemon:is_swamp", "#cobblemon:is_jungle"],
            },
        },
        {
            "id": "xice-castform-snowy-snow",
            "pokemon": "castform forecast_form=snowy",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "10-35", "weight": 100.0,
            "condition": {
                "dimensions": ["minecraft:overworld"], "isRaining": True,
                "biomes": ["#cobblemon:is_cold", "#cobblemon:is_snowy", "#cobblemon:is_tundra"],
            },
        },
        {
            "id": "xice-oricorio-baile-warm",
            "pokemon": "oricorio dance_style=baile",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "12-37", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_savanna", "#cobblemon:is_badlands"]},
        },
        {
            "id": "xice-oricorio-pompom-plains",
            "pokemon": "oricorio dance_style=pom_pom",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "12-37", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_plains"]},
        },
        {
            "id": "xice-oricorio-pau-tropical",
            "pokemon": "oricorio dance_style=pa'u",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "12-37", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_jungle", "#cobblemon:is_beach"]},
        },
        {
            "id": "xice-oricorio-sensu-spooky",
            "pokemon": "oricorio dance_style=sensu",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "12-37", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_spooky", "#cobblemon:is_swamp"], "timeRange": "night"},
        },
        {
            "id": "xice-wishiwashi-ocean",
            "pokemon": "wishiwashi school=false",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "submerged",
            "bucket": "common", "level": "1-45", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_ocean", "#cobblemon:is_coast"]},
        },
        {
            "id": "xice-wishiwashi-fishing",
            "pokemon": "wishiwashi school=false",
            "type": "pokemon", "spawnablePositionType": "fishing",
            "bucket": "common", "level": "1-45", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_ocean", "#cobblemon:is_coast"]},
        },
        {
            "id": "xice-morpeko-berry-habitat",
            "pokemon": "morpeko",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "19-44", "weight": 80.0,
            "weightMultipliers": [{"multiplier": 5.0, "condition": {"isThundering": True}}],
            "condition": {
                "biomes": ["#cobblemon:is_overworld"], "neededNearbyBlocks": ["#cobblemon:berries"],
            },
        },
        {
            "id": "xice-indeedee-village",
            "pokemon": "indeedee gender=male",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "23-48", "weight": 100.0,
            "condition": {"structures": ["#minecraft:village"]},
        },
        {
            "id": "xice-indeedee-mansion",
            "pokemon": "indeedee gender=female",
            "presets": ["mansion"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "23-48", "weight": 100.0,
            "condition": {"structures": ["minecraft:mansion"]},
        },
        {
            "id": "xice-indeedee-male-lich-tower",
            "pokemon": "indeedee gender=male",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "23-48", "weight": 50.0,
            "condition": {"structures": ["twilightforest:lich_tower"]},
        },
        {
            "id": "xice-indeedee-female-lich-tower",
            "pokemon": "indeedee gender=female",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "23-48", "weight": 50.0,
            "condition": {"structures": ["twilightforest:lich_tower"]},
        },
        {
            "id": "xice-mimikyu-haunted-structures",
            "pokemon": "mimikyu",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "23-48", "weight": 100.0,
            "condition": {"structures": [
                "minecraft:ancient_city", "minecraft:mineshaft", "minecraft:mineshaft_mesa",
                "minecraft:mansion", "minecraft:trail_ruins",
                "cobblemon:ruins/decaying_crypt_ruins", "cobblemon:ruins/deserted_house_ruins",
                "cobblemon:ruins/deserted_tower_ruins", "cobblemon:ruins/hidden_bunker_ruins",
                "cobblemon:ruins/mossy_oubliette_ruins", "twilightforest:dark_tower",
                "twilightforest:lich_tower", "twilightforest:final_castle",
            ]},
        },
        {
            "id": "xice-sableye-deep-caves",
            "pokemon": "sableye",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "15-40", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_cave"], "maxY": 32, "canSeeSky": False},
        },
        {
            "id": "xice-sableye-amethyst-bonus",
            "pokemon": "sableye",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "15-40", "weight": 200.0,
            "condition": {"maxY": 48, "canSeeSky": False, "nearbyCrystalRadius": 8},
        },
        {
            "id": "xice-sableye-dark-ruins",
            "pokemon": "sableye",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "20-45", "weight": 100.0,
            "condition": {"structures": [
                "minecraft:ancient_city", "minecraft:mineshaft", "minecraft:mineshaft_mesa",
                "cobblemon:ruins/decaying_crypt_ruins", "cobblemon:ruins/mossy_oubliette_ruins",
                "twilightforest:dark_tower", "twilightforest:lich_tower",
            ]},
        },
        {
            "id": "xice-mawile-caves",
            "pokemon": "mawile",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "15-40", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_cave"], "maxY": 48, "canSeeSky": False},
        },
        {
            "id": "xice-mawile-mineshafts",
            "pokemon": "mawile",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "18-43", "weight": 150.0,
            "condition": {"structures": ["minecraft:mineshaft", "minecraft:mineshaft_mesa"]},
        },
        {
            "id": "xice-kecleon-jungles",
            "pokemon": "kecleon",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "15-40", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_jungle"], "canSeeSky": True},
        },
        {
            "id": "xice-kecleon-jungle-pyramid",
            "pokemon": "kecleon",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "20-45", "weight": 200.0,
            "condition": {"structures": ["minecraft:jungle_pyramid"]},
        },
        {
            "id": "xice-carbink-deep-rock",
            "pokemon": "carbink",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "20-45", "weight": 80.0,
            "condition": {"biomes": ["#cobblemon:is_cave"], "maxY": 0, "canSeeSky": False},
        },
        {
            "id": "xice-carbink-amethyst-geodes",
            "pokemon": "carbink",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "20-45", "weight": 200.0,
            "condition": {"maxY": 48, "canSeeSky": False, "nearbyCrystalRadius": 8},
        },
        {
            "id": "xice-klefki-artificial-structures",
            "pokemon": "klefki",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "20-45", "weight": 100.0,
            "condition": {"structures": [
                "#minecraft:village", "minecraft:mansion", "minecraft:stronghold",
                "minecraft:trial_chambers", "twilightforest:knight_stronghold",
            ]},
        },
        {
            "id": "xice-falinks-pillager-outpost",
            "pokemon": "falinks",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "20-45", "weight": 100.0,
            "condition": {"structures": ["minecraft:pillager_outpost"]},
        },
        {
            "id": "xice-falinks-strongholds",
            "pokemon": "falinks",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "25-50", "weight": 100.0,
            "condition": {"structures": ["minecraft:stronghold", "twilightforest:knight_stronghold"]},
        },
        {
            "id": "xice-absol-mountains",
            "pokemon": "absol",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "25-50", "weight": 100.0,
            "weightMultipliers": [{"multiplier": 3.0, "condition": {"isThundering": True}}],
            "condition": {"biomes": ["#cobblemon:is_mountain", "#cobblemon:is_peak"], "minY": 80},
        },
        {
            "id": "xice-absol-dangerous-structures",
            "pokemon": "absol",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "25-50", "weight": 150.0,
            "condition": {
                "dimensions": ["minecraft:overworld", "twilightforest:twilight_forest"],
                "nearbyStructureRadius": 48,
                "nearbyStructures": [
                    "minecraft:ancient_city", "minecraft:mansion", "minecraft:pillager_outpost",
                    "minecraft:ruined_portal", "minecraft:ruined_portal_desert",
                    "minecraft:ruined_portal_jungle", "minecraft:ruined_portal_mountain",
                    "minecraft:ruined_portal_ocean", "minecraft:ruined_portal_swamp",
                    "twilightforest:dark_tower", "twilightforest:lich_tower",
                    "twilightforest:final_castle", "twilightforest:knight_stronghold",
                ],
            },
        },
        {
            "id": "xice-lapras-cold-ocean",
            "pokemon": "lapras",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "surface",
            "bucket": "rare", "level": "29-54", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_cold_ocean"]},
        },
        {
            "id": "xice-lapras-frozen-ocean",
            "pokemon": "lapras",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "surface",
            "bucket": "uncommon", "level": "29-54", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_frozen_ocean"]},
        },
        {
            "id": "xice-luvdisc-warm-ocean-pairs",
            "pokemon": "luvdisc",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "submerged",
            "bucket": "common", "level": "8-33", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_warm_ocean"]},
        },
        {
            "id": "xice-alomomola-warm-ocean",
            "pokemon": "alomomola",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "submerged",
            "bucket": "uncommon", "level": "22-47", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_warm_ocean"]},
        },
        {
            "id": "xice-pyukumuku-shallow-coasts",
            "pokemon": "pyukumuku",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "seafloor",
            "bucket": "common", "level": "16-41", "weight": 100.0,
            "condition": {
                "biomes": ["#cobblemon:is_beach", "#cobblemon:is_coast", "#cobblemon:is_warm_ocean"],
                "minY": 55, "maxY": 68,
            },
        },
        {
            "id": "xice-bruxish-warm-ocean",
            "pokemon": "bruxish",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "submerged",
            "bucket": "uncommon", "level": "23-48", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_warm_ocean"]},
        },
        {
            "id": "xice-bruxish-coral-reef-bonus",
            "pokemon": "bruxish",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "submerged",
            "bucket": "uncommon", "level": "23-48", "weight": 200.0,
            "condition": {"biomes": ["#cobblemon:is_ocean"], "nearbyCoralRadius": 8},
        },
        {
            "id": "xice-cramorant-rivers-coasts",
            "pokemon": "cramorant",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "surface",
            "bucket": "uncommon", "level": "23-48", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_river", "#cobblemon:is_coast"]},
        },
        {
            "id": "xice-pincurchin-rocky-seafloor",
            "pokemon": "pincurchin",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "seafloor",
            "bucket": "common", "level": "19-44", "weight": 100.0,
            "condition": {
                "biomes": ["#cobblemon:is_coast", "#cobblemon:is_ocean"],
                "neededBaseBlocks": [
                    "minecraft:stone", "minecraft:gravel", "minecraft:andesite",
                    "minecraft:tuff", "minecraft:cobblestone",
                ],
            },
        },
        {
            "id": "xice-veluza-deep-ocean-schools",
            "pokemon": "veluza",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "submerged",
            "bucket": "uncommon", "level": "23-48", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_deep_ocean"]},
        },
        {
            "id": "xice-pinsir-forests",
            "pokemon": "pinsir",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "18-43", "weight": 100.0,
            "weightMultipliers": [{"multiplier": 2.0, "condition": {"timeRange": "night"}}],
            "condition": {"biomes": ["#cobblemon:is_forest"]},
        },
        {
            "id": "xice-pinsir-old-growth-bonus",
            "pokemon": "pinsir",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "18-43", "weight": 150.0,
            "weightMultipliers": [{"multiplier": 2.0, "condition": {"timeRange": "night"}}],
            "condition": {"biomes": [
                "minecraft:old_growth_birch_forest", "minecraft:old_growth_pine_taiga",
                "minecraft:old_growth_spruce_taiga",
            ]},
        },
        {
            "id": "xice-heracross-forests",
            "pokemon": "heracross",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "18-43", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_forest"]},
        },
        {
            "id": "xice-heracross-honey-bonus",
            "pokemon": "heracross",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "18-43", "weight": 200.0,
            "condition": {"biomes": ["#cobblemon:is_forest"], "nearbyHoneyRadius": 8},
        },
        {
            "id": "xice-shuckle-rocky-forests",
            "pokemon": "shuckle",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "12-37", "weight": 100.0,
            "condition": {"biomes": [
                "#cobblemon:is_forest", "#cobblemon:is_hills", "#cobblemon:is_mountain",
            ]},
        },
        {
            "id": "xice-shuckle-berries-bonus",
            "pokemon": "shuckle",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "12-37", "weight": 200.0,
            "condition": {"nearbyBerryRadius": 8},
        },
        {
            "id": "xice-tropius-jungles",
            "pokemon": "tropius",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "22-47", "weight": 80.0,
            "condition": {"biomes": ["#cobblemon:is_jungle"], "canSeeSky": True},
        },
        {
            "id": "xice-tropius-berries-bonus",
            "pokemon": "tropius",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "22-47", "weight": 160.0,
            "condition": {"biomes": ["#cobblemon:is_jungle"], "nearbyBerryRadius": 8},
        },
        {
            "id": "xice-tropius-large-trees-bonus",
            "pokemon": "tropius",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "22-47", "weight": 80.0,
            "condition": {"biomes": ["#cobblemon:is_jungle"], "nearbyTreeRadius": 6},
        },
        {
            "id": "xice-pachirisu-forest-groups",
            "pokemon": "pachirisu",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "10-35", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_forest"]},
        },
        {
            "id": "xice-chatot-day-flocks",
            "pokemon": "chatot",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "15-40", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_jungle", "#cobblemon:is_savanna"], "timeRange": "day"},
        },
        {
            "id": "xice-carnivine-wet-lush-habitats",
            "pokemon": "carnivine",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "18-43", "weight": 100.0,
            "condition": {"biomes": [
                "#cobblemon:is_swamp", "#cobblemon:is_lush", "minecraft:mangrove_swamp",
                "minecraft:lush_caves",
            ]},
        },
        {
            "id": "xice-emolga-forest-hill-groups",
            "pokemon": "emolga",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "15-40", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_forest", "#cobblemon:is_hills"]},
        },
        {
            "id": "xice-komala-tree-day",
            "pokemon": "komala",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "15-40", "weight": 100.0,
            "condition": {
                "biomes": ["#cobblemon:is_forest"], "timeRange": "day", "nearbyTreeRadius": 4,
            },
        },
        {
            "id": "xice-delibird-snowy-habitats",
            "pokemon": "delibird",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "15-40", "weight": 100.0,
            "condition": {"biomes": [
                "#cobblemon:is_snowy_forest", "#cobblemon:is_tundra", "#cobblemon:is_snowy",
            ]},
        },
        {
            "id": "xice-delibird-snowy-village-bonus",
            "pokemon": "delibird",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "15-40", "weight": 200.0,
            "condition": {"structures": ["minecraft:village_snowy"]},
        },
        {
            "id": "xice-skarmory-high-peaks",
            "pokemon": "skarmory",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "25-50", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_mountain", "#cobblemon:is_peak"], "minY": 100},
        },
        {
            "id": "xice-skarmory-iron-bonus",
            "pokemon": "skarmory",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "25-50", "weight": 200.0,
            "condition": {
                "biomes": ["#cobblemon:is_mountain", "#cobblemon:is_peak"],
                "minY": 64, "nearbyIronRadius": 8,
            },
        },
        {
            "id": "xice-torkoal-volcanic-lands",
            "pokemon": "torkoal",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "20-45", "weight": 100.0,
            "condition": {"biomes": [
                "#cobblemon:is_badlands", "#cobblemon:is_volcanic",
                "#cobblemon:nether/is_basalt", "#cobblemon:nether/is_wasteland",
            ]},
        },
        {
            "id": "xice-torkoal-magma-bonus",
            "pokemon": "torkoal",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "20-45", "weight": 200.0,
            "condition": {
                "biomes": [
                    "#cobblemon:is_badlands", "#cobblemon:is_volcanic",
                    "#cobblemon:nether/is_basalt", "#cobblemon:nether/is_wasteland",
                ],
                "nearbyMagmaRadius": 8,
            },
        },
        {
            "id": "xice-maractus-arid-lands",
            "pokemon": "maractus",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "12-37", "weight": 100.0,
            "weightMultipliers": [{"multiplier": 0.5, "condition": {"isRaining": True}}],
            "condition": {"biomes": ["#cobblemon:is_desert", "#cobblemon:is_badlands"]},
        },
        {
            "id": "xice-maractus-cactus-bonus",
            "pokemon": "maractus",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "12-37", "weight": 200.0,
            "weightMultipliers": [{"multiplier": 0.5, "condition": {"isRaining": True}}],
            "condition": {
                "biomes": ["#cobblemon:is_desert", "#cobblemon:is_badlands"], "nearbyCactusRadius": 6,
            },
        },
        {
            "id": "xice-cryogonal-snowy-night",
            "pokemon": "cryogonal",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "25-50", "weight": 100.0,
            "weightMultipliers": [{"multiplier": 3.0, "condition": {"isRaining": True}}],
            "condition": {
                "biomes": ["#cobblemon:is_snowy", "#cobblemon:is_peak", "#cobblemon:is_tundra"],
                "timeRange": "night",
            },
        },
        {
            "id": "xice-druddigon-high-caves",
            "pokemon": "druddigon",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "25-50", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_cave"], "minY": 64, "canSeeSky": False},
        },
        {
            "id": "xice-druddigon-strongholds",
            "pokemon": "druddigon",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "30-55", "weight": 150.0,
            "condition": {"structures": ["minecraft:stronghold", "twilightforest:knight_stronghold"]},
        },
        {
            "id": "xice-hawlucha-highlands",
            "pokemon": "hawlucha",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "20-45", "weight": 100.0,
            "condition": {
                "biomes": [
                    "#cobblemon:is_mountain", "#cobblemon:is_peak", "#cobblemon:is_hills",
                    "#cobblemon:is_jungle",
                ],
                "minY": 90,
            },
        },
        {
            "id": "xice-klawf-rocky-cliff-groups",
            "pokemon": "klawf",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "18-43", "weight": 100.0,
            "condition": {
                "biomes": ["#cobblemon:is_mountain", "#cobblemon:is_peak", "#cobblemon:is_badlands"],
                "minY": 80,
            },
        },
        {
            "id": "xice-kangaskhan-nursery-groups",
            "pokemon": "kangaskhan",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "20-45", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_plains", "#cobblemon:is_savanna"]},
        },
        {
            "id": "xice-spinda-dryland-groups",
            "pokemon": "spinda",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "12-37", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_savanna", "#cobblemon:is_badlands"]},
        },
        {
            "id": "xice-audino-villages",
            "pokemon": "audino",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "15-40", "weight": 100.0,
            "condition": {"structures": ["#minecraft:village"]},
        },
        {
            "id": "xice-audino-healing-centers",
            "pokemon": "audino",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "15-40", "weight": 200.0,
            "condition": {"dimensions": ["minecraft:overworld"], "nearbyHealingRadius": 12},
        },
        {
            "id": "xice-bouffalant-grassland-herds",
            "pokemon": "bouffalant",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "20-45", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_plains", "#cobblemon:is_savanna"]},
        },
        {
            "id": "xice-furfrou-natural-settlements",
            "pokemon": "furfrou poodle_trim=natural",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "15-40", "weight": 100.0,
            "condition": {"structures": ["#minecraft:village"]},
        },
        {
            "id": "xice-furfrou-natural-plains",
            "pokemon": "furfrou poodle_trim=natural",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "15-40", "weight": 60.0,
            "condition": {"biomes": ["#cobblemon:is_plains"]},
        },
        {
            "id": "xice-dedenne-forest-edges",
            "pokemon": "dedenne",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "10-35", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_forest", "#cobblemon:is_plains"]},
        },
        {
            "id": "xice-dedenne-villages",
            "pokemon": "dedenne",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "10-35", "weight": 100.0,
            "condition": {"structures": ["#minecraft:village"]},
        },
        {
            "id": "xice-dedenne-redstone-bonus",
            "pokemon": "dedenne",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "10-35", "weight": 200.0,
            "condition": {"dimensions": ["minecraft:overworld"], "nearbyRedstoneRadius": 8},
        },
        {
            "id": "xice-togedemaru-foothills-plains",
            "pokemon": "togedemaru",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "15-40", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_hills", "#cobblemon:is_plains"]},
        },
        {
            "id": "xice-togedemaru-iron-bonus",
            "pokemon": "togedemaru",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "15-40", "weight": 150.0,
            "condition": {"nearbyIronRadius": 8},
        },
        {
            "id": "xice-togedemaru-redstone-bonus",
            "pokemon": "togedemaru",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "15-40", "weight": 150.0,
            "condition": {"nearbyRedstoneRadius": 8},
        },
        {
            "id": "xice-squawkabilly-green-plains-village",
            "pokemon": "squawkabilly squawkabilly_color=green",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "12-37", "weight": 100.0,
            "condition": {"structures": ["minecraft:village_plains"]},
        },
        {
            "id": "xice-squawkabilly-blue-cold-villages",
            "pokemon": "squawkabilly squawkabilly_color=blue",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "12-37", "weight": 100.0,
            "condition": {"structures": ["minecraft:village_snowy", "minecraft:village_taiga"]},
        },
        {
            "id": "xice-squawkabilly-yellow-savanna",
            "pokemon": "squawkabilly squawkabilly_color=yellow",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "12-37", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_savanna"]},
        },
        {
            "id": "xice-squawkabilly-gray-desert-village",
            "pokemon": "squawkabilly squawkabilly_color=gray",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "12-37", "weight": 100.0,
            "condition": {"structures": ["minecraft:village_desert"]},
        },
        {
            "id": "xice-cyclizar-open-land-groups",
            "pokemon": "cyclizar",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "20-45", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_plains", "#cobblemon:is_savanna"]},
        },
        {
            "id": "xice-flamigo-wetland-flocks",
            "pokemon": "flamigo",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "15-40", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_swamp", "minecraft:mangrove_swamp"]},
        },
        {
            "id": "xice-flamigo-shallow-lakes",
            "pokemon": "flamigo",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "surface",
            "bucket": "common", "level": "15-40", "weight": 80.0,
            "condition": {"biomes": ["#cobblemon:is_river", "#cobblemon:is_swamp"], "minY": 55},
        },
        {
            "id": "xice-stunfisk-muddy-shallows",
            "pokemon": "stunfisk",
            "presets": ["water"], "type": "pokemon", "spawnablePositionType": "seafloor",
            "bucket": "common", "level": "22-47", "weight": 100.0,
            "condition": {
                "biomes": ["#cobblemon:is_swamp", "#cobblemon:is_river"], "minY": 50, "maxY": 68,
            },
        },
        {
            "id": "xice-stunfisk-galarian-caves",
            "pokemon": "stunfisk galarian",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "22-47", "weight": 100.0,
            "condition": {"biomes": ["#cobblemon:is_cave"], "canSeeSky": False},
        },
        {
            "id": "xice-stunfisk-galarian-iron-bonus",
            "pokemon": "stunfisk galarian",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "22-47", "weight": 200.0,
            "condition": {"canSeeSky": False, "nearbyIronRadius": 8},
        },
        {
            "id": "xice-comfey-flower-habitats",
            "pokemon": "comfey",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "20-45", "weight": 100.0,
            "condition": {"biomes": ["minecraft:flower_forest", "#cobblemon:is_lush", "minecraft:lush_caves"]},
        },
        {
            "id": "xice-turtonator-volcanic",
            "pokemon": "turtonator",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "24-49", "weight": 100.0,
            "condition": {"biomes": [
                "#cobblemon:is_volcanic", "#cobblemon:nether/is_basalt",
                "#cobblemon:nether/is_wasteland",
            ]},
        },
        {
            "id": "xice-turtonator-magma-bonus",
            "pokemon": "turtonator",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "24-49", "weight": 200.0,
            "condition": {
                "biomes": [
                    "#cobblemon:is_volcanic", "#cobblemon:nether/is_basalt",
                    "#cobblemon:nether/is_wasteland",
                ],
                "nearbyMagmaRadius": 8,
            },
        },
        {
            "id": "xice-drampa-high-mountain-forest",
            "pokemon": "drampa",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "rare", "level": "24-49", "weight": 100.0,
            "weightMultipliers": [
                {"multiplier": 2.0, "condition": {"isRaining": True}},
                {"multiplier": 1.5, "condition": {"isThundering": True}},
            ],
            "condition": {
                "biomes": ["#cobblemon:is_mountain", "#cobblemon:is_hills", "#cobblemon:is_forest"],
                "minY": 100,
            },
        },
        {
            "id": "xice-bombirdier-high-cliffs",
            "pokemon": "bombirdier",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "20-45", "weight": 100.0,
            "condition": {
                "biomes": ["#cobblemon:is_peak", "#cobblemon:is_mountain", "#cobblemon:is_badlands"],
                "minY": 90,
            },
        },
        {
            "id": "xice-bombirdier-settlements-ruins-bonus",
            "pokemon": "bombirdier",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "20-45", "weight": 150.0,
            "condition": {
                "nearbyStructureRadius": 48,
                "nearbyStructures": [
                    "minecraft:village_desert", "minecraft:village_plains",
                    "minecraft:village_savanna", "minecraft:village_snowy",
                    "minecraft:village_taiga", "minecraft:trail_ruins",
                    "minecraft:desert_pyramid", "minecraft:jungle_pyramid",
                ] + COBBLEMON_RUINS,
            },
        },
        {
            "id": "xice-orthworm-arid-underground",
            "pokemon": "orthworm",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "20-45", "weight": 100.0,
            "condition": {
                "biomes": ["#cobblemon:is_desert", "#cobblemon:is_badlands"],
                "maxY": 48, "canSeeSky": False,
            },
        },
        {
            "id": "xice-orthworm-iron-bonus",
            "pokemon": "orthworm",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "20-45", "weight": 150.0,
            "condition": {"maxY": 64, "canSeeSky": False, "nearbyIronRadius": 8},
        },
        {
            "id": "xice-orthworm-create-infrastructure-bonus",
            "pokemon": "orthworm",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "common", "level": "20-45", "weight": 150.0,
            "condition": {"maxY": 64, "nearbyInfrastructureRadius": 8},
        },
    ])

    minior_colors = ["red", "orange", "yellow", "green", "blue", "indigo", "violet"]
    for color in minior_colors:
        base = {
            "pokemon": f"minior meteor_shield=meteor core_color={color}",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "bucket": "uncommon", "level": "20-50", "weight": 100.0,
        }
        custom_spawns["spawns"].append({
            **base,
            "id": f"xice-minior-{color}-overworld-sky",
            "condition": {
                "dimensions": ["minecraft:overworld"], "minY": 200,
                "canSeeSky": True, "noStructureRadius": 32,
            },
        })
        custom_spawns["spawns"].append({
            **base,
            "id": f"xice-minior-{color}-end",
            "condition": {"dimensions": ["minecraft:the_end"], "noStructureRadius": 32},
        })

    unown_characters = list("abcdefghijklmnopqrstuvwxyz") + ["!", "?"]
    structured_dimensions = [
        "minecraft:overworld", "minecraft:the_nether", "twilightforest:twilight_forest"
    ]
    for character in unown_characters:
        weight = 10.0 if character in {"!", "?"} else 37.692
        common = {
            "pokemon": f"unown character={character}",
            "presets": ["natural"], "type": "pokemon", "spawnablePositionType": "grounded",
            "level": "1-100", "weight": weight,
        }
        custom_spawns["spawns"].append({
            **common,
            "id": f"xice-unown-{ord(character)}-structured-realms",
            "bucket": "ultra-rare",
            "condition": {
                "dimensions": structured_dimensions,
                "requiresAnyStructure": True,
                "excludedStructures": ["twilightforest:lich_tower"],
            },
        })
        custom_spawns["spawns"].append({
            **common,
            "id": f"xice-unown-{ord(character)}-lich-tower",
            "bucket": "rare",
            "condition": {"structures": ["twilightforest:lich_tower"]},
        })
        custom_spawns["spawns"].append({
            **common,
            "id": f"xice-unown-{ord(character)}-end-wilderness",
            "bucket": "common",
            "condition": {"dimensions": ["minecraft:the_end"], "requiresNoStructure": True},
        })

    custom_spawn_path = data_root / "xice_single_stage_specials.json"
    custom_spawn_path.write_text(
        json.dumps(custom_spawns, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )
    changed_files += 1

    redstone_tag_path = (
        OUTPUT / "data" / "xices_cobblemon_fix" / "tags" / "block" /
        "rotom_redstone_components.json"
    )
    redstone_tag_path.parent.mkdir(parents=True, exist_ok=True)
    redstone_tag_path.write_text(
        json.dumps({"replace": False, "values": ROTOM_REDSTONE_COMPONENTS}, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )

    infrastructure_tag_path = (
        OUTPUT / "data" / "xices_cobblemon_fix" / "tags" / "block" /
        "infrastructure_components.json"
    )
    infrastructure_tag_path.write_text(
        json.dumps({"replace": False, "values": INFRASTRUCTURE_COMPONENTS}, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )

    crystal_tag_path = (
        OUTPUT / "data" / "xices_cobblemon_fix" / "tags" / "block" /
        "crystal_sources.json"
    )
    crystal_tag_path.write_text(
        json.dumps({"replace": False, "values": CRYSTAL_SOURCES}, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )

    coral_tag_path = (
        OUTPUT / "data" / "xices_cobblemon_fix" / "tags" / "block" /
        "coral_reef_blocks.json"
    )
    coral_tag_path.write_text(
        json.dumps({"replace": False, "values": CORAL_REEF_BLOCKS}, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )

    for tag_name, values in {
        "honey_sources": HONEY_SOURCES,
        "berry_sources": BERRY_SOURCES,
        "tree_sources": TREE_SOURCES,
        "iron_sources": IRON_SOURCES,
        "magma_sources": MAGMA_SOURCES,
        "cactus_sources": CACTUS_SOURCES,
        "healing_sources": HEALING_SOURCES,
    }.items():
        tag_path = (
            OUTPUT / "data" / "xices_cobblemon_fix" / "tags" / "block" /
            f"{tag_name}.json"
        )
        tag_path.write_text(
            json.dumps({"replace": False, "values": values}, ensure_ascii=False, indent=2) + "\n",
            encoding="utf-8",
        )

    pack_meta = {
        "pack": {
            "pack_format": 48,
            "description": "Xice 通用宝可梦生成规则（Cobblemon 1.8.0）",
        }
    }
    (OUTPUT / "pack.mcmeta").write_text(
        json.dumps(pack_meta, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )

    def sorted_list(values):
        return sorted(values)

    category_counts = defaultdict(int)
    for species_id, bucket in final_bucket.items():
        if species_id not in disabled:
            category_counts[bucket] += 1

    report = {
        "sourceSpecies": len(species_by_id),
        "sourceSpawnFiles": len(spawn_files),
        "generatedOverrideFiles": changed_files,
        "changedSpawnEntries": changed_entries,
        "removedSpawnEntries": removed_entries,
        "removedHerdMembers": removed_herd_members,
        "assignedSpeciesByBucket": dict(sorted(category_counts.items())),
        "specialCounts": {
            "starterRoots": len(starter_roots),
            "starterFamilySpeciesForcedUltraRare": len(starter_families),
            "pseudoFamilyForcedUltraRare": len(PSEUDO_FAMILIES & set(species_by_id)),
            "friendshipEvolutionTargetsDisabled": len(friendship_targets),
            "fossilsDisabled": len(fossils),
            "legendaryDisabled": len(legendary),
            "mythicalDisabled": len(mythical),
            "ultraBeastsDisabled": len(ultra_beasts),
            "paradoxDisabled": len(paradox),
            "exclusiveCustomSpawnSpecies": len(EXCLUSIVE_CUSTOM_SPAWN_SPECIES),
            "customSpawnEntries": len(custom_spawns["spawns"]),
            "otherSpecialEvolutionEdgesLeftUnchanged": sum(1 for _s, _t, kind in edges if kind == "other"),
        },
        "specialSpecies": {
            "starterRoots": sorted_list(starter_roots),
            "starterFamilies": sorted_list(starter_families),
            "pseudoFamilies": sorted_list(PSEUDO_FAMILIES & set(species_by_id)),
            "friendshipTargets": sorted_list(friendship_targets),
            "fossils": sorted_list(fossils),
            "legendary": sorted_list(legendary),
            "mythical": sorted_list(mythical),
            "ultraBeasts": sorted_list(ultra_beasts),
            "paradox": sorted_list(paradox),
        },
        "notes": [
            "Species without an evolution relationship retain their original Cobblemon bucket.",
            "Nested herd files retain their special herd/boss bucket; disabled species are removed from herd members.",
            "Fossil, legendary, mythical, Ultra Beast, and Paradox acquisition must be configured separately.",
        ],
    }
    (OUTPUT / "generation-report.json").write_text(
        json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )

    print(json.dumps({key: value for key, value in report.items() if key != "specialSpecies"}, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
