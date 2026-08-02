# Генератор ВРЕМЕННЫХ текстур-заглушек для floress_mod.
# Запускать из корня проекта: powershell -File tools\generate_placeholder_textures.ps1
# Когда будут настоящие текстуры — просто заменить PNG в assets/floress_mod/textures.

Add-Type -AssemblyName System.Drawing

$root = Split-Path -Parent $PSScriptRoot
$blockDir = Join-Path $root "src\main\resources\assets\floress_mod\textures\block"
$itemDir  = Join-Path $root "src\main\resources\assets\floress_mod\textures\item"
$entityDir = Join-Path $root "src\main\resources\assets\floress_mod\textures\entity"
New-Item -ItemType Directory -Force $blockDir, $itemDir, $entityDir | Out-Null

$rand = New-Object System.Random(42)

function New-Texture([string]$path, [int]$size, [System.Drawing.Color]$base, [int]$noise, [System.Drawing.Color]$noiseColor) {
    $bmp = New-Object System.Drawing.Bitmap($size, $size)
    for ($x = 0; $x -lt $size; $x++) {
        for ($y = 0; $y -lt $size; $y++) {
            if ($noise -gt 0 -and $rand.Next(100) -lt $noise) {
                $bmp.SetPixel($x, $y, $noiseColor)
            } else {
                $v = $rand.Next(-8, 9)
                $c = [System.Drawing.Color]::FromArgb(
                    [Math]::Min(255, [Math]::Max(0, $base.R + $v)),
                    [Math]::Min(255, [Math]::Max(0, $base.G + $v)),
                    [Math]::Min(255, [Math]::Max(0, $base.B + $v)))
                $bmp.SetPixel($x, $y, $c)
            }
        }
    }
    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    Write-Host "  $path"
}

function Color($r, $g, $b) { [System.Drawing.Color]::FromArgb($r, $g, $b) }

Write-Host "Block textures:"
New-Texture "$blockDir\wormy_dirt.png"       16 (Color 121  85  58) 20 (Color 160  60  60)
New-Texture "$blockDir\dead_leaves.png"      16 (Color 110  95  70) 25 (Color  70  60  45)
New-Texture "$blockDir\poison_ivy.png"       16 (Color  45  80  40) 25 (Color 110  40  130)
New-Texture "$blockDir\loose_brick.png"      16 (Color 150  60  45) 15 (Color 110  40  30)
New-Texture "$blockDir\fruit_fly.png"        16 (Color  80  40  90) 20 (Color  40  20  50)
New-Texture "$blockDir\fruit_harvest.png"    16 (Color 220 150  40) 20 (Color 180 100  20)
New-Texture "$blockDir\fruit_explosive.png"  16 (Color  40  40  40) 20 (Color 200  40  20)
New-Texture "$blockDir\fruit_rabbit.png"     16 (Color 230 225 215) 15 (Color 190 180 165)
New-Texture "$blockDir\fruit_zombie.png"     16 (Color  60 110  60) 20 (Color  30  60  30)
New-Texture "$blockDir\fruit_chicken.png"    16 (Color 230 210  60) 15 (Color 200 160  30)

# мухомор — красная шляпка с белыми точками на прозрачном фоне (как cross-текстура)
Write-Host "Amanita (cross):"
$bmp = New-Object System.Drawing.Bitmap(16, 16)
for ($x = 0; $x -lt 16; $x++) { for ($y = 0; $y -lt 16; $y++) { $bmp.SetPixel($x, $y, [System.Drawing.Color]::Transparent) } }
for ($x = 6; $x -le 9; $x++) { for ($y = 8; $y -le 15; $y++) { $bmp.SetPixel($x, $y, (Color 235 225 200)) } } # ножка
for ($x = 2; $x -le 13; $x++) { for ($y = 2; $y -le 7; $y++) { $bmp.SetPixel($x, $y, (Color 190 30 25)) } }   # шляпка
foreach ($p in @(@(4,3),@(7,5),@(10,3),@(12,5),@(6,4))) { $bmp.SetPixel($p[0], $p[1], [System.Drawing.Color]::White) }
$bmp.Save("$blockDir\amanita.png", [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()

Write-Host "Item textures:"
$bmp = New-Object System.Drawing.Bitmap(16, 16)
for ($x = 0; $x -lt 16; $x++) { for ($y = 0; $y -lt 16; $y++) { $bmp.SetPixel($x, $y, [System.Drawing.Color]::Transparent) } }
for ($y = 4; $y -le 11; $y++) { for ($x = (7 - [int]($y/3)); $x -le (8 + [int]($y/3)); $x++) { $bmp.SetPixel($x, $y, (Color 190 120 110)) } } # червяк-дуга
$bmp.Save("$itemDir\worm.png", [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()

Write-Host "Entity textures:"
New-Texture "$entityDir\living_mushroom.png" 64 (Color 230 220 195) 0 ([System.Drawing.Color]::White)
$bmp = [System.Drawing.Image]::FromFile("$entityDir\living_mushroom.png")
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.FillRectangle((New-Object System.Drawing.SolidBrush((Color 190 30 25))), 0, 10, 40, 14) # зона шляпки
$g.Dispose(); $bmp.Save("$entityDir\living_mushroom.png", [System.Drawing.Imaging.ImageFormat]::Png); $bmp.Dispose()
New-Texture "$entityDir\fly.png"             32 (Color  70  70  70) 25 (Color 160 160 170)

Write-Host "Done."
