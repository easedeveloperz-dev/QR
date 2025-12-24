package aki.pawar.qr.domain.usecase

import aki.pawar.qr.domain.repository.QRCodeRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Unit tests for CopyToClipboardUseCase
 */
class CopyToClipboardUseCaseTest {

    @Mock
    private lateinit var repository: QRCodeRepository

    private lateinit var useCase: CopyToClipboardUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = CopyToClipboardUseCase(repository)
    }

    @Test
    fun `invoke calls repository copyToClipboard with correct text`() {
        // Arrange
        val text = "Test clipboard text"

        // Act
        useCase(text)

        // Assert
        verify(repository).copyToClipboard(text)
    }

    @Test
    fun `invoke with empty string still calls repository`() {
        // Arrange
        val text = ""

        // Act
        useCase(text)

        // Assert
        verify(repository).copyToClipboard(text)
    }

    @Test
    fun `invoke with URL text calls repository`() {
        // Arrange
        val text = "https://example.com"

        // Act
        useCase(text)

        // Assert
        verify(repository).copyToClipboard(text)
    }
}

