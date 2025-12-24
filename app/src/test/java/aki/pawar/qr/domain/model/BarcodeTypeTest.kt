package aki.pawar.qr.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for BarcodeType enum
 */
class BarcodeTypeTest {

    @Test
    fun `QR_CODE has correct display name`() {
        assertEquals("QR Code", BarcodeType.QR_CODE.displayName)
    }

    @Test
    fun `BARCODE_128 has correct display name`() {
        assertEquals("Code 128", BarcodeType.BARCODE_128.displayName)
    }

    @Test
    fun `BARCODE_39 has correct display name`() {
        assertEquals("Code 39", BarcodeType.BARCODE_39.displayName)
    }

    @Test
    fun `BARCODE_93 has correct display name`() {
        assertEquals("Code 93", BarcodeType.BARCODE_93.displayName)
    }

    @Test
    fun `EAN_13 has correct display name`() {
        assertEquals("EAN-13", BarcodeType.EAN_13.displayName)
    }

    @Test
    fun `EAN_8 has correct display name`() {
        assertEquals("EAN-8", BarcodeType.EAN_8.displayName)
    }

    @Test
    fun `UPC_A has correct display name`() {
        assertEquals("UPC-A", BarcodeType.UPC_A.displayName)
    }

    @Test
    fun `UPC_E has correct display name`() {
        assertEquals("UPC-E", BarcodeType.UPC_E.displayName)
    }

    @Test
    fun `PDF_417 has correct display name`() {
        assertEquals("PDF417", BarcodeType.PDF_417.displayName)
    }

    @Test
    fun `AZTEC has correct display name`() {
        assertEquals("Aztec", BarcodeType.AZTEC.displayName)
    }

    @Test
    fun `DATA_MATRIX has correct display name`() {
        assertEquals("Data Matrix", BarcodeType.DATA_MATRIX.displayName)
    }

    @Test
    fun `UNKNOWN has correct display name`() {
        assertEquals("Barcode", BarcodeType.UNKNOWN.displayName)
    }

    @Test
    fun `all enum values have non-empty display names`() {
        BarcodeType.entries.forEach { type ->
            assert(type.displayName.isNotEmpty()) { "${type.name} has empty display name" }
        }
    }
}

