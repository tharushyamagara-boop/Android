Add-Type -AssemblyName System.Drawing

function Generate-SplashImage {
    param(
        [string]$IconPath,
        [string]$OutputPath,
        [int]$TargetWidth,
        [int]$TargetHeight,
        [int]$IconHeight,
        [int]$FontSize,
        [int]$Spacing
    )

    $bmp = New-Object System.Drawing.Bitmap($TargetWidth, $TargetHeight, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit

    $g.Clear([System.Drawing.Color]::Transparent)

    $icon = [System.Drawing.Image]::FromFile((Resolve-Path $IconPath))
    
    # Maintain aspect ratio of cropped icon
    $iconAspect = $icon.Width / [float]$icon.Height
    $iconWidth = [int]($IconHeight * $iconAspect)

    # Font
    $fontFamily = New-Object System.Drawing.FontFamily('Segoe UI')
    $font = New-Object System.Drawing.Font($fontFamily, $FontSize, [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
    $text = 'LopRok'
    $textSize = $g.MeasureString($text, $font)

    # Center alignment
    $totalContentWidth = $iconWidth + $Spacing + $textSize.Width
    $startX = ($TargetWidth - $totalContentWidth) / 2.0
    $startY_Icon = ($TargetHeight - $IconHeight) / 2.0
    $startY_Text = ($TargetHeight - $textSize.Height) / 2.0

    # Draw icon
    $g.DrawImage($icon, [float]$startX, [float]$startY_Icon, [float]$iconWidth, [float]$IconHeight)

    # Draw text
    $textBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)
    $textX = $startX + $iconWidth + $Spacing
    $g.DrawString($text, $font, $textBrush, [float]$textX, [float]$startY_Text)

    $bmp.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)

    $g.Dispose()
    $bmp.Dispose()
    $icon.Dispose()
    $font.Dispose()
    $textBrush.Dispose()
    Write-Host "Generated $OutputPath"
}

# Use the tightly cropped splash_icon.png with 0px transparent padding!
$iconSrc = 'app/src/main/res/drawable/splash_icon.png'
Generate-SplashImage -IconPath $iconSrc -OutputPath 'app/src/main/res/drawable-xxxhdpi/splash.png' -TargetWidth 1200 -TargetHeight 480 -IconHeight 220 -FontSize 175 -Spacing 25
Generate-SplashImage -IconPath $iconSrc -OutputPath 'app/src/main/res/drawable-xxhdpi/splash.png' -TargetWidth 900 -TargetHeight 360 -IconHeight 165 -FontSize 130 -Spacing 18
Generate-SplashImage -IconPath $iconSrc -OutputPath 'app/src/main/res/drawable-xhdpi/splash.png' -TargetWidth 600 -TargetHeight 240 -IconHeight 110 -FontSize 87 -Spacing 12
Generate-SplashImage -IconPath $iconSrc -OutputPath 'app/src/main/res/drawable-hdpi/splash.png' -TargetWidth 450 -TargetHeight 180 -IconHeight 82 -FontSize 65 -Spacing 9
Generate-SplashImage -IconPath $iconSrc -OutputPath 'app/src/main/res/drawable-mdpi/splash.png' -TargetWidth 300 -TargetHeight 120 -IconHeight 55 -FontSize 43 -Spacing 6
