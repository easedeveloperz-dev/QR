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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aki.pawar.qr.domain.model.FrameStyle

/**
 * Component for selecting QR code frame styles
 */
@Composable
fun FrameSelector(
    selectedFrame: FrameStyle,
    onFrameSelected: (FrameStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Frame Style",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FrameStyle.entries.forEach { frame ->
                FrameOption(
                    frame = frame,
                    isSelected = frame == selectedFrame,
                    onClick = { onFrameSelected(frame) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FrameOption(
    frame: FrameStyle,
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
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Frame preview
        FramePreview(
            frame = frame,
            isSelected = isSelected
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = frame.displayName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FramePreview(
    frame: FrameStyle,
    isSelected: Boolean
) {
    val primaryColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    Box(
        modifier = Modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        when (frame) {
            FrameStyle.NONE -> {
                // Just a simple QR-like pattern
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(primaryColor, RoundedCornerShape(4.dp))
                ) {
                    // Inner white box to simulate QR
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                            .background(surfaceColor, RoundedCornerShape(2.dp))
                    )
                }
            }
            
            FrameStyle.SIMPLE_BORDER -> {
                // QR with border
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .border(2.dp, primaryColor, RoundedCornerShape(6.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                            .background(primaryColor, RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Center)
                                .background(surfaceColor, RoundedCornerShape(1.dp))
                        )
                    }
                }
            }
            
            FrameStyle.SCAN_ME -> {
                // QR with "SCAN ME" text frame
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SCAN",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 6.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.5.dp, primaryColor, RoundedCornerShape(4.dp))
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.Center)
                                .background(primaryColor, RoundedCornerShape(1.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(Alignment.Center)
                                    .background(surfaceColor, RoundedCornerShape(0.5.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}
