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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import aki.pawar.qr.domain.model.ModuleShape

/**
 * Component for selecting QR code module shapes
 */
@Composable
fun ShapeSelector(
    selectedShape: ModuleShape,
    onShapeSelected: (ModuleShape) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Module Shape",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModuleShape.entries.forEach { shape ->
                ShapeOption(
                    shape = shape,
                    isSelected = shape == selectedShape,
                    onClick = { onShapeSelected(shape) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ShapeOption(
    shape: ModuleShape,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Shape preview
        ShapePreview(
            shape = shape,
            isSelected = isSelected
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = shape.displayName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun ShapePreview(
    shape: ModuleShape,
    isSelected: Boolean
) {
    val color = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    // Draw a 3x3 grid preview of the shape
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { col ->
                    // Create a simple pattern - skip center for logo preview simulation
                    val showModule = !(row == 1 && col == 1)
                    
                    if (showModule) {
                        when (shape) {
                            ModuleShape.SQUARE -> {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(color, RoundedCornerShape(0.dp))
                                )
                            }
                            ModuleShape.CIRCLE -> {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(color, CircleShape)
                                )
                            }
                            ModuleShape.ROUNDED -> {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(color, RoundedCornerShape(3.dp))
                                )
                            }
                        }
                    } else {
                        // Empty space for center
                        Box(modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}
