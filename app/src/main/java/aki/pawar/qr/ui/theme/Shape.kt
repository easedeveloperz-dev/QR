package aki.pawar.qr.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Modern rounded shapes for a polished look
val Shapes = Shapes(
    // Extra small - chips, small badges
    extraSmall = RoundedCornerShape(8.dp),
    
    // Small - text fields, small buttons
    small = RoundedCornerShape(12.dp),
    
    // Medium - cards, dialogs
    medium = RoundedCornerShape(16.dp),
    
    // Large - bottom sheets, large cards
    large = RoundedCornerShape(24.dp),
    
    // Extra large - full screen dialogs
    extraLarge = RoundedCornerShape(32.dp)
)

// Additional custom shapes
val BottomSheetShape = RoundedCornerShape(
    topStart = 28.dp,
    topEnd = 28.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

val TopSheetShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = 28.dp,
    bottomEnd = 28.dp
)

val QrCodeShape = RoundedCornerShape(16.dp)

val ButtonShape = RoundedCornerShape(14.dp)

val FabShape = RoundedCornerShape(18.dp)

val CardShape = RoundedCornerShape(20.dp)

val ChipShape = RoundedCornerShape(10.dp)








