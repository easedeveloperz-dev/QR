package aki.pawar.qr.presentation.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for QRGeneratorState
 */
class QRGeneratorStateTest {

    @Test
    fun `default state has empty input text`() {
        // Arrange
        val state = QRGeneratorState()

        // Assert
        assertEquals("", state.inputText)
    }

    @Test
    fun `default state has null generated bitmap`() {
        // Arrange
        val state = QRGeneratorState()

        // Assert
        assertNull(state.generatedBitmap)
    }

    @Test
    fun `default state has isGenerating false`() {
        // Arrange
        val state = QRGeneratorState()

        // Assert
        assertFalse(state.isGenerating)
    }

    @Test
    fun `default state has isSaving false`() {
        // Arrange
        val state = QRGeneratorState()

        // Assert
        assertFalse(state.isSaving)
    }

    @Test
    fun `default state has isSharing false`() {
        // Arrange
        val state = QRGeneratorState()

        // Assert
        assertFalse(state.isSharing)
    }

    @Test
    fun `default state has showQRCode false`() {
        // Arrange
        val state = QRGeneratorState()

        // Assert
        assertFalse(state.showQRCode)
    }

    @Test
    fun `default state has null message`() {
        // Arrange
        val state = QRGeneratorState()

        // Assert
        assertNull(state.message)
    }

    @Test
    fun `state can be copied with new input text`() {
        // Arrange
        val state = QRGeneratorState()

        // Act
        val newState = state.copy(inputText = "hello")

        // Assert
        assertEquals("hello", newState.inputText)
    }
}

