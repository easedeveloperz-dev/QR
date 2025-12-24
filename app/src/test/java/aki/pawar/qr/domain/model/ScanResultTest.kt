package aki.pawar.qr.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for ScanResult model
 */
class ScanResultTest {

    @Test
    fun `isUrl returns true for http URL`() {
        // Arrange
        val result = ScanResult(content = "http://example.com")

        // Assert
        assertTrue(result.isUrl)
    }

    @Test
    fun `isUrl returns true for https URL`() {
        // Arrange
        val result = ScanResult(content = "https://example.com")

        // Assert
        assertTrue(result.isUrl)
    }

    @Test
    fun `isUrl returns false for plain text`() {
        // Arrange
        val result = ScanResult(content = "Hello World")

        // Assert
        assertFalse(result.isUrl)
    }

    @Test
    fun `isUrl returns false for text starting with www`() {
        // Arrange
        val result = ScanResult(content = "www.example.com")

        // Assert
        assertFalse(result.isUrl)
    }

    @Test
    fun `barcodeType defaults to UNKNOWN`() {
        // Arrange
        val result = ScanResult(content = "12345")

        // Assert
        assertEquals(BarcodeType.UNKNOWN, result.barcodeType)
    }

    @Test
    fun `barcodeType can be set to QR_CODE`() {
        // Arrange
        val result = ScanResult(content = "test", barcodeType = BarcodeType.QR_CODE)

        // Assert
        assertEquals(BarcodeType.QR_CODE, result.barcodeType)
    }

    @Test
    fun `barcodeType can be set to EAN_13`() {
        // Arrange
        val result = ScanResult(content = "1234567890123", barcodeType = BarcodeType.EAN_13)

        // Assert
        assertEquals(BarcodeType.EAN_13, result.barcodeType)
    }

    @Test
    fun `timestamp is set automatically`() {
        // Arrange
        val before = System.currentTimeMillis()
        val result = ScanResult(content = "test")
        val after = System.currentTimeMillis()

        // Assert
        assertTrue(result.timestamp >= before)
        assertTrue(result.timestamp <= after)
    }
}

