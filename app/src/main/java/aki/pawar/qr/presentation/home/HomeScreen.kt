package aki.pawar.qr.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aki.pawar.qr.ui.theme.NeonBlue
import aki.pawar.qr.ui.theme.NeonCoral
import aki.pawar.qr.ui.theme.NeonGreen
import aki.pawar.qr.ui.theme.NeonPurple
import aki.pawar.qr.ui.theme.NeonTeal
import aki.pawar.qr.ui.theme.NeonYellow
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

/**
 * Stunning Home Screen with animated cards and modern design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToScanner: () -> Unit,
    onNavigateToGenerator: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Animated background circles
        AnimatedBackground(isDark)
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { },
                    actions = {
                        IconButton(
                            onClick = onNavigateToHistory,
                            modifier = Modifier
                                .padding(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.History,
                                contentDescription = "History",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                // Animated Title
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(
                        initialOffsetY = { -50 },
                        animationSpec = tween(600)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // App Logo/Icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(NeonTeal, NeonPurple)
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            text = "QR Scanner & Generator",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Scan & Create QR Codes Instantly",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Main Action Cards
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                        initialOffsetY = { 100 },
                        animationSpec = tween(600, delayMillis = 200)
                    )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        // Scan QR Card - Large
                        GlowingFeatureCard(
                            icon = Icons.Default.QrCodeScanner,
                            title = "Scan QR Code",
                            description = "Scan any QR code or barcode instantly with your camera",
                            gradientColors = listOf(NeonTeal, NeonBlue),
                            onClick = onNavigateToScanner,
                            isLarge = true
                        )
                        
                        // Create QR Card - Large
                        GlowingFeatureCard(
                            icon = Icons.Default.QrCode,
                            title = "Create QR Code",
                            description = "Generate QR codes for URLs, WiFi, contacts & more",
                            gradientColors = listOf(NeonPurple, NeonCoral),
                            onClick = onNavigateToGenerator,
                            isLarge = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Features Row
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600, delayMillis = 400)) + slideInVertically(
                        initialOffsetY = { 50 },
                        animationSpec = tween(600, delayMillis = 400)
                    )
                ) {
                    Column {
                        Text(
                            text = "Why Choose Us?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FeatureBadge(
                                icon = Icons.Outlined.WifiOff,
                                text = "Offline",
                                color = NeonGreen
                            )
                            FeatureBadge(
                                icon = Icons.Outlined.Bolt,
                                text = "Fast",
                                color = NeonYellow
                            )
                            FeatureBadge(
                                icon = Icons.Outlined.Security,
                                text = "Secure",
                                color = NeonTeal
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AnimatedBackground(isDark: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )
    
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .blur(100.dp)
    ) {
        // Teal blob
        drawCircle(
            color = if (isDark) NeonTeal.copy(alpha = 0.15f) else NeonTeal.copy(alpha = 0.1f),
            radius = 300f,
            center = Offset(size.width * 0.2f + offset1, size.height * 0.3f)
        )
        
        // Purple blob
        drawCircle(
            color = if (isDark) NeonPurple.copy(alpha = 0.15f) else NeonPurple.copy(alpha = 0.1f),
            radius = 250f,
            center = Offset(size.width * 0.8f - offset2, size.height * 0.2f + offset1)
        )
        
        // Coral blob
        drawCircle(
            color = if (isDark) NeonCoral.copy(alpha = 0.1f) else NeonCoral.copy(alpha = 0.08f),
            radius = 200f,
            center = Offset(size.width * 0.7f + offset2, size.height * 0.7f - offset1)
        )
    }
}

@Composable
private fun GlowingFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    isLarge: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val accentColor = gradientColors[0]
    
    // Card without gradient
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLarge) 140.dp else 100.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with solid color background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = accentColor,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            // Arrow indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = accentColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "→",
                    fontSize = 20.sp,
                    color = accentColor
                )
            }
        }
    }
}

@Composable
private fun FeatureBadge(
    icon: ImageVector,
    text: String,
    color: Color
) {
    val isDark = isSystemInDarkTheme()
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = color.copy(alpha = if (isDark) 0.15f else 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(showBackground = true, name = "Home Screen Light")
@Composable
private fun HomeScreenPreviewLight() {
    aki.pawar.qr.ui.theme.QrAppTheme(darkTheme = false) {
        HomeScreen(
            onNavigateToScanner = {},
            onNavigateToGenerator = {},
            onNavigateToHistory = {}
        )
    }
}

@Preview(showBackground = true, name = "Home Screen Dark")
@Composable
private fun HomeScreenPreviewDark() {
    aki.pawar.qr.ui.theme.QrAppTheme(darkTheme = true) {
        HomeScreen(
            onNavigateToScanner = {},
            onNavigateToGenerator = {},
            onNavigateToHistory = {}
        )
    }
}

@Preview(showBackground = true, name = "Feature Card")
@Composable
private fun FeatureCardPreview() {
    aki.pawar.qr.ui.theme.QrAppTheme {
        GlowingFeatureCard(
            icon = Icons.Default.QrCodeScanner,
            title = "Scan QR Code",
            description = "Scan any QR code or barcode instantly",
            gradientColors = listOf(NeonTeal, NeonBlue),
            onClick = {},
            isLarge = true
        )
    }
}
