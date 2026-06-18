package aki.pawar.qr.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Stunning Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    surfaceTint = SurfaceTintLight
)

// Stunning Dark Color Scheme - Cyberpunk inspired
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    surfaceTint = SurfaceTintDark
)

@Composable
fun QrAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color to use our custom stunning palette
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use background color for status bar for immersive feel
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

// ==========================================
// GRADIENT BRUSHES FOR STUNNING UI EFFECTS
// ==========================================

// Primary gradient - Teal to Purple
val PrimaryGradient = Brush.linearGradient(
    colors = listOf(NeonTeal, NeonPurple)
)

// Secondary gradient - Coral to Pink
val SecondaryGradient = Brush.linearGradient(
    colors = listOf(NeonCoral, NeonPink)
)

// Vibrant gradient - Full spectrum
val VibrantGradient = Brush.linearGradient(
    colors = listOf(NeonTeal, NeonPurple, NeonCoral)
)

// Card gradient for dark mode
val CardGradient = Brush.verticalGradient(
    colors = listOf(CardGradientStart, CardGradientEnd)
)

// Scanner frame gradient
val ScannerGradient = Brush.sweepGradient(
    colors = listOf(NeonTeal, NeonPurple, NeonCoral, NeonTeal)
)

// Shimmer gradient
val ShimmerGradientLight = Brush.linearGradient(
    colors = listOf(ShimmerLight, ShimmerHighlight, ShimmerLight)
)

val ShimmerGradientDark = Brush.linearGradient(
    colors = listOf(ShimmerDark, ShimmerHighlightDark, ShimmerDark)
)

// ==========================================
// EXTENSION PROPERTIES FOR EASY ACCESS
// ==========================================

// Check if current theme is dark
@Composable
fun isDarkTheme(): Boolean = isSystemInDarkTheme()

// Get appropriate shimmer gradient
@Composable
fun shimmerGradient(): Brush = if (isSystemInDarkTheme()) ShimmerGradientDark else ShimmerGradientLight
