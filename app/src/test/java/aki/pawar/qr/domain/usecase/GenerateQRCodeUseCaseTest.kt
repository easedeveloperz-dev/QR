package aki.pawar.qr.domain.usecase

import aki.pawar.qr.domain.repository.QRCodeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Unit tests for GenerateQRCodeUseCase
 */
class GenerateQRCodeUseCaseTest {

    @Mock
    private lateinit var repository: QRCodeRepository

    private lateinit var useCase: GenerateQRCodeUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GenerateQRCodeUseCase(repository)
    }

    @Test
    fun `invoke with blank text returns null`() = runTest {
        // Arrange
        val text = ""

        // Act
        val result = useCase(text)

        // Assert
        assertNull(result)
    }

    @Test
    fun `invoke with whitespace only returns null`() = runTest {
        // Arrange
        val text = "   "

        // Act
        val result = useCase(text)

        // Assert
        assertNull(result)
    }

    @Test
    fun `invoke with tabs and newlines returns null`() = runTest {
        // Arrange
        val text = "\t\n  \t"

        // Act
        val result = useCase(text)

        // Assert
        assertNull(result)
    }
}

