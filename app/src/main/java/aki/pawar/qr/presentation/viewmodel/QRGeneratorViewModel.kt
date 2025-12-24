package aki.pawar.qr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aki.pawar.qr.domain.usecase.GenerateQRCodeUseCase
import aki.pawar.qr.domain.usecase.SaveQRCodeUseCase
import aki.pawar.qr.domain.usecase.ShareQRCodeUseCase
import aki.pawar.qr.presentation.state.Message
import aki.pawar.qr.presentation.state.MessageType
import aki.pawar.qr.presentation.state.QRGeneratorEvent
import aki.pawar.qr.presentation.state.QRGeneratorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for QR Code Generator screen
 * Handles business logic and state management
 */
class QRGeneratorViewModel(
    private val generateQRCodeUseCase: GenerateQRCodeUseCase,
    private val saveQRCodeUseCase: SaveQRCodeUseCase,
    private val shareQRCodeUseCase: ShareQRCodeUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(QRGeneratorState())
    val state: StateFlow<QRGeneratorState> = _state.asStateFlow()
    
    /**
     * Handles UI events from the screen
     */
    fun onEvent(event: QRGeneratorEvent) {
        when (event) {
            is QRGeneratorEvent.OnTextChanged -> {
                _state.update { it.copy(inputText = event.text) }
            }
            
            is QRGeneratorEvent.OnGenerateClick -> {
                generateQRCode()
            }
            
            is QRGeneratorEvent.OnShareClick -> {
                shareQRCode()
            }
            
            is QRGeneratorEvent.OnSaveClick -> {
                saveQRCode()
            }
            
            is QRGeneratorEvent.OnMessageDismissed -> {
                _state.update { it.copy(message = null) }
            }
        }
    }
    
    private fun generateQRCode() {
        val text = _state.value.inputText
        if (text.isBlank()) return
        
        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true) }
            
            val result = generateQRCodeUseCase(text)
            
            _state.update { currentState ->
                if (result != null) {
                    currentState.copy(
                        isGenerating = false,
                        generatedBitmap = result.bitmap,
                        showQRCode = true
                    )
                } else {
                    currentState.copy(
                        isGenerating = false,
                        message = Message(
                            text = "Failed to generate QR code",
                            type = MessageType.ERROR
                        )
                    )
                }
            }
        }
    }
    
    private fun shareQRCode() {
        val bitmap = _state.value.generatedBitmap ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isSharing = true) }
            
            val success = shareQRCodeUseCase(bitmap)
            
            _state.update { currentState ->
                currentState.copy(
                    isSharing = false,
                    message = if (!success) {
                        Message(
                            text = "Failed to share QR code",
                            type = MessageType.ERROR
                        )
                    } else null
                )
            }
        }
    }
    
    private fun saveQRCode() {
        val bitmap = _state.value.generatedBitmap ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            
            val success = saveQRCodeUseCase(bitmap)
            
            _state.update { currentState ->
                currentState.copy(
                    isSaving = false,
                    message = Message(
                        text = if (success) "QR Code saved to gallery!" else "Failed to save QR code",
                        type = if (success) MessageType.SUCCESS else MessageType.ERROR
                    )
                )
            }
        }
    }
}

