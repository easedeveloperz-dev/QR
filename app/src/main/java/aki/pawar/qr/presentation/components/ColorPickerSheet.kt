package aki.pawar.qr.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import aki.pawar.qr.domain.model.QrPresetColors
import kotlin.math.roundToInt

/**
 * Bottom sheet for selecting colors with preset options and custom RGB sliders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerSheet(
    currentColor: Int,
    isForeground: Boolean,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var selectedColor by remember { mutableIntStateOf(currentColor) }
    var showCustomPicker by remember { mutableFloatStateOf(0f) }
    
    // RGB values for custom color
    var redValue by remember { mutableFloatStateOf(android.graphics.Color.red(currentColor).toFloat()) }
    var greenValue by remember { mutableFloatStateOf(android.graphics.Color.green(currentColor).toFloat()) }
    var blueValue by remember { mutableFloatStateOf(android.graphics.Color.blue(currentColor).toFloat()) }
    
    val presetColors = if (isForeground) {
        QrPresetColors.foregroundColors
    } else {
        QrPresetColors.backgroundColors
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Title
            Text(
                text = if (isForeground) "QR Code Color" else "Background Color",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isForeground) 
                    "Choose the color for QR code modules" 
                else 
                    "Choose the background color",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Current color preview
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Selected:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(selectedColor))
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
                
                Spacer(modifier = Modifier.size(12.dp))
                
                Text(
                    text = String.format("#%06X", 0xFFFFFF and selectedColor),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Preset colors section
            Text(
                text = "Preset Colors",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(140.dp)
            ) {
                items(presetColors) { color ->
                    ColorCircle(
                        color = color,
                        isSelected = color == selectedColor,
                        onClick = {
                            selectedColor = color
                            redValue = android.graphics.Color.red(color).toFloat()
                            greenValue = android.graphics.Color.green(color).toFloat()
                            blueValue = android.graphics.Color.blue(color).toFloat()
                            onColorSelected(color)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Custom color section
            Text(
                text = "Custom Color",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Red slider
            ColorSlider(
                label = "R",
                value = redValue,
                color = Color.Red,
                onValueChange = {
                    redValue = it
                    selectedColor = android.graphics.Color.rgb(
                        redValue.roundToInt(),
                        greenValue.roundToInt(),
                        blueValue.roundToInt()
                    )
                    onColorSelected(selectedColor)
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Green slider
            ColorSlider(
                label = "G",
                value = greenValue,
                color = Color.Green,
                onValueChange = {
                    greenValue = it
                    selectedColor = android.graphics.Color.rgb(
                        redValue.roundToInt(),
                        greenValue.roundToInt(),
                        blueValue.roundToInt()
                    )
                    onColorSelected(selectedColor)
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Blue slider
            ColorSlider(
                label = "B",
                value = blueValue,
                color = Color.Blue,
                onValueChange = {
                    blueValue = it
                    selectedColor = android.graphics.Color.rgb(
                        redValue.roundToInt(),
                        greenValue.roundToInt(),
                        blueValue.roundToInt()
                    )
                    onColorSelected(selectedColor)
                }
            )
        }
    }
}

@Composable
private fun ColorCircle(
    color: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(color))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Show check icon with contrasting color
            val luminance = calculateLuminance(color)
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (luminance > 0.5f) Color.Black else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ColorSlider(
    label: String,
    value: Float,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.size(24.dp)
        )
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..255f,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.3f)
            )
        )
        
        Text(
            text = value.roundToInt().toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(36.dp)
        )
    }
}

private fun calculateLuminance(color: Int): Float {
    val r = android.graphics.Color.red(color) / 255f
    val g = android.graphics.Color.green(color) / 255f
    val b = android.graphics.Color.blue(color) / 255f
    return 0.299f * r + 0.587f * g + 0.114f * b
}
