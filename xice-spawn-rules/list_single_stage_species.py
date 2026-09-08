from __future__ import annotations

import json
import zipfile
from pathlib import Path

from generate_spawn_datapack import COBBLEMON_JAR, iter_evolutions, result_species, species_id_from_path


OUTPUT = Path(__file__).with_name("single-stage-species.json")


def main() -> None:
    species_by_id: dict[str, dict] = {}
    incoming: set[str] = set()
    outgoing: set[str] = set()

    with zipfile.ZipFile(COBBLEMON_JAR) as jar:
        lang = json.loads(jar.read("assets/cobblemon/lang/zh_cn.json"))
        for name in jar.namelist():
            if name.startswith("data/cobblemon/species/") and name.endswith(".json"):
                species_by_id[species_id_from_path(name)] = json.loads(jar.read(name))

    for source, species in species_by_id.items():
        for evolution in iter_evolutions(species):
            raw_result = evolution.get("result")
            if not isinstance(raw_result, str):
                continue
            target = result_species(raw_result)
            if target in species_by_id and target != source:
                outgoing.add(source)
                incoming.add(target)

    result = []
    for species_id, species in species_by_id.items():
        if species_id in incoming or species_id in outgoing:
            continue
        result.append(
            {
                "nationalPokedexNumber": species.get("nationalPokedexNumber"),
                "id": species_id,
                "name": lang.get(f"cobblemon.species.{species_id}.name", species.get("name", species_id)),
                "labels": sorted(species.get("labels", []) or []),
            }
        )

    result.sort(key=lambda item: (item["nationalPokedexNumber"] or 99999, item["id"]))
    OUTPUT.write_text(
        json.dumps({"definition": "no incoming or outgoing evolution", "count": len(result), "species": result}, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    print(json.dumps({"count": len(result), "output": str(OUTPUT), "species": result}, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
