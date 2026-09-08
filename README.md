# Xice's Pokémon Package

这是“Xice的方块宝可梦”整合包的配置与自定义代码仓库。

## 基础版本

- Minecraft 1.21.1
- NeoForge 21.1.250
- Java 21
- Cobblemon 1.8.0

精确模组文件、大小及 SHA-256 校验值见 [`pack-info.json`](pack-info.json)，便于核对本地实例；便于阅读的清单见 [`MODLIST.md`](MODLIST.md)。第三方模组 JAR 不提交到仓库，需要按照清单自行取得对应版本。

## 仓库内容

- `config/`：客户端与通用配置。
- `datapacks/Xice通用宝可梦生成规则/`：当前世界使用的自定义生成数据包。
- `xice-spawn-rules/`：生成规则源码、物种分析数据及说明。
- `smartphone-progression-addon/`：智能手机升级、饰品槽、汉化与扫描限制附属模组源码。
- `xices-cobblemon-fix/`：Cobblemon 1.8.0 兼容修复及生成事件附属模组源码。
- `mods/xices_cobblemon_fix-1.0.11.jar`、`mods/smartphoneprogression-1.0.1.jar`：本仓库自制附属模组的当前构建产物。
- `biome_registry.md`、`structure_registry.md`：当前实例的群系与结构注册名清单。
- `options.txt`：整合包客户端选项与按键配置。

## 不同步的内容

世界存档、玩家数据、用户名缓存、日志、崩溃报告、地图缓存、启动器文件、Minecraft 本体、第三方模组 JAR 与本地构建缓存均由 `.gitignore` 排除。

## 更新清单

在实例根目录运行：

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\export_pack_metadata.ps1
```

生成数据规则后，应把世界中的数据包重新复制到仓库 `datapacks/` 目录，再提交变更。

