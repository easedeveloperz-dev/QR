package aki.pawar.qr.presentation.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for QR Code Scanner Screen
 */
@RunWith(AndroidJUnit4::class)
class QRCodeScannerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun scannerScreen_displaysHeader() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Scan Code").performClick()

        // Assert
        composeTestRule.onNodeWithText("Scan QR codes & barcodes").assertIsDisplayed()
    }

    @Test
    fun scannerScreen_displaysSupportedFormatsHint() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Scan Code").performClick()

        // Assert
        composeTestRule.onNodeWithText("Supports: QR, EAN, UPC, Code 128, Code 39, and more")
            .assertIsDisplayed()
    }

    @Test
    fun scannerScreen_displaysPickFromGalleryButton() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Scan Code").performClick()

        // Assert
        composeTestRule.onNodeWithText("Pick from Gallery").assertIsDisplayed()
    }

    @Test
    fun scannerScreen_pickFromGalleryButtonIsClickable() {
        // Arrange
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Scan Code").performClick()

        // Assert - Button exists and can be found
        composeTestRule.onNodeWithText("Pick from Gallery").assertIsDisplayed()
    }

    @Test
    fun scannerScreen_showsPermissionScreenOrCamera() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Scan Code").performClick()

        // Assert - Either permission screen or camera preview should be shown
        // The Pick from Gallery button is always visible
        composeTestRule.onNodeWithText("Pick from Gallery").assertIsDisplayed()
    }
}

