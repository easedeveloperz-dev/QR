package aki.pawar.qr.presentation.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for QRScannerState
 */
class QRScannerStateTest {

    @Test
    fun `default state has isScanning true`() {
        // Arrange
        val state = QRScannerState()

        // Assert
        assertTrue(state.isScanning)
    }

    @Test
    fun `default state has null scanned result`() {
        // Arrange
        val state = QRScannerState()

        // Assert
        assertNull(state.scannedResult)
    }

    @Test
    fun `default state has default barcode type`() {
        // Arrange
        val state = QRScannerState()

        // Assert
        assertEquals("QR Code", state.barcodeType)
    }

    @Test
    fun `default state has isCopied false`() {
        // Arrange
        val state = QRScannerState()

        // Assert
        assertFalse(state.isCopied)
    }

    @Test
    fun `default state has isProcessingImage false`() {
        // Arrange
        val state = QRScannerState()

        // Assert
        assertFalse(state.isProcessingImage)
    }

    @Test
    fun `default state has null message`() {
        // Arrange
        val state = QRScannerState()

        // Assert
        assertNull(state.message)
    }

    @Test
    fun `state can be copied with scanned result`() {
        // Arrange
        val state = QRScannerState()

        // Act
        val newState = state.copy(scannedResult = "https://example.com")

        // Assert
        assertEquals("https://example.com", newState.scannedResult)
    }

    @Test
    fun `state can be copied with custom barcode type`() {
        // Arrange
        val state = QRScannerState()

        // Act
        val newState = state.copy(barcodeType = "EAN-13")

        // Assert
        assertEquals("EAN-13", newState.barcodeType)
    }
}

