# Конвертер анимаций Blockbench (GeckoLib-проект) -> GeckoLib .animation.json
# Повторяет официальный экспорт Blockbench: инвертирует X/Y у вращений,
# сортирует ключи по времени и сохраняет формат интерполяции GeckoLib.
#   powershell -File tools\convert_animation.ps1 -InFile Assets\gribg.bbmodel -Output out.animation.json
param(
    [Parameter(Mandatory=$true)][string]$InFile,
    [Parameter(Mandatory=$true)][string]$Output
)

$ErrorActionPreference = "Stop"
$bb = Get-Content $InFile -Raw | ConvertFrom-Json

function Format-Time([double]$t) {
    if ($t -eq [Math]::Floor($t)) { return "$([int]$t).0" }
    return $t.ToString("0.####", [System.Globalization.CultureInfo]::InvariantCulture)
}

$anims = [ordered]@{}
foreach ($a in $bb.animations) {
    $bones = [ordered]@{}
    foreach ($prop in $a.animators.PSObject.Properties) {
        $animator = $prop.Value
        $channels = [ordered]@{}
        foreach ($kf in @($animator.keyframes | Sort-Object { [double]$_.time })) {
            $dp = $kf.data_points[0]
            if ($kf.channel -eq "rotation") {
                $vec = @(-[double]$dp.x, -[double]$dp.y, [double]$dp.z)
            } else {
                $vec = @([double]$dp.x, [double]$dp.y, [double]$dp.z)
            }
            $t = Format-Time ([double]$kf.time)
            if (-not $channels.Contains($kf.channel)) { $channels[$kf.channel] = [ordered]@{} }
            if ($kf.interpolation -eq "catmullrom") {
                $channels[$kf.channel][$t] = [ordered]@{
                    post = [ordered]@{ vector = $vec }
                    lerp_mode = "catmullrom"
                }
            } else {
                $channels[$kf.channel][$t] = [ordered]@{ vector = $vec }
            }
        }
        if ($channels.Count -gt 0) { $bones[$animator.name] = $channels }
    }
    $anims[$a.name] = [ordered]@{
        loop = ($a.loop -eq "loop")
        animation_length = [double]$a.length
        bones = $bones
    }
}

$out = [ordered]@{ format_version = "1.8.0"; animations = $anims }
[System.IO.File]::WriteAllText($Output, ($out | ConvertTo-Json -Depth 30), (New-Object System.Text.UTF8Encoding($false)))
Write-Host "animations -> $Output ($($anims.Count))"
