# Конвертер Blockbench .bbmodel -> GeckoLib .geo.json / vanilla block model JSON.
#   powershell -File tools\convert_bbmodel.ps1 -InFile Assets\gribg.bbmodel -Output out.geo.json -Mode geo -Identifier gribg
#   powershell -File tools\convert_bbmodel.ps1 -InFile Assets\brick.bbmodel -Output out.json -Mode block -TextureRef "floress_mod:block/lying_brick"
# -EntitySpace: сдвиг из block-space (0..16) в entity-space (центр 0, низ на y=0)
param(
    [Parameter(Mandatory=$true)][string]$InFile,
    [Parameter(Mandatory=$true)][string]$Output,
    [Parameter(Mandatory=$true)][ValidateSet("geo","block")][string]$Mode,
    [string]$Identifier = "model",
    [string]$TextureRef = "minecraft:block/stone",
    [switch]$EntitySpace
)

$ErrorActionPreference = "Stop"
$bb = Get-Content $InFile -Raw | ConvertFrom-Json

# ВАЖНО: в PowerShell внутри @(...) каст [double] перехватывает всё выражение с запятыми,
# поэтому вся арифметика — только через предвычисление в переменные.
function Vec3($x, $y, $z) { return @([double]$x, [double]$y, [double]$z) }
function Sub3($a, $b) { return Vec3 ($a[0] - $b[0]) ($a[1] - $b[1]) ($a[2] - $b[2]) }

$groups = @{}
if ($bb.groups) {
    foreach ($g in $bb.groups) {
        $groups[$g.uuid] = @{ name = $g.name; origin = $g.origin; parentUuid = $null }
    }
}

$boneCubes = @{}
function Walk-Outliner($nodes, $parentUuid) {
    foreach ($n in $nodes) {
        if ($n -is [string]) {
            if ($parentUuid) { $boneCubes[$parentUuid] += @($n) }
            continue
        }
        if ($n.uuid) {
            if ($groups.ContainsKey($n.uuid)) { $groups[$n.uuid].parentUuid = $parentUuid }
            if (-not $boneCubes.ContainsKey($n.uuid)) { $boneCubes[$n.uuid] = @() }
            Walk-Outliner $n.children $n.uuid
        }
    }
}
Walk-Outliner $bb.outliner $null

$elements = @{}
foreach ($e in $bb.elements) { $elements[$e.uuid] = $e }

$shift = @(0.0, 0.0, 0.0)
if ($EntitySpace) {
    $minX = 999.0; $minY = 999.0; $minZ = 999.0
    $maxX = -999.0; $maxZ = -999.0
    foreach ($e in $bb.elements) {
        if ($e.from[0] -lt $minX) { $minX = [double]$e.from[0] }
        if ($e.from[1] -lt $minY) { $minY = [double]$e.from[1] }
        if ($e.from[2] -lt $minZ) { $minZ = [double]$e.from[2] }
        if ($e.to[0] -gt $maxX) { $maxX = [double]$e.to[0] }
        if ($e.to[2] -gt $maxZ) { $maxZ = [double]$e.to[2] }
    }
    # Центруем реальные границы модели: это работает и для 0..16, и для уже центрированных bbmodel.
    $centerX = ($minX + $maxX) / 2.0
    $centerZ = ($minZ + $maxZ) / 2.0
    $shift = @(-$centerX, -$minY, -$centerZ)
}
function Shift-Vec($v) {
    return Vec3 ($v[0] + $shift[0]) ($v[1] + $shift[1]) ($v[2] + $shift[2])
}

function Convert-CubeGeo($e) {
    $from = Shift-Vec $e.from
    $size = Sub3 $e.to $e.from
    $cube = [ordered]@{ origin = $from; size = $size }
    if ($e.rotation) {
        $cube.pivot = Shift-Vec $e.origin
        $cube.rotation = Vec3 $e.rotation[0] $e.rotation[1] $e.rotation[2]
    }
    if ($e.box_uv) {
        $cube.uv = @([int]$e.uv_offset[0], [int]$e.uv_offset[1])
    } else {
        $uv = [ordered]@{}
        foreach ($face in @("north","east","south","west","up","down")) {
            $f = $e.faces.$face
            if (-not $f -or -not $f.uv) { continue }
            $w = $f.uv[2] - $f.uv[0]
            $h = $f.uv[3] - $f.uv[1]
            $uv[$face] = [ordered]@{ uv = @([double]$f.uv[0], [double]$f.uv[1]); uv_size = @([double]$w, [double]$h) }
        }
        $cube.uv = $uv
    }
    return $cube
}

if ($Mode -eq "geo") {
    $bones = @()
    foreach ($uuid in $groups.Keys) {
        $g = $groups[$uuid]
        $bone = [ordered]@{ name = $g.name; pivot = (Shift-Vec $g.origin) }
        if ($g.parentUuid -and $groups.ContainsKey($g.parentUuid)) {
            $bone.parent = $groups[$g.parentUuid].name
        }
        $cubes = @()
        foreach ($cubeUuid in $boneCubes[$uuid]) {
            if ($elements.ContainsKey($cubeUuid)) { $cubes += Convert-CubeGeo ($elements[$cubeUuid]) }
        }
        if ($cubes.Count -gt 0) { $bone.cubes = $cubes }
        $bones += $bone
    }
    $geo = [ordered]@{
        format_version = "1.12.0"
        "minecraft:geometry" = @([ordered]@{
            description = [ordered]@{
                identifier = "geometry.$Identifier"
                texture_width = $bb.resolution.width
                texture_height = $bb.resolution.height
            }
            bones = $bones
        })
    }
    [System.IO.File]::WriteAllText($Output, ($geo | ConvertTo-Json -Depth 20), (New-Object System.Text.UTF8Encoding($false)))
    Write-Host "geo -> $Output (bones: $($bones.Count))"
} else {
    $cubes = @()
    foreach ($e in $bb.elements) {
        $fromV = @(Vec3 $e.from[0] $e.from[1] $e.from[2])
        $toV = @(Vec3 $e.to[0] $e.to[1] $e.to[2])
        $faceSwap = $null
        $keepRotation = $false
        if ($e.rotation) {
            $rx = [double]$e.rotation[0]; $ry = [double]$e.rotation[1]; $rz = [double]$e.rotation[2]
            $nz = 0; if ($rx -ne 0) { $nz++ }; if ($ry -ne 0) { $nz++ }; if ($rz -ne 0) { $nz++ }
            $single = if ($rx -ne 0) { $rx } elseif ($ry -ne 0) { $ry } else { $rz }
            if ($nz -eq 1 -and [Math]::Abs($single) -eq 180) {
                # 180° ваниль не умеет — запекаем поворот в координаты
                $o = $e.origin
                if ($rx -ne 0) {
                    $fromV = @(Vec3 $e.from[0] (2*$o[1]-$e.to[1]) (2*$o[2]-$e.to[2]))
                    $toV = @(Vec3 $e.to[0] (2*$o[1]-$e.from[1]) (2*$o[2]-$e.from[2]))
                    $faceSwap = @{ north="south"; south="north"; up="down"; down="up" }
                } elseif ($ry -ne 0) {
                    $fromV = @(Vec3 (2*$o[0]-$e.to[0]) $e.from[1] (2*$o[2]-$e.to[2]))
                    $toV = @(Vec3 (2*$o[0]-$e.from[0]) $e.to[1] (2*$o[2]-$e.from[2]))
                    $faceSwap = @{ north="south"; south="north"; east="west"; west="east" }
                } else {
                    $fromV = @(Vec3 (2*$o[0]-$e.to[0]) (2*$o[1]-$e.to[1]) $e.from[2])
                    $toV = @(Vec3 (2*$o[0]-$e.from[0]) (2*$o[1]-$e.from[1]) $e.to[2])
                    $faceSwap = @{ east="west"; west="east"; up="down"; down="up" }
                }
            } else {
                # ваниль умеет только одноосные -45/-22.5/0/22.5/45 — всё остальное теряем
                $validAngles = @(-45, -22.5, 0, 22.5, 45)
                if ($nz -eq 1 -and $validAngles -contains $single) {
                    $keepRotation = $true
                } else {
                    Write-Warning "cube $($e.uuid): rotation [$($e.rotation -join ',')] not representable in vanilla, dropped"
                }
            }
        }
        $el = [ordered]@{ from = $fromV; to = $toV }
        if ($keepRotation) {
            $axis = "y"; $angle = [double]$e.rotation[1]
            if ([double]$e.rotation[0] -ne 0) { $axis = "x"; $angle = [double]$e.rotation[0] }
            elseif ([double]$e.rotation[2] -ne 0) { $axis = "z"; $angle = [double]$e.rotation[2] }
            $el.rotation = [ordered]@{
                origin = @(Vec3 $e.origin[0] $e.origin[1] $e.origin[2])
                axis = $axis; angle = $angle
            }
        }
        $faces = [ordered]@{}
        # UV в bbmodel — в пикселях текстуры; в ванильной модели — в сетке 0..16
        $uScale = 16.0 / [double]$bb.resolution.width
        $vScale = 16.0 / [double]$bb.resolution.height
        foreach ($face in @("north","east","south","west","up","down")) {
            $f = $e.faces.$face
            if (-not $f -or -not $f.uv) { continue }
            $target = if ($faceSwap -and $faceSwap.ContainsKey($face)) { $faceSwap[$face] } else { $face }
            $u0 = [double]($f.uv[0]) * $uScale
            $v0 = [double]($f.uv[1]) * $vScale
            $u1 = [double]($f.uv[2]) * $uScale
            $v1 = [double]($f.uv[3]) * $vScale
            $faces[$target] = [ordered]@{ uv = @($u0, $v0, $u1, $v1); texture = "#0" }
        }
        $el.faces = $faces
        $cubes += $el
    }
    $model = [ordered]@{
        parent = "minecraft:block/block"
        textures = [ordered]@{ "0" = $TextureRef; particle = $TextureRef }
        elements = $cubes
    }
    [System.IO.File]::WriteAllText($Output, ($model | ConvertTo-Json -Depth 20), (New-Object System.Text.UTF8Encoding($false)))
    Write-Host "block -> $Output (cubes: $($cubes.Count))"
}
