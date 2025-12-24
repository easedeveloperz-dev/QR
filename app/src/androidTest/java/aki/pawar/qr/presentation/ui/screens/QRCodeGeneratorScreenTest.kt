package aki.pawar.qr.presentation.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for QR Code Generator Screen
 */
@RunWith(AndroidJUnit4::class)
class QRCodeGeneratorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun generatorScreen_displaysSubtitle() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Generate QR Code").performClick()

        // Assert
        composeTestRule.onNodeWithText("Enter text or URL to create your QR code").assertIsDisplayed()
    }

    @Test
    fun generatorScreen_displaysTextInputField() {
        // Arrange & Act
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Generate QR Code").performClick()

        // Assert
        composeTestRule.onNodeWithText("Enter text or URL").assertIsDisplayed()
    }

    @Test
    fun generatorScreen_canEnterTextInInputField() {
        // Arrange
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Generate QR Code").performClick()

        // Act
        composeTestRule.onNodeWithText("Enter text or URL").performTextInput("https://example.com")

        // Assert - Text should be entered
        composeTestRule.onNodeWithText("https://example.com").assertIsDisplayed()
    }

    @Test
    fun generatorScreen_generatesQRCodeOnButtonClick() {
        // Arrange
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Generate QR Code").performClick()

        // Act
        composeTestRule.onNodeWithText("Enter text or URL").performTextInput("https://test.com")
        composeTestRule.waitForIdle()
        
        // Find and click the generate button (second occurrence)
        composeTestRule.onAllNodesWithText("Generate QR Code")[1].performClick()

        // Wait for QR generation
        composeTestRule.waitForIdle()
        Thread.sleep(500) // Small delay for async operation

        // Assert - Share and Save buttons should appear
        composeTestRule.onNodeWithText("Share").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun generatorScreen_showsShareButtonAfterGeneration() {
        // Arrange
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Generate QR Code").performClick()

        // Act
        composeTestRule.onNodeWithText("Enter text or URL").performTextInput("hello world")
        composeTestRule.waitForIdle()
        
        composeTestRule.onAllNodesWithText("Generate QR Code")[1].performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        // Assert
        composeTestRule.onNodeWithText("Share").assertIsDisplayed()
    }

    @Test
    fun generatorScreen_showsSaveButtonAfterGeneration() {
        // Arrange
        composeTestRule.setContent {
            QRCodeApp()
        }
        composeTestRule.onNodeWithText("Generate QR Code").performClick()

        // Act
        composeTestRule.onNodeWithText("Enter text or URL").performTextInput("hello world")
        composeTestRule.waitForIdle()
        
        composeTestRule.onAllNodesWithText("Generate QR Code")[1].performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)

        // Assert
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }
}

