$ErrorActionPreference = 'Stop'

$packRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$profilePath = Join-Path $packRoot 'Xice的方块宝可梦.json'
$modsPath = Join-Path $packRoot 'mods'

$profile = Get-Content -LiteralPath $profilePath -Raw | ConvertFrom-Json
$gameArguments = @($profile.arguments.game)

function Get-GameArgumentValue([string] $Name) {
    $index = [Array]::IndexOf($gameArguments, $Name)
    if ($index -ge 0 -and $index + 1 -lt $gameArguments.Count) {
        return [string] $gameArguments[$index + 1]
    }
    return $null
}

$mods = @(
    Get-ChildItem -LiteralPath $modsPath -Filter '*.jar' -File |
        Sort-Object Name |
        ForEach-Object {
            [ordered]@{
                file = $_.Name
                sizeBytes = $_.Length
                sha256 = (Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
            }
        }
)

$manifest = [ordered]@{
    name = 'Xice的方块宝可梦'
    minecraftVersion = [string] $profile.clientVersion
    modLoader = 'NeoForge'
    modLoaderVersion = Get-GameArgumentValue '--fml.neoForgeVersion'
    javaMajorVersion = [int] $profile.javaVersion.majorVersion
    cobblemonVersion = '1.8.0'
    generatedAtUtc = [DateTime]::UtcNow.ToString('yyyy-MM-ddTHH:mm:ssZ')
    modCount = $mods.Count
    mods = $mods
}

$utf8NoBom = [System.Text.UTF8Encoding]::new($false)
$manifestText = $manifest | ConvertTo-Json -Depth 6
[System.IO.File]::WriteAllText((Join-Path $packRoot 'pack-info.json'), $manifestText + "`n", $utf8NoBom)

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add('# 模组清单')
$lines.Add('')
$lines.Add("共 $($mods.Count) 个模组文件。版本与文件名来自当前实例；第三方 JAR 不直接提交到本仓库。")
$lines.Add('')
$lines.Add('| 文件 | 大小（字节） | SHA-256 |')
$lines.Add('|---|---:|---|')
foreach ($mod in $mods) {
    $lines.Add(('| `{0}` | {1} | `{2}` |' -f $mod.file, $mod.sizeBytes, $mod.sha256))
}
[System.IO.File]::WriteAllText((Join-Path $packRoot 'MODLIST.md'), ($lines -join "`n") + "`n", $utf8NoBom)

Write-Output "Exported $($mods.Count) mods to pack-info.json and MODLIST.md"
