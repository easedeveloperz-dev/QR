package aki.pawar.qr.presentation.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import aki.pawar.qr.MyApp
import aki.pawar.qr.di.QRGeneratorViewModelFactory
import aki.pawar.qr.presentation.state.QRGeneratorEvent
import aki.pawar.qr.presentation.ui.theme.QRTheme
import aki.pawar.qr.presentation.viewmodel.QRGeneratorViewModel

/**
 * QR Code Generator Screen
 * Allows users to generate QR codes from text input
 */
@Composable
fun QRCodeGeneratorScreen(
    viewModel: QRGeneratorViewModel = viewModel(
        factory = QRGeneratorViewModelFactory(
            (LocalContext.current.applicationContext as MyApp).container
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    // Handle messages (toasts)
    LaunchedEffect(state.message) {
        state.message?.let { message ->
            Toast.makeText(context, message.text, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(QRGeneratorEvent.OnMessageDismissed)
        }
    }
    
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle text
            Text(
                text = "Enter text or URL to create your QR code",
                style = MaterialTheme.typography.bodyMedium,
                color = QRTheme.TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Input field
            OutlinedTextField(
                value = state.inputText,
                onValueChange = { viewModel.onEvent(QRGeneratorEvent.OnTextChanged(it)) },
                label = { Text("Enter text or URL", color = QRTheme.TextSecondary) },
                placeholder = { 
                    Text(
                        "https://example.com", 
                        color = QRTheme.TextLight
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QRTheme.AccentTeal,
                    unfocusedBorderColor = QRTheme.SoftBorder,
                    focusedTextColor = QRTheme.TextPrimary,
                    unfocusedTextColor = QRTheme.TextPrimary,
                    cursorColor = QRTheme.AccentTeal,
                    focusedContainerColor = QRTheme.CardBackground,
                    unfocusedContainerColor = QRTheme.CardBackground
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.onEvent(QRGeneratorEvent.OnGenerateClick)
                    }
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Generate button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onEvent(QRGeneratorEvent.OnGenerateClick)
                },
                enabled = state.inputText.isNotBlank() && !state.isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QRTheme.AccentTeal,
                    contentColor = Color.White,
                    disabledContainerColor = QRTheme.SoftBorder,
                    disabledContentColor = QRTheme.TextLight
                )
            ) {
                if (state.isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Generate QR Code",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Generated QR Code display
            AnimatedVisibility(
                visible = state.showQRCode && state.generatedBitmap != null,
                enter = fadeIn() + scaleIn()
            ) {
                QRCodeCard(
                    bitmap = state.generatedBitmap,
                    isSaving = state.isSaving,
                    isSharing = state.isSharing,
                    onShareClick = { viewModel.onEvent(QRGeneratorEvent.OnShareClick) },
                    onSaveClick = { viewModel.onEvent(QRGeneratorEvent.OnSaveClick) }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Card displaying the generated QR code with action buttons
 */
@Composable
private fun QRCodeCard(
    bitmap: android.graphics.Bitmap?,
    isSaving: Boolean,
    isSharing: Boolean,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = QRTheme.AccentTeal.copy(alpha = 0.1f),
                spotColor = QRTheme.AccentTeal.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = QRTheme.CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // QR Code with soft border
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(
                        width = 2.dp,
                        color = QRTheme.AccentTealLight,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Generated QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Share button
                FilledTonalButton(
                    onClick = onShareClick,
                    enabled = !isSharing,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = QRTheme.AccentTealLight,
                        contentColor = QRTheme.AccentTeal
                    )
                ) {
                    if (isSharing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = QRTheme.AccentTeal,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Share", fontWeight = FontWeight.SemiBold)
                    }
                }
                
                // Save button
                FilledTonalButton(
                    onClick = onSaveClick,
                    enabled = !isSaving,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = QRTheme.AccentTealLight,
                        contentColor = QRTheme.AccentTealDark
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = QRTheme.AccentTealDark,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Save", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

