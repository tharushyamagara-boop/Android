Add-Type -AssemblyName System.Drawing

$imgPath = (Resolve-Path 'store_icon.png').Path
$img = [System.Drawing.Bitmap]::FromFile($imgPath)
$minX = $img.Width
$maxX = 0
$minY = $img.Height
$maxY = 0

for ($y = 0; $y -lt $img.Height; $y++) {
    for ($x = 0; $x -lt $img.Width; $x++) {
        $pixel = $img.GetPixel($x, $y)
        if ($pixel.A -gt 10) {
            if ($x -lt $minX) { $minX = $x }
            if ($x -gt $maxX) { $maxX = $x }
            if ($y -lt $minY) { $minY = $y }
            if ($y -gt $maxY) { $maxY = $y }
        }
    }
}

Write-Host "Original Image size: $($img.Width) x $($img.Height)"
Write-Host "Bounding box: minX=$minX, maxX=$maxX, minY=$minY, maxY=$maxY"
$padLeft = $minX
$padRight = $img.Width - 1 - $maxX
$padTop = $minY
$padBottom = $img.Height - 1 - $maxY
Write-Host "Padding: left=$padLeft, right=$padRight, top=$padTop, bottom=$padBottom"

# Now let's crop the image to the exact tight bounding box of the main square logo!
# Notice the small dot on the bottom right might add extra right padding.
# Let's crop to the exact content bounding box!
$cropWidth = $maxX - $minX + 1
$cropHeight = $maxY - $minY + 1

$croppedBmp = New-Object System.Drawing.Bitmap($cropWidth, $cropHeight, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($croppedBmp)
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality

$srcRect = New-Object System.Drawing.Rectangle($minX, $minY, $cropWidth, $cropHeight)
$destRect = New-Object System.Drawing.Rectangle(0, 0, $cropWidth, $cropHeight)
$g.DrawImage($img, $destRect, $srcRect, [System.Drawing.GraphicsUnit]::Pixel)

$croppedBmp.Save('app/src/main/res/drawable/splash_icon.png', [System.Drawing.Imaging.ImageFormat]::Png)
$croppedBmp.Save('app/src/main/res/drawable-xxxhdpi/splash_icon.png', [System.Drawing.Imaging.ImageFormat]::Png)

$g.Dispose()
$croppedBmp.Dispose()
$img.Dispose()

Write-Host "Saved tightly cropped logo with 0px transparent padding to splash_icon.png ($cropWidth x $cropHeight)"
