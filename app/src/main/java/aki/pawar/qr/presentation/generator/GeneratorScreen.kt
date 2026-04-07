package aki.pawar.qr.presentation.generator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aki.pawar.qr.domain.model.QrTypeOption
import aki.pawar.qr.domain.model.SocialPlatform
import androidx.compose.ui.tooling.preview.Preview
import aki.pawar.qr.domain.model.WifiSecurity
import aki.pawar.qr.presentation.components.ColorPickerSheet
import aki.pawar.qr.presentation.components.FrameSelector
import aki.pawar.qr.presentation.components.LogoPicker
import aki.pawar.qr.presentation.components.ShapeSelector
import androidx.compose.material.icons.filled.QrCode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    viewModel: GeneratorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(GeneratorEvent.ClearError)
        }
    }
    
    LaunchedEffect(state.showSuccess) {
        if (state.showSuccess) {
            snackbarHostState.showSnackbar(state.successMessage)
            viewModel.onEvent(GeneratorEvent.DismissSuccess)
        }
    }
    
    // Color picker sheet
    if (state.showColorPicker) {
        ColorPickerSheet(
            currentColor = if (state.isPickingForeground) {
                state.customization.foregroundColor
            } else {
                state.customization.backgroundColor
            },
            isForeground = state.isPickingForeground,
            onColorSelected = { color ->
                if (state.isPickingForeground) {
                    viewModel.onEvent(GeneratorEvent.UpdateForegroundColor(color))
                } else {
                    viewModel.onEvent(GeneratorEvent.UpdateBackgroundColor(color))
                }
            },
            onDismiss = { viewModel.onEvent(GeneratorEvent.HideColorPicker) }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when (state.currentStep) {
                            GeneratorStep.TYPE_SELECTION -> "Create QR Code"
                            GeneratorStep.FORM_INPUT -> state.selectedType?.displayName ?: "Create QR Code"
                            GeneratorStep.PREVIEW -> if (state.showCustomizationPanel) "Customize QR Code" else "Generated QR Code"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when (state.currentStep) {
                            GeneratorStep.TYPE_SELECTION -> onNavigateBack()
                            GeneratorStep.FORM_INPUT -> viewModel.onEvent(GeneratorEvent.ClearType)
                            GeneratorStep.PREVIEW -> {
                                if (state.showCustomizationPanel) {
                                    viewModel.onEvent(GeneratorEvent.ToggleCustomization)
                                } else {
                                    viewModel.onEvent(GeneratorEvent.BackToForm)
                                }
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        AnimatedContent(
            targetState = state.currentStep,
            label = "generator_content"
        ) { currentStep ->
            when {
                state.isGenerating -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Generating QR Code...")
                        }
                    }
                }
                currentStep == GeneratorStep.TYPE_SELECTION -> {
                    TypeSelectionScreen(
                        onSelectType = { viewModel.onEvent(GeneratorEvent.SelectType(it)) },
                        modifier = Modifier.padding(padding)
                    )
                }
                currentStep == GeneratorStep.FORM_INPUT -> {
                    QrFormScreen(
                        state = state,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier.padding(padding)
                    )
                }
                currentStep == GeneratorStep.PREVIEW -> {
                    QrPreviewScreen(
                        state = state,
                        onEvent = viewModel::onEvent,
                        onSave = { viewModel.onEvent(GeneratorEvent.Save) },
                        onShare = { viewModel.onEvent(GeneratorEvent.Share) },
                        onCreateNew = { viewModel.onEvent(GeneratorEvent.ClearType) },
                        onGoHome = onNavigateBack,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorOptionCard(
    label: String,
    color: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(color))
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        CircleShape
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format("#%06X", 0xFFFFFF and color),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TypeSelectionScreen(
    onSelectType: (QrTypeOption) -> Unit,
    modifier: Modifier = Modifier
) {
    // Main options displayed prominently
    val mainOptions = listOf(QrTypeOption.TEXT, QrTypeOption.URL)
    // Other options displayed under "More"
    val moreOptions = QrTypeOption.entries.filter { it !in mainOptions }
    
    var showMoreOptions by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Select QR Type",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choose what you want to encode",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Main options: Plain Text and URL - displayed prominently
        Text(
            text = "Quick Create",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Main options in a row - same card style as More Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            mainOptions.forEach { type ->
                Box(modifier = Modifier.weight(1f)) {
                    TypeCard(
                        type = type,
                        onClick = { onSelectType(type) },
                        isHighlighted = true
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // More options section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "More Options",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            IconButton(
                onClick = { showMoreOptions = !showMoreOptions }
            ) {
                Icon(
                    imageVector = if (showMoreOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showMoreOptions) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Animated more options grid
        AnimatedVisibility(
            visible = showMoreOptions,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Display more options in a grid (2 columns)
                val chunkedOptions = moreOptions.chunked(2)
                chunkedOptions.forEach { rowOptions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowOptions.forEach { type ->
                            Box(modifier = Modifier.weight(1f)) {
                                TypeCard(
                                    type = type,
                                    onClick = { onSelectType(type) },
                                    isHighlighted = false
                                )
                            }
                        }
                        // Add empty box if odd number of items
                        if (rowOptions.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TypeCard(
    type: QrTypeOption,
    onClick: () -> Unit,
    isHighlighted: Boolean = false
) {
    val gradientColors = getGradientForType(type)
    
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighlighted) 4.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp) // Fixed height for uniform size
            .border(
                width = if (isHighlighted) 2.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = if (isHighlighted) gradientColors else gradientColors.map { it.copy(alpha = 0.3f) }
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Gradient icon background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(gradientColors),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForType(type),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = type.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = type.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 16.sp
            )
        }
    }
}

private fun getGradientForType(type: QrTypeOption): List<Color> {
    return when (type) {
        QrTypeOption.URL -> listOf(Color(0xFF4ECDC4), Color(0xFF2BA39B))
        QrTypeOption.SMS -> listOf(Color(0xFF60A5FA), Color(0xFF3B82F6))
        QrTypeOption.EMAIL -> listOf(Color(0xFFF472B6), Color(0xFFEC4899))
        QrTypeOption.LOCATION -> listOf(Color(0xFF34D399), Color(0xFF10B981))
        QrTypeOption.SOCIAL_MEDIA -> listOf(Color(0xFFA78BFA), Color(0xFF8B5CF6))
        QrTypeOption.APP_DOWNLOAD -> listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
        QrTypeOption.UPI -> listOf(Color(0xFFFF6B6B), Color(0xFFEF4444))
        QrTypeOption.TEXT -> listOf(Color(0xFF6B7280), Color(0xFF4B5563))
    }
}

private fun getIconForType(type: QrTypeOption): ImageVector {
    return when (type) {
        QrTypeOption.URL -> Icons.Default.Link
        QrTypeOption.SMS -> Icons.Default.Sms
        QrTypeOption.EMAIL -> Icons.Default.Email
        QrTypeOption.LOCATION -> Icons.Default.LocationOn
        QrTypeOption.SOCIAL_MEDIA -> Icons.Default.Share
        QrTypeOption.APP_DOWNLOAD -> Icons.Default.Download
        QrTypeOption.UPI -> Icons.Default.Payment
        QrTypeOption.TEXT -> Icons.Default.TextFields
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QrFormScreen(
    state: GeneratorState,
    onEvent: (GeneratorEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedType = state.selectedType ?: return
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        when (selectedType) {
            QrTypeOption.URL -> UrlForm(state, onEvent)
            QrTypeOption.SMS -> SmsForm(state, onEvent)
            QrTypeOption.EMAIL -> EmailForm(state, onEvent)
            QrTypeOption.LOCATION -> LocationForm(state, onEvent)
            QrTypeOption.SOCIAL_MEDIA -> SocialMediaForm(state, onEvent)
            QrTypeOption.APP_DOWNLOAD -> AppDownloadForm(state, onEvent)
            QrTypeOption.UPI -> UpiForm(state, onEvent)
            QrTypeOption.TEXT -> TextForm(state, onEvent)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { onEvent(GeneratorEvent.Generate) },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid(selectedType, state)
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate QR Code")
        }
    }
}

private fun isFormValid(type: QrTypeOption, state: GeneratorState): Boolean {
    return when (type) {
        QrTypeOption.URL -> state.urlInput.isNotBlank()
        QrTypeOption.SMS -> state.smsNumber.isNotBlank()
        QrTypeOption.EMAIL -> state.emailAddress.isNotBlank()
        QrTypeOption.LOCATION -> state.locationLatitude.isNotBlank() && state.locationLongitude.isNotBlank()
        QrTypeOption.SOCIAL_MEDIA -> state.socialUsername.isNotBlank()
        QrTypeOption.APP_DOWNLOAD -> state.appPackageName.isNotBlank()
        QrTypeOption.UPI -> state.upiId.isNotBlank() && state.upiPayeeName.isNotBlank()
        QrTypeOption.TEXT -> state.plainText.isNotBlank()
    }
}

// Form composables for each type
@Composable
private fun UrlForm(state: GeneratorState, onEvent: (GeneratorEvent) -> Unit) {
    OutlinedTextField(
        value = state.urlInput,
        onValueChange = { onEvent(GeneratorEvent.UpdateUrl(it)) },
        label = { Text("Website URL") },
        placeholder = { Text("https://example.com") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
    )
}

@Composable
private fun SmsForm(state: GeneratorState, onEvent: (GeneratorEvent) -> Unit) {
    OutlinedTextField(
        value = state.smsNumber,
        onValueChange = { onEvent(GeneratorEvent.UpdateSmsNumber(it)) },
        label = { Text("Phone Number *") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    OutlinedTextField(
        value = state.smsMessage,
        onValueChange = { onEvent(GeneratorEvent.UpdateSmsMessage(it)) },
        label = { Text("Message (Optional)") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 4
    )
}

@Composable
private fun EmailForm(state: GeneratorState, onEvent: (GeneratorEvent) -> Unit) {
    OutlinedTextField(
        value = state.emailAddress,
        onValueChange = { onEvent(GeneratorEvent.UpdateEmailAddress(it)) },
        label = { Text("Email Address *") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    OutlinedTextField(
        value = state.emailSubject,
        onValueChange = { onEvent(GeneratorEvent.UpdateEmailSubject(it)) },
        label = { Text("Subject (Optional)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    OutlinedTextField(
        value = state.emailBody,
        onValueChange = { onEvent(GeneratorEvent.UpdateEmailBody(it)) },
        label = { Text("Body (Optional)") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 4
    )
}

@Composable
private fun LocationForm(state: GeneratorState, onEvent: (GeneratorEvent) -> Unit) {
    OutlinedTextField(
        value = state.locationLatitude,
        onValueChange = { onEvent(GeneratorEvent.UpdateLocationLatitude(it)) },
        label = { Text("Latitude *") },
        placeholder = { Text("28.6139") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    OutlinedTextField(
        value = state.locationLongitude,
        onValueChange = { onEvent(GeneratorEvent.UpdateLocationLongitude(it)) },
        label = { Text("Longitude *") },
        placeholder = { Text("77.2090") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    OutlinedTextField(
        value = state.locationLabel,
        onValueChange = { onEvent(GeneratorEvent.UpdateLocationLabel(it)) },
        label = { Text("Location Name (Optional)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SocialMediaForm(state: GeneratorState, onEvent: (GeneratorEvent) -> Unit) {
    var platformExpanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = platformExpanded,
        onExpandedChange = { platformExpanded = it }
    ) {
        OutlinedTextField(
            value = state.socialPlatform.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Platform") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = platformExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        
        ExposedDropdownMenu(
            expanded = platformExpanded,
            onDismissRequest = { platformExpanded = false }
        ) {
            SocialPlatform.entries.forEach { platform ->
                DropdownMenuItem(
                    text = { Text(platform.displayName) },
                    onClick = {
                        onEvent(GeneratorEvent.UpdateSocialPlatform(platform))
                        platformExpanded = false
                    }
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    OutlinedTextField(
        value = state.socialUsername,
        onValueChange = { onEvent(GeneratorEvent.UpdateSocialUsername(it)) },
        label = { Text("Username *") },
        placeholder = { Text("yourprofile") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun AppDownloadForm(state: GeneratorState, onEvent: (GeneratorEvent) -> Unit) {
    OutlinedTextField(
        value = state.appPackageName,
        onValueChange = { onEvent(GeneratorEvent.UpdateAppPackageName(it)) },
        label = { Text("Package Name *") },
        placeholder = { Text("com.example.app") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Text(
        text = "Enter the app's Play Store package name",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun UpiForm(state: GeneratorState, onEvent: (GeneratorEvent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "This only generates a payment QR. Actual payment processing happens in UPI apps like GPay, PhonePe, etc.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp),
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    OutlinedTextField(
        value = state.upiId,
        onValueChange = { onEvent(GeneratorEvent.UpdateUpiId(it)) },
        label = { Text("UPI ID *") },
        placeholder = { Text("name@upi") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    OutlinedTextField(
        value = state.upiPayeeName,
        onValueChange = { onEvent(GeneratorEvent.UpdateUpiPayeeName(it)) },
        label = { Text("Payee Name *") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    OutlinedTextField(
        value = state.upiAmount,
        onValueChange = { onEvent(GeneratorEvent.UpdateUpiAmount(it)) },
        label = { Text("Amount (Optional)") },
        placeholder = { Text("100") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        prefix = { Text("₹ ") }
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    OutlinedTextField(
        value = state.upiNote,
        onValueChange = { onEvent(GeneratorEvent.UpdateUpiNote(it)) },
        label = { Text("Transaction Note (Optional)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun TextForm(state: GeneratorState, onEvent: (GeneratorEvent) -> Unit) {
    OutlinedTextField(
        value = state.plainText,
        onValueChange = { onEvent(GeneratorEvent.UpdatePlainText(it)) },
        label = { Text("Text Content *") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        maxLines = 10
    )
}

@Composable
private fun QrPreviewScreen(
    state: GeneratorState,
    onEvent: (GeneratorEvent) -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onCreateNew: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = state.selectedType?.let { getGradientForType(it) } 
        ?: listOf(Color(0xFF4ECDC4), Color(0xFF2BA39B))
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show customization panel or QR preview
        if (state.showCustomizationPanel) {
            // Customization Panel
            CustomizationPanel(
                state = state,
                onEvent = onEvent
            )
        } else {
            // Success badge
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF00E676).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "QR Code Generated!",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF00E676),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // QR Code display with gradient border
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .background(
                        brush = Brush.linearGradient(gradientColors),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color(state.customization.backgroundColor),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isApplyingCustomization) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    } else {
                        state.generatedBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Generated QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Type badge
            if (state.selectedType != null) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                gradientColors.map { it.copy(alpha = 0.15f) }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = state.selectedType.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = gradientColors[0],
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Text(
                text = state.displayLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Customize button
            OutlinedButton(
                onClick = { onEvent(GeneratorEvent.ToggleCustomization) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    Brush.linearGradient(gradientColors)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = gradientColors[0],
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Customize QR Code",
                    fontWeight = FontWeight.SemiBold,
                    color = gradientColors[0]
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons with gradient
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Save button
                Button(
                    onClick = onSave,
                    enabled = !state.isSaving && !state.isApplyingCustomization,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Download, null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save", fontWeight = FontWeight.SemiBold)
                    }
                }
                
                // Share button
                Button(
                    onClick = onShare,
                    enabled = !state.isSharing && !state.isApplyingCustomization,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (state.isSharing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Share, null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onCreateNew,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    "Create Another QR Code",
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Go to Home button - prominent gradient design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667eea),
                                Color(0xFF764ba2)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onGoHome),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Go to Home",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CustomizationPanel(
    state: GeneratorState,
    onEvent: (GeneratorEvent) -> Unit
) {
    // QR Preview with customization
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(16.dp)
                )
                .background(Color(state.customization.backgroundColor))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isApplyingCustomization) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                state.generatedBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Preview",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
    
    // Contrast warning
    AnimatedVisibility(visible = state.showContrastWarning) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Low contrast may affect scannability",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
    
    // Colors section
    Text(
        text = "Colors",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Foreground color
        ColorOptionCard(
            label = "QR Color",
            color = state.customization.foregroundColor,
            onClick = { onEvent(GeneratorEvent.ShowForegroundColorPicker) },
            modifier = Modifier.weight(1f)
        )
        
        // Background color
        ColorOptionCard(
            label = "Background",
            color = state.customization.backgroundColor,
            onClick = { onEvent(GeneratorEvent.ShowBackgroundColorPicker) },
            modifier = Modifier.weight(1f)
        )
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Module shape selector
    ShapeSelector(
        selectedShape = state.customization.moduleShape,
        onShapeSelected = { onEvent(GeneratorEvent.UpdateModuleShape(it)) }
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Frame selector
    FrameSelector(
        selectedFrame = state.customization.frameStyle,
        onFrameSelected = { onEvent(GeneratorEvent.UpdateFrameStyle(it)) }
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Logo picker
    LogoPicker(
        currentLogo = state.customization.logoBitmap,
        logoSizePercent = state.customization.logoSizePercent,
        onLogoSelected = { onEvent(GeneratorEvent.UpdateLogo(it)) },
        onLogoSizeChanged = { onEvent(GeneratorEvent.UpdateLogoSize(it)) }
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Done button
    Button(
        onClick = { onEvent(GeneratorEvent.ToggleCustomization) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Done",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(showBackground = true, name = "Type Selection Light")
@Composable
private fun TypeSelectionPreviewLight() {
    aki.pawar.qr.ui.theme.QrAppTheme(darkTheme = false) {
        TypeSelectionScreen(
            onSelectType = {},
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true, name = "Type Selection Dark")
@Composable
private fun TypeSelectionPreviewDark() {
    aki.pawar.qr.ui.theme.QrAppTheme(darkTheme = true) {
        TypeSelectionScreen(
            onSelectType = {},
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true, name = "Type Card")
@Composable
private fun TypeCardPreview() {
    aki.pawar.qr.ui.theme.QrAppTheme {
        TypeCard(
            type = QrTypeOption.URL,
            onClick = {},
            isHighlighted = false
        )
    }
}
