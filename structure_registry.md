# Xice 的方块宝可梦：结构注册名清单

扫描时间：2026-09-08  
游戏版本：Minecraft 1.21.1  
加载器：NeoForge 21.1.250  
Cobblemon：1.8.0+1.21.1  

## 统计口径

- 本清单统计动态注册表 `minecraft:worldgen/structure` 中的结构，即可用于 `/locate structure <注册名>` 的结构类型。
- 未把 `data/<命名空间>/structures/*.nbt` 下的拼装模板误算成可定位结构。
- 已扫描当前版本 JAR、`mods` 目录的 50 个直接安装 JAR，以及当前世界和版本目录中的数据包。
- 最近一次成功启动日志列出 102 个已加载条目；其中大量是 Forgified Fabric API 子模块和库。
- 当前共 166 个结构：原版 34、Cobblemon 67、暮色森林 21、Mega Showdown 5、Better End 14、Moog's End Structures 25。
- 当前世界的数据包没有新增 `worldgen/structure` 注册项。
- 中文名称优先采用已安装模组提供的 `zh_cn` 文本；没有官方文本的条目按英文注册路径直译，仅用于阅读。注册名才是命令输入时的准确标识。

## 原版 Minecraft（34）

| 结构名称 | 注册名 |
|---|---|
| 远古城市 | `minecraft:ancient_city` |
| 堡垒遗迹 | `minecraft:bastion_remnant` |
| 埋藏的宝藏 | `minecraft:buried_treasure` |
| 沙漠神殿 | `minecraft:desert_pyramid` |
| 末地城 | `minecraft:end_city` |
| 下界要塞 | `minecraft:fortress` |
| 雪屋 | `minecraft:igloo` |
| 丛林神庙 | `minecraft:jungle_pyramid` |
| 林地府邸 | `minecraft:mansion` |
| 废弃矿井 | `minecraft:mineshaft` |
| 恶地废弃矿井 | `minecraft:mineshaft_mesa` |
| 海底神殿 | `minecraft:monument` |
| 下界化石 | `minecraft:nether_fossil` |
| 冷水海底废墟 | `minecraft:ocean_ruin_cold` |
| 温水海底废墟 | `minecraft:ocean_ruin_warm` |
| 掠夺者前哨站 | `minecraft:pillager_outpost` |
| 废弃传送门 | `minecraft:ruined_portal` |
| 沙漠废弃传送门 | `minecraft:ruined_portal_desert` |
| 丛林废弃传送门 | `minecraft:ruined_portal_jungle` |
| 山地废弃传送门 | `minecraft:ruined_portal_mountain` |
| 下界废弃传送门 | `minecraft:ruined_portal_nether` |
| 海洋废弃传送门 | `minecraft:ruined_portal_ocean` |
| 沼泽废弃传送门 | `minecraft:ruined_portal_swamp` |
| 沉船 | `minecraft:shipwreck` |
| 搁浅的沉船 | `minecraft:shipwreck_beached` |
| 要塞 | `minecraft:stronghold` |
| 沼泽小屋 | `minecraft:swamp_hut` |
| 古迹废墟 | `minecraft:trail_ruins` |
| 试炼密室 | `minecraft:trial_chambers` |
| 沙漠村庄 | `minecraft:village_desert` |
| 平原村庄 | `minecraft:village_plains` |
| 热带草原村庄 | `minecraft:village_savanna` |
| 雪原村庄 | `minecraft:village_snowy` |
| 针叶林村庄 | `minecraft:village_taiga` |

## Cobblemon（67）

### 钓鱼船（3）

| 结构名称 | 注册名 |
|---|---|
| 沙滩钓鱼船 | `cobblemon:fishing_boat/beach` |
| 深海钓鱼船 | `cobblemon:fishing_boat/deep_ocean` |
| 温暖海洋钓鱼船 | `cobblemon:fishing_boat/warm_ocean` |

### 栖息地（32）

| 结构名称 | 注册名 |
|---|---|
| 恶地遮阴岩石 | `cobblemon:habitats/badlands_shaded_rock` |
| 树果丛 | `cobblemon:habitats/berry_patch` |
| 白桦林野火痕迹 | `cobblemon:habitats/birch_wildfire_scar` |
| 虫丘 | `cobblemon:habitats/bug_mound` |
| 雕刻冰刺 | `cobblemon:habitats/carved_ice_spikes` |
| 深海尖塔 | `cobblemon:habitats/deep_sea_spire` |
| 沙漠绿洲 | `cobblemon:habitats/desert_oasis` |
| 沙漠遮阴岩石 | `cobblemon:habitats/desert_shaded_rock` |
| 漂流冰山 | `cobblemon:habitats/drifting_icebergs` |
| 妖精丘 | `cobblemon:habitats/fae_mounds` |
| 花床林间空地 | `cobblemon:habitats/flowerbed_clearing` |
| 淡水池塘 | `cobblemon:habitats/freshwater_pond` |
| 真菌居所 | `cobblemon:habitats/fungal_dwelling` |
| 繁茂树冠 | `cobblemon:habitats/lush_canopy` |
| 繁茂天然井 | `cobblemon:habitats/lush_cenote` |
| 繁茂泥炭沼泽 | `cobblemon:habitats/lush_peat_bog` |
| 陨石撞击坑 | `cobblemon:habitats/meteorite_impact` |
| 天然避雷针 | `cobblemon:habitats/natural_lightningrod` |
| 橡树林野火痕迹 | `cobblemon:habitats/oak_wildfire_scar` |
| 干涸泥炭沼泽 | `cobblemon:habitats/parched_peat_bog` |
| 粉色花床林间空地 | `cobblemon:habitats/pinkflowerbed_clearing` |
| 重获生机的废弃纪念碑 | `cobblemon:habitats/reclaimed_deserted_monument` |
| 重获生机的繁茂纪念碑 | `cobblemon:habitats/reclaimed_lush_monument` |
| 沙坑林间空地 | `cobblemon:habitats/sandpit_clearing` |
| 雪地洞穴 | `cobblemon:habitats/snowy_burrow` |
| 雪地洞窟 | `cobblemon:habitats/snowy_grotto` |
| 雪地热泉口 | `cobblemon:habitats/snowy_thermal_vents` |
| 云杉林野火痕迹 | `cobblemon:habitats/spruce_wildfire_scar` |
| 向日葵花床林间空地 | `cobblemon:habitats/sunflowerbed_clearing` |
| 日灼林间空地 | `cobblemon:habitats/sunscorched_clearing` |
| 热裂隙 | `cobblemon:habitats/thermal_crevices` |
| 禅意花园 | `cobblemon:habitats/zen_garden` |

### 遗址（29）

| 结构名称 | 注册名 |
|---|---|
| 古代高台遗址 | `cobblemon:ruins/ancient_dais_ruins` |
| 破碎拱门遗址 | `cobblemon:ruins/crumbling_arch_ruins` |
| 腐朽墓穴遗址 | `cobblemon:ruins/decaying_crypt_ruins` |
| 废弃索财灵塔 | `cobblemon:ruins/deserted_gimmi_tower` |
| 废弃房屋遗址 | `cobblemon:ruins/deserted_house_ruins` |
| 废弃纪念碑遗址 | `cobblemon:ruins/deserted_monument_ruins` |
| 废弃高塔遗址 | `cobblemon:ruins/deserted_tower_ruins` |
| 废弃城镇中心遗址 | `cobblemon:ruins/deserted_town_center_ruins` |
| 倒塌雕像遗址 | `cobblemon:ruins/fallen_statue_ruins` |
| 冰冻祭坛遗址 | `cobblemon:ruins/frozen_altar_ruins` |
| 冰冻索财灵塔 | `cobblemon:ruins/frozen_gimmi_tower` |
| 隐蔽掩体遗址 | `cobblemon:ruins/hidden_bunker_ruins` |
| 月之石阵遗址 | `cobblemon:ruins/luna_henge_ruins` |
| 繁茂索财灵塔 | `cobblemon:ruins/lush_gimmi_tower` |
| 繁茂纪念碑遗址 | `cobblemon:ruins/lush_monument_ruins` |
| 陨石战场遗址 | `cobblemon:ruins/meteor_battleground_ruins` |
| 长苔地牢遗址 | `cobblemon:ruins/mossy_oubliette_ruins` |
| 古旧花园遗址 | `cobblemon:ruins/old_garden_ruins` |
| 蔓生试炼遗址 | `cobblemon:ruins/overgrown_trial_ruins` |
| 扎根拱门遗址 | `cobblemon:ruins/rooted_arch_ruins` |
| 扎根索财灵塔 | `cobblemon:ruins/rooted_gimmi_tower` |
| 日之石阵遗址 | `cobblemon:ruins/sol_henge_ruins` |
| 巨石丁石阵遗址 | `cobblemon:ruins/stonjourner_henge_ruins` |
| 水下锻造场遗址 | `cobblemon:ruins/submerged_forge_ruins` |
| 日灼索财灵塔 | `cobblemon:ruins/sunscorched_gimmi_tower` |
| 日灼遮阴遗址 | `cobblemon:ruins/sunscorched_shaded_ruins` |
| 温带索财灵塔 | `cobblemon:ruins/temperate_gimmi_tower` |
| 倾倒石柱遗址 | `cobblemon:ruins/toppled_pillars_ruins` |
| 不稳定洞穴遗址 | `cobblemon:ruins/unstable_cave_ruins` |

### 沉船湾（3）

| 结构名称 | 注册名 |
|---|---|
| 繁茂沉船湾 | `cobblemon:shipwreck_coves/lush_shipwreck_cove` |
| 岩浆沉船湾 | `cobblemon:shipwreck_coves/magma_shipwreck_cove` |
| 水下沉船湾 | `cobblemon:shipwreck_coves/submerged_shipwreck_cove` |

## 暮色森林（21）

| 结构名称 | 注册名 |
|---|---|
| 极光宫殿 | `twilightforest:aurora_palace` |
| 营地 | `twilightforest:camp` |
| 黑暗高塔 | `twilightforest:dark_tower` |
| 倒下的树干 | `twilightforest:fallen_trunk` |
| 终焉高原城堡 | `twilightforest:final_castle` |
| 巨人屋 | `twilightforest:giant_house` |
| 树篱迷宫 | `twilightforest:hedge_maze` |
| 空心树 | `twilightforest:hollow_tree` |
| 九头蛇巢穴 | `twilightforest:hydra_lair` |
| 黑松骑士要塞 | `twilightforest:knight_stronghold` |
| 牛头人迷宫 | `twilightforest:labyrinth` |
| 亡灵矿山（大型空心矿山） | `twilightforest:large_hollow_hill` |
| 巫妖怪塔 | `twilightforest:lich_tower` |
| 工兵矿山（中型空心矿山） | `twilightforest:medium_hollow_hill` |
| 巨蕈堡垒 | `twilightforest:mushroom_tower` |
| 娜迦庭院 | `twilightforest:naga_courtyard` |
| 谜题羊树丛 | `twilightforest:quest_grove` |
| 空心矿山（小型空心矿山） | `twilightforest:small_hollow_hill` |
| 沼泽空心树 | `twilightforest:swamp_hollow_tree` |
| 巨魔洞穴 | `twilightforest:troll_cave` |
| 雪怪洞窟 | `twilightforest:yeti_cave` |

## Cobblemon: Mega Showdown（5）

| 结构名称 | 注册名 |
|---|---|
| 考古遗址 | `mega_showdown:archaeological_site` |
| Mega Site（超级遗址/矿场之一） | `mega_showdown:mega_site` |
| Megaroid（超级遗址/矿场之一） | `mega_showdown:megaroid` |
| 天文台 | `mega_showdown:observatory` |
| 许愿森林 | `mega_showdown:wishing_weald` |

## Better End（14）

Better End 没有为这些结构提供独立的中文显示文本，以下名称按注册路径直译。

| 结构名称 | 注册名 |
|---|---|
| 末地桥梁 | `betterend:end_bridge` |
| 末地湖泊 | `betterend:end_lake` |
| 普通末地湖泊 | `betterend:end_lake_normal` |
| 稀有末地湖泊 | `betterend:end_lake_rare` |
| 末地村庄 | `betterend:end_village` |
| 永恒传送门 | `betterend:eternal_portal` |
| 巨型冰星 | `betterend:giant_ice_star` |
| 巨型苔藓发光菇 | `betterend:giant_mossy_glowshroom` |
| 巨型湖泊 | `betterend:megalake` |
| 小型巨湖 | `betterend:megalake_small` |
| 山脉 | `betterend:mountain` |
| 彩绘山脉 | `betterend:painted_mountain` |
| 小型岛屿 | `betterend:small_island` |
| 硫磺洞穴 | `betterend:sulphuric_cave` |

## Moog's End Structures（25）

该模组没有提供中文语言文件，以下名称按英文注册路径直译。

| 结构名称 | 注册名 |
|---|---|
| 星界隐居所 | `mes:astral_hideaway` |
| 星界陨石 | `mes:astral_meteorite` |
| 末影尖塔 | `mes:ender_spire` |
| 末影花树林 | `mes:enderbloom_grove` |
| 末影要塞庭院 | `mes:enderkeep_courtyard` |
| Enderpin 尖刺 | `mes:enderpin_spikes` |
| Enderskog | `mes:enderskog` |
| 末影瞭望塔 | `mes:enderwatch_tower` |
| 末地残骸 | `mes:endscraps` |
| 手稿神殿 | `mes:manuscript_shrine` |
| 巨型飞船 | `mes:mega_ship` |
| 基础巨型飞船 | `mes:mega_ship_basic` |
| 坠毁的巨型飞船 | `mes:mega_ship_crashed` |
| 坠毁的巨型飞船 II | `mes:mega_ship_crashed_2` |
| 坠毁的深板岩巨型飞船 | `mes:mega_ship_crashed_deepslate` |
| 深板岩巨型飞船 | `mes:mega_ship_deepslate` |
| 深板岩巨型飞船 II | `mes:mega_ship_deepslate_2` |
| 深板岩巨型飞船 III | `mes:mega_ship_deepslate_3` |
| 巨石碑 | `mes:monolith` |
| 神秘拱门 | `mes:mystical_archway` |
| 神话花园 | `mes:mythic_garden` |
| 幻影堡垒 | `mes:phantom_citadel` |
| 宁静草原 | `mes:placid_prairie` |
| 废墟石柱 | `mes:ruined_pillar` |
| 星光航行者 | `mes:starlight_voyager` |

## 命令示例

```mcfunction
/locate structure cobblemon:habitats/meteorite_impact
/locate structure twilightforest:aurora_palace
/locate structure mega_showdown:wishing_weald
/locate structure betterend:eternal_portal
/locate structure mes:phantom_citadel
```

结构是否能在当前维度被找到，还取决于其生物群系、维度和结构集生成条件；“已注册”不等于在任意维度都能生成。
