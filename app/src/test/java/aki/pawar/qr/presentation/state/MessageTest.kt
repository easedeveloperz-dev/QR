package aki.pawar.qr.presentation.state

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for Message data class
 */
class MessageTest {

    @Test
    fun `message with default type is INFO`() {
        // Arrange
        val message = Message(text = "Test message")

        // Assert
        assertEquals(MessageType.INFO, message.type)
    }

    @Test
    fun `message can be created with SUCCESS type`() {
        // Arrange
        val message = Message(text = "Success!", type = MessageType.SUCCESS)

        // Assert
        assertEquals(MessageType.SUCCESS, message.type)
        assertEquals("Success!", message.text)
    }

    @Test
    fun `message can be created with ERROR type`() {
        // Arrange
        val message = Message(text = "Error occurred", type = MessageType.ERROR)

        // Assert
        assertEquals(MessageType.ERROR, message.type)
        assertEquals("Error occurred", message.text)
    }

    @Test
    fun `message text is stored correctly`() {
        // Arrange
        val text = "This is a test message"
        val message = Message(text = text)

        // Assert
        assertEquals(text, message.text)
    }

    @Test
    fun `empty message text is allowed`() {
        // Arrange
        val message = Message(text = "")

        // Assert
        assertEquals("", message.text)
    }
}

