package aki.pawar.qr.domain.usecase

import android.graphics.Bitmap
import aki.pawar.qr.domain.model.BarcodeType
import aki.pawar.qr.domain.model.ScanResult
import aki.pawar.qr.domain.repository.QRCodeRepository
import com.google.mlkit.vision.barcode.common.Barcode

/**
 * Use case for scanning QR codes and barcodes from images
 */
class ScanImageUseCase(
    private val repository: QRCodeRepository
) {
    /**
     * Scans a bitmap image for QR codes or barcodes
     * @param bitmap The image to scan
     * @return ScanResult if code found, null otherwise
     */
    suspend operator fun invoke(bitmap: Bitmap): ScanResult? {
        val result = repository.scanBarcodeFromImage(bitmap)
        return result?.let { (content, format) ->
            ScanResult(
                content = content,
                barcodeType = mapBarcodeFormat(format)
            )
        }
    }
    
    /**
     * Maps ML Kit barcode format to our BarcodeType enum
     */
    private fun mapBarcodeFormat(format: Int): BarcodeType {
        return when (format) {
            Barcode.FORMAT_QR_CODE -> BarcodeType.QR_CODE
            Barcode.FORMAT_CODE_128 -> BarcodeType.BARCODE_128
            Barcode.FORMAT_CODE_39 -> BarcodeType.BARCODE_39
            Barcode.FORMAT_CODE_93 -> BarcodeType.BARCODE_93
            Barcode.FORMAT_CODABAR -> BarcodeType.CODABAR
            Barcode.FORMAT_EAN_13 -> BarcodeType.EAN_13
            Barcode.FORMAT_EAN_8 -> BarcodeType.EAN_8
            Barcode.FORMAT_ITF -> BarcodeType.ITF
            Barcode.FORMAT_UPC_A -> BarcodeType.UPC_A
            Barcode.FORMAT_UPC_E -> BarcodeType.UPC_E
            Barcode.FORMAT_PDF417 -> BarcodeType.PDF_417
            Barcode.FORMAT_AZTEC -> BarcodeType.AZTEC
            Barcode.FORMAT_DATA_MATRIX -> BarcodeType.DATA_MATRIX
            else -> BarcodeType.UNKNOWN
        }
    }
}

