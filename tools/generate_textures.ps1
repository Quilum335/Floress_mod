# Генератор текстур floress_mod (запуск из корня проекта).
# Все текстуры процедурные 16x16/64x64 под UV-разметку моделей.

Add-Type -AssemblyName System.Drawing

$root = Split-Path -Parent $PSScriptRoot
$blockDir = Join-Path $root "src\main\resources\assets\floress_mod\textures\block"
$itemDir  = Join-Path $root "src\main\resources\assets\floress_mod\textures\item"
$entityDir = Join-Path $root "src\main\resources\assets\floress_mod\textures\entity"
New-Item -ItemType Directory -Force $blockDir, $itemDir, $entityDir | Out-Null

$script:rand = New-Object System.Random(1234)

function C([int]$r, [int]$g, [int]$b) { [System.Drawing.Color]::FromArgb($r, $g, $b) }
function Shade([System.Drawing.Color]$c, [int]$d) {
    C ([Math]::Min(255, [Math]::Max(0, $c.R + $d))) ([Math]::Min(255, [Math]::Max(0, $c.G + $d))) ([Math]::Min(255, [Math]::Max(0, $c.B + $d)))
}
function New-Bmp([int]$w, [int]$h, [bool]$transparent) {
    $bmp = New-Object System.Drawing.Bitmap($w, $h)
    if ($transparent) {
        for ($x = 0; $x -lt $w; $x++) { for ($y = 0; $y -lt $h; $y++) { $bmp.SetPixel($x, $y, [System.Drawing.Color]::Transparent) } }
    }
    return $bmp
}
function Fill([System.Drawing.Bitmap]$bmp, [int]$x1, [int]$y1, [int]$x2, [int]$y2, [System.Drawing.Color]$c) {
    for ($x = $x1; $x -le $x2; $x++) { for ($y = $y1; $y -le $y2; $y++) {
        if ($x -ge 0 -and $y -ge 0 -and $x -lt $bmp.Width -and $y -lt $bmp.Height) { $bmp.SetPixel($x, $y, $c) }
    } }
}
function NoiseFill([System.Drawing.Bitmap]$bmp, [int]$x1, [int]$y1, [int]$x2, [int]$y2, [System.Drawing.Color]$base, [int]$amp) {
    for ($x = $x1; $x -le $x2; $x++) { for ($y = $y1; $y -le $y2; $y++) {
        if ($x -ge 0 -and $y -ge 0 -and $x -lt $bmp.Width -and $y -lt $bmp.Height) {
            $bmp.SetPixel($x, $y, (Shade $base ($script:rand.Next(-$amp, $amp + 1))))
        }
    } }
}
function Ellipse([System.Drawing.Bitmap]$bmp, [double]$cx, [double]$cy, [double]$rx, [double]$ry, [System.Drawing.Color]$c) {
    for ($x = [int]($cx - $rx - 1); $x -le [int]($cx + $rx + 1); $x++) {
        for ($y = [int]($cy - $ry - 1); $y -le [int]($cy + $ry + 1); $y++) {
            $d = (($x - $cx) * ($x - $cx)) / ($rx * $rx) + (($y - $cy) * ($y - $cy)) / ($ry * $ry)
            if ($d -le 1.0 -and $x -ge 0 -and $y -ge 0 -and $x -lt $bmp.Width -and $y -lt $bmp.Height) { $bmp.SetPixel($x, $y, $c) }
        }
    }
}
function Save([System.Drawing.Bitmap]$bmp, [string]$path) {
    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    Write-Host "  $(Split-Path $path -Leaf)"
}

Write-Host "=== blocks ==="

# --- wormy_dirt: земля + розовые черви ---
$b = New-Bmp 16 16 $false
NoiseFill $b 0 0 15 15 (C 121 85 58) 14
for ($i = 0; $i -lt 3; $i++) {
    $sx = $script:rand.Next(1, 10); $sy = $script:rand.Next(2, 13)
    for ($t = 0; $t -lt 5; $t++) {
        $x = $sx + $t; $y = $sy + [int]([Math]::Sin($t * 1.2 + $i) * 1.5)
        Fill $b $x $y ($x + 1) $y (C 205 125 115)
    }
}
Save $b "$blockDir\wormy_dirt.png"

# --- dead_leaves: коричневая листва с прожилками и дырками ---
$b = New-Bmp 16 16 $false
NoiseFill $b 0 0 15 15 (C 104 88 62) 12
for ($x = 0; $x -lt 16; $x++) { $b.SetPixel($x, 7, (C 80 66 45)) }
for ($y = 0; $y -lt 16; $y++) { $b.SetPixel(7, $y, (C 80 66 45)) }
for ($i = 0; $i -lt 14; $i++) {
    $x = $script:rand.Next(16); $y = $script:rand.Next(16)
    $b.SetPixel($x, $y, [System.Drawing.Color]::Transparent)
}
Save $b "$blockDir\dead_leaves.png"

# --- poison_ivy: лиана с фиолетовыми ягодами ---
$b = New-Bmp 16 16 $true
foreach ($vx in @(2, 7, 12)) {
    for ($y = 0; $y -lt 16; $y++) {
        $x = $vx + [int]([Math]::Sin($y * 0.7 + $vx) * 1.2)
        $b.SetPixel($x, $y, (C 42 84 38))
        if ($y % 3 -eq 0) {
            Fill $b ($x - 1) $y ($x + 1) ($y + 1) (C 52 110 48)
        }
        if ($script:rand.Next(100) -lt 12) { $b.SetPixel($x, $y, (C 125 55 155)) }
    }
}
Save $b "$blockDir\poison_ivy.png"

# --- amanita: красный купол с белыми точками + ножка ---
$b = New-Bmp 16 16 $true
Ellipse $b 8 6.5 6.5 4.5 (C 196 38 30)
Ellipse $b 8 5 5 3 (C 214 60 50)
foreach ($p in @(@(4,5),@(7,4),@(10,6),@(12,5),@(6,8),@(9,8))) { $b.SetPixel($p[0], $p[1], (C 245 240 225)) }
Fill $b 6 10 9 15 (C 232 220 192)
Fill $b 6 10 7 15 (C 214 198 168)
Save $b "$blockDir\amanita.png"

# --- loose_brick: один кирпич (вид сверху 12x6) ---
$b = New-Bmp 16 16 $true
NoiseFill $b 0 0 11 5 (C 152 72 54) 10
for ($x = 0; $x -lt 12; $x++) { $b.SetPixel($x, 0, (C 110 48 34)); $b.SetPixel($x, 5, (C 110 48 34)) }
for ($y = 0; $y -lt 6; $y++) { $b.SetPixel(0, $y, (C 110 48 34)); $b.SetPixel(11, $y, (C 110 48 34)) }
Save $b "$blockDir\loose_brick.png"

# --- fruit: зелёный плод (рабочая зона 8x8 слева сверху) ---
$b = New-Bmp 16 16 $true
Ellipse $b 4 5 3.4 3.2 (C 96 160 60)
Ellipse $b 3.2 4 1.6 1.4 (C 130 195 90)
$b.SetPixel(4, 0, (C 90 60 35)); $b.SetPixel(4, 1, (C 90 60 35))
Save $b "$blockDir\fruit.png"

Write-Host "=== items ==="

# --- worm: сегментированный червь-дуга ---
$b = New-Bmp 16 16 $true
for ($i = 0; $i -lt 9; $i++) {
    $x = 3 + $i; $y = 8 + [int]([Math]::Sin($i * 0.75) * 3)
    $c = if ($i % 2 -eq 0) { C 205 128 118 } else { C 180 100 95 }
    Fill $b $x ($y - 1) ($x + 1) $y $c
}
$b.SetPixel(11, 4, (C 60 30 30))
Save $b "$itemDir\worm.png"

# --- яйца призыва ---
$b = New-Bmp 16 16 $true
Ellipse $b 8 8.5 5 6.5 (C 228 212 186)
foreach ($p in @(@(5,5),@(9,4),@(11,7),@(6,9),@(10,11),@(7,12))) { Fill $b $p[0] $p[1] ($p[0]+1) ($p[1]+1) (C 190 42 32) }
Save $b "$itemDir\living_mushroom_spawn_egg.png"

$b = New-Bmp 16 16 $true
Ellipse $b 8 8.5 5 6.5 (C 68 68 74)
foreach ($p in @(@(5,5),@(9,4),@(11,7),@(6,9),@(10,11),@(7,12))) { Fill $b $p[0] $p[1] ($p[0]+1) ($p[1]+1) (C 172 172 178) }
Save $b "$itemDir\fly_spawn_egg.png"

Write-Host "=== entities ==="

# --- living_mushroom 64x64 (UV под модель) ---
$b = New-Bmp 64 64 $false
NoiseFill $b 0 0 63 63 (C 232 220 192) 8
# шляпка: верх (10,0)-(20,10), низ (20,0)-(30,10), бока y10-14
NoiseFill $b 10 0 20 9 (C 205 48 38) 10
NoiseFill $b 20 0 30 9 (C 150 30 24) 8
NoiseFill $b 0 10 40 13 (C 196 38 30) 10
foreach ($p in @(@(12,2),@(16,5),@(18,1),@(3,11),@(13,12),@(23,11),@(33,12),@(8,12),@(28,12))) {
    Fill $b $p[0] $p[1] ($p[0]+1) ($p[1]+1) (C 245 240 225)
}
# ножка: верх (5,16)-(10,21), бока y21-25; лицо на передней грани (5,21)-(10,25)
NoiseFill $b 0 16 20 25 (C 232 220 192) 6
$b.SetPixel(6, 22, (C 25 20 18)); $b.SetPixel(8, 22, (C 25 20 18))
Fill $b 6 24 8 24 (C 90 50 45)
# ноги (40,16)-(48,21)
NoiseFill $b 40 16 48 21 (C 130 95 62) 8
Save $b "$entityDir\living_mushroom.png"

# --- fly 32x32 (UV под модель) ---
$b = New-Bmp 32 32 $false
NoiseFill $b 0 0 31 31 (C 62 62 68) 6
# тело: верх (6,0)-(10,6), бока y6-9; перед (6,6)-(10,9) — голова с глазами
NoiseFill $b 6 0 10 5 (C 74 74 82) 6
NoiseFill $b 0 6 20 8 (C 58 58 64) 6
Fill $b 6 6 9 8 (C 40 40 45)
$b.SetPixel(6, 6, (C 200 40 40)); $b.SetPixel(7, 6, (C 200 40 40))
$b.SetPixel(8, 6, (C 200 40 40)); $b.SetPixel(9, 6, (C 200 40 40))
# крылья: (4,10)-(7,14) и (16,10)-(19,14)
NoiseFill $b 4 10 7 13 (C 200 205 215) 5
NoiseFill $b 16 10 19 13 (C 200 205 215) 5
Save $b "$entityDir\fly.png"

Write-Host "Done."
