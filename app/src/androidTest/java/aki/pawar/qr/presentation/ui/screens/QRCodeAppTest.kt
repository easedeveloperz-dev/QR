package aki.pawar.qr.presentation.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for QRCodeApp navigation and home screen
 */
@RunWith(AndroidJUnit4::class)
class QRCodeAppTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysAppTitle() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }

        // Assert
        composeTestRule.onNodeWithText("QR Code Pro").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysSubtitle() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }

        // Assert
        composeTestRule.onNodeWithText("Generate & Scan QR Codes").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysGenerateButton() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }

        // Assert
        composeTestRule.onNodeWithText("Generate QR Code").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysScanButton() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }

        // Assert
        composeTestRule.onNodeWithText("Scan Code").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysFeatureDescriptions() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }

        // Assert
        composeTestRule.onNodeWithText("Create QR codes from text or URLs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scan QR codes & barcodes").assertIsDisplayed()
    }

    @Test
    fun clickingGenerateButton_navigatesToGeneratorScreen() {
        // Arrange
        composeTestRule.setContent {
            QRCodeApp()
        }

        // Act
        composeTestRule.onNodeWithText("Generate QR Code").performClick()

        // Assert - Generator screen should show input field hint
        composeTestRule.onNodeWithText("Enter text or URL to create your QR code").assertIsDisplayed()
    }

    @Test
    fun clickingScanButton_navigatesToScannerScreen() {
        // Arrange
        composeTestRule.setContent {
            QRCodeApp()
        }

        // Act
        composeTestRule.onNodeWithText("Scan Code").performClick()

        // Assert - Scanner screen should show scan hint
        composeTestRule.onNodeWithText("Scan QR codes & barcodes").assertIsDisplayed()
    }

    @Test
    fun generatorScreen_backButtonReturnsToHome() {
        // Arrange
        composeTestRule.setContent {
            QRCodeApp()
        }

        // Act - Navigate to generator
        composeTestRule.onNodeWithText("Generate QR Code").performClick()
        
        // Click back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Assert - Should be back at home
        composeTestRule.onNodeWithText("QR Code Pro").assertIsDisplayed()
    }

    @Test
    fun scannerScreen_backButtonReturnsToHome() {
        // Arrange
        composeTestRule.setContent {
            QRCodeApp()
        }

        // Act - Navigate to scanner
        composeTestRule.onNodeWithText("Scan Code").performClick()
        
        // Click back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Assert - Should be back at home
        composeTestRule.onNodeWithText("QR Code Pro").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysFooterText() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }

        // Assert
        composeTestRule.onNodeWithText("Share generated QR codes on social media").assertIsDisplayed()
    }
}

