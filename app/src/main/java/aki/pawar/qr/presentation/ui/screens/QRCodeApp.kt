package aki.pawar.qr.presentation.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aki.pawar.qr.presentation.ui.theme.QRTheme

/**
 * Enum representing the different screens in the app
 */
enum class QRScreen {
    HOME,
    GENERATOR,
    SCANNER
}

/**
 * Main QR Code App composable
 * Handles navigation between screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeApp() {
    var currentScreen by rememberSaveable { mutableStateOf(QRScreen.HOME) }
    
    Scaffold(
        topBar = {
            if (currentScreen != QRScreen.HOME) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentScreen) {
                                QRScreen.GENERATOR -> "Generate QR Code"
                                QRScreen.SCANNER -> "Scan QR Code"
                                else -> ""
                            },
                            color = QRTheme.TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { currentScreen = QRScreen.HOME }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = QRTheme.TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = QRTheme.BackgroundLight
                    )
                )
            }
        },
        containerColor = QRTheme.BackgroundLight
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                if (targetState == QRScreen.HOME) {
                    (fadeIn() + slideInHorizontally { -it }) togetherWith
                    (fadeOut() + slideOutHorizontally { it })
                } else {
                    (fadeIn() + slideInHorizontally { it }) togetherWith
                    (fadeOut() + slideOutHorizontally { -it })
                }
            },
            label = "screen_transition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { screen ->
            when (screen) {
                QRScreen.HOME -> {
                    HomeScreen(
                        onGeneratorClick = { currentScreen = QRScreen.GENERATOR },
                        onScannerClick = { currentScreen = QRScreen.SCANNER }
                    )
                }
                QRScreen.GENERATOR -> {
                    QRCodeGeneratorScreen()
                }
                QRScreen.SCANNER -> {
                    QRCodeScannerScreen()
                }
            }
        }
    }
}

/**
 * Home screen with navigation buttons
 */
@Composable
private fun HomeScreen(
    onGeneratorClick: () -> Unit,
    onScannerClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        QRTheme.BackgroundLight,
                        QRTheme.BackgroundLightSecondary
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            QRTheme.AccentTealLight,
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = QRTheme.AccentTeal,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            QRTheme.AccentOrangeLight,
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = QRTheme.AccentOrange,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "QR Code Pro",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = QRTheme.TextPrimary
            )
            
            Text(
                text = "Generate & Scan QR Codes",
                fontSize = 16.sp,
                color = QRTheme.TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(56.dp))
            
            // Generator button
            FeatureButton(
                title = "Generate QR Code",
                description = "Create QR codes from text or URLs",
                icon = Icons.Default.QrCode2,
                accentColor = QRTheme.AccentTeal,
                backgroundColor = QRTheme.AccentTealLight,
                onClick = onGeneratorClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scanner button
            FeatureButton(
                title = "Scan Code",
                description = "Scan QR codes & barcodes",
                icon = Icons.Default.QrCodeScanner,
                accentColor = QRTheme.AccentOrange,
                backgroundColor = QRTheme.AccentOrangeLight,
                onClick = onScannerClick
            )
            
            Spacer(modifier = Modifier.height(56.dp))
            
            Text(
                text = "Share generated QR codes on social media",
                fontSize = 13.sp,
                color = QRTheme.TextLight,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Reusable feature button component
 */
@Composable
private fun FeatureButton(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = accentColor.copy(alpha = 0.1f),
                spotColor = accentColor.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = QRTheme.CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = QRTheme.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = QRTheme.TextSecondary
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

