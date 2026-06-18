package aki.pawar.qr.presentation.generator

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aki.pawar.qr.data.repository.GeneratedQrRepository
import aki.pawar.qr.domain.model.FrameStyle
import aki.pawar.qr.domain.model.ModuleShape
import aki.pawar.qr.domain.model.QrCustomization
import aki.pawar.qr.domain.model.QrType
import aki.pawar.qr.domain.model.QrTypeOption
import aki.pawar.qr.domain.model.SocialPlatform
import aki.pawar.qr.domain.model.WifiSecurity
import aki.pawar.qr.util.BitmapUtils
import aki.pawar.qr.util.InAppReviewManager
import aki.pawar.qr.util.QrGenerator
import aki.pawar.qr.util.UpiPayeeAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Screen step in the generator flow
 */
enum class GeneratorStep {
    TYPE_SELECTION,
    FORM_INPUT,
    PREVIEW
}

/**
 * UI State for Generator Screen
 */
data class GeneratorState(
    val currentStep: GeneratorStep = GeneratorStep.TYPE_SELECTION,
    val selectedType: QrTypeOption? = null,
    val generatedBitmap: Bitmap? = null,
    val qrContent: String = "",
    val displayLabel: String = "",
    val isGenerating: Boolean = false,
    val isSaving: Boolean = false,
    val isSharing: Boolean = false,
    val showSuccess: Boolean = false,
    val successMessage: String = "",
    val error: String? = null,
    
    // Customization (on preview screen)
    val customization: QrCustomization = QrCustomization(),
    val showCustomizationPanel: Boolean = false,  // Toggle customization options on preview
    val showColorPicker: Boolean = false,
    val isPickingForeground: Boolean = true,  // true = foreground, false = background
    val showContrastWarning: Boolean = false,
    val isApplyingCustomization: Boolean = false,
    
    // Form fields for each type
    val urlInput: String = "",
    val wifiSsid: String = "",
    val wifiPassword: String = "",
    val wifiSecurity: WifiSecurity = WifiSecurity.WPA,
    val wifiHidden: Boolean = false,
    val contactFirstName: String = "",
    val contactLastName: String = "",
    val contactPhone: String = "",
    val contactEmail: String = "",
    val contactOrganization: String = "",
    val contactWebsite: String = "",
    val phoneNumber: String = "",
    val smsNumber: String = "",
    val smsMessage: String = "",
    val emailAddress: String = "",
    val emailSubject: String = "",
    val emailBody: String = "",
    val locationLatitude: String = "",
    val locationLongitude: String = "",
    val locationLabel: String = "",
    val socialPlatform: SocialPlatform = SocialPlatform.INSTAGRAM,
    val socialUsername: String = "",
    val appPackageName: String = "",
    val upiId: String = "",
    val upiMobileNumber: String = "",
    val upiInputMode: UpiPayeeAddress.InputMode = UpiPayeeAddress.InputMode.UPI_ID,
    val upiMobileSuffix: UpiPayeeAddress.MobileSuffix = UpiPayeeAddress.MobileSuffix.UPI,
    val upiPayeeName: String = "",
    val upiAmount: String = "",
    val upiNote: String = "",
    val plainText: String = ""
)

/**
 * Events for Generator Screen
 */
sealed class GeneratorEvent {
    data class SelectType(val type: QrTypeOption) : GeneratorEvent()
    data object ClearType : GeneratorEvent()
    data object Generate : GeneratorEvent()
    data object Save : GeneratorEvent()
    data object Share : GeneratorEvent()
    data object Reset : GeneratorEvent()
    data object ClearError : GeneratorEvent()
    data object DismissSuccess : GeneratorEvent()
    data object BackToForm : GeneratorEvent()
    
    // Customization events (applied on preview screen)
    data class UpdateForegroundColor(val color: Int) : GeneratorEvent()
    data class UpdateBackgroundColor(val color: Int) : GeneratorEvent()
    data class UpdateModuleShape(val shape: ModuleShape) : GeneratorEvent()
    data class UpdateFrameStyle(val style: FrameStyle) : GeneratorEvent()
    data class UpdateLogo(val bitmap: Bitmap?) : GeneratorEvent()
    data class UpdateLogoSize(val percent: Float) : GeneratorEvent()
    data object ShowForegroundColorPicker : GeneratorEvent()
    data object ShowBackgroundColorPicker : GeneratorEvent()
    data object HideColorPicker : GeneratorEvent()
    data object DismissContrastWarning : GeneratorEvent()
    data object ToggleCustomization : GeneratorEvent()
    
    // Form input events
    data class UpdateUrl(val value: String) : GeneratorEvent()
    data class UpdateWifiSsid(val value: String) : GeneratorEvent()
    data class UpdateWifiPassword(val value: String) : GeneratorEvent()
    data class UpdateWifiSecurity(val value: WifiSecurity) : GeneratorEvent()
    data class UpdateWifiHidden(val value: Boolean) : GeneratorEvent()
    data class UpdateContactFirstName(val value: String) : GeneratorEvent()
    data class UpdateContactLastName(val value: String) : GeneratorEvent()
    data class UpdateContactPhone(val value: String) : GeneratorEvent()
    data class UpdateContactEmail(val value: String) : GeneratorEvent()
    data class UpdateContactOrganization(val value: String) : GeneratorEvent()
    data class UpdateContactWebsite(val value: String) : GeneratorEvent()
    data class UpdatePhoneNumber(val value: String) : GeneratorEvent()
    data class UpdateSmsNumber(val value: String) : GeneratorEvent()
    data class UpdateSmsMessage(val value: String) : GeneratorEvent()
    data class UpdateEmailAddress(val value: String) : GeneratorEvent()
    data class UpdateEmailSubject(val value: String) : GeneratorEvent()
    data class UpdateEmailBody(val value: String) : GeneratorEvent()
    data class UpdateLocationLatitude(val value: String) : GeneratorEvent()
    data class UpdateLocationLongitude(val value: String) : GeneratorEvent()
    data class UpdateLocationLabel(val value: String) : GeneratorEvent()
    data class UpdateSocialPlatform(val value: SocialPlatform) : GeneratorEvent()
    data class UpdateSocialUsername(val value: String) : GeneratorEvent()
    data class UpdateAppPackageName(val value: String) : GeneratorEvent()
    data class UpdateUpiId(val value: String) : GeneratorEvent()
    data class UpdateUpiMobileNumber(val value: String) : GeneratorEvent()
    data class UpdateUpiInputMode(val mode: UpiPayeeAddress.InputMode) : GeneratorEvent()
    data class UpdateUpiMobileSuffix(val suffix: UpiPayeeAddress.MobileSuffix) : GeneratorEvent()
    data class UpdateUpiPayeeName(val value: String) : GeneratorEvent()
    data class UpdateUpiAmount(val value: String) : GeneratorEvent()
    data class UpdateUpiNote(val value: String) : GeneratorEvent()
    data class UpdatePlainText(val value: String) : GeneratorEvent()
}

@HiltViewModel
class GeneratorViewModel @Inject constructor(
    private val qrGenerator: QrGenerator,
    private val bitmapUtils: BitmapUtils,
    private val generatedQrRepository: GeneratedQrRepository,
    private val inAppReviewManager: InAppReviewManager
) : ViewModel() {
    
    private val _state = MutableStateFlow(GeneratorState())
    val state: StateFlow<GeneratorState> = _state.asStateFlow()
    
    private var regenerateJob: Job? = null
    
    fun onEvent(event: GeneratorEvent) {
        when (event) {
            is GeneratorEvent.SelectType -> selectType(event.type)
            is GeneratorEvent.ClearType -> clearType()
            is GeneratorEvent.Generate -> generate()
            is GeneratorEvent.Save -> save()
            is GeneratorEvent.Share -> share()
            is GeneratorEvent.Reset -> reset()
            is GeneratorEvent.ClearError -> clearError()
            is GeneratorEvent.DismissSuccess -> dismissSuccess()
            is GeneratorEvent.BackToForm -> backToForm()
            
            // Customization events
            is GeneratorEvent.ToggleCustomization -> _state.update { 
                it.copy(showCustomizationPanel = !it.showCustomizationPanel) 
            }
            is GeneratorEvent.UpdateForegroundColor -> updateForegroundColor(event.color)
            is GeneratorEvent.UpdateBackgroundColor -> updateBackgroundColor(event.color)
            is GeneratorEvent.UpdateModuleShape -> updateModuleShape(event.shape)
            is GeneratorEvent.UpdateFrameStyle -> updateFrameStyle(event.style)
            is GeneratorEvent.UpdateLogo -> updateLogo(event.bitmap)
            is GeneratorEvent.UpdateLogoSize -> updateLogoSize(event.percent)
            is GeneratorEvent.ShowForegroundColorPicker -> _state.update { 
                it.copy(showColorPicker = true, isPickingForeground = true) 
            }
            is GeneratorEvent.ShowBackgroundColorPicker -> _state.update { 
                it.copy(showColorPicker = true, isPickingForeground = false) 
            }
            is GeneratorEvent.HideColorPicker -> _state.update { it.copy(showColorPicker = false) }
            is GeneratorEvent.DismissContrastWarning -> _state.update { it.copy(showContrastWarning = false) }
            
            // Form updates
            is GeneratorEvent.UpdateUrl -> _state.update { it.copy(urlInput = event.value) }
            is GeneratorEvent.UpdateWifiSsid -> _state.update { it.copy(wifiSsid = event.value) }
            is GeneratorEvent.UpdateWifiPassword -> _state.update { it.copy(wifiPassword = event.value) }
            is GeneratorEvent.UpdateWifiSecurity -> _state.update { it.copy(wifiSecurity = event.value) }
            is GeneratorEvent.UpdateWifiHidden -> _state.update { it.copy(wifiHidden = event.value) }
            is GeneratorEvent.UpdateContactFirstName -> _state.update { it.copy(contactFirstName = event.value) }
            is GeneratorEvent.UpdateContactLastName -> _state.update { it.copy(contactLastName = event.value) }
            is GeneratorEvent.UpdateContactPhone -> _state.update { it.copy(contactPhone = event.value) }
            is GeneratorEvent.UpdateContactEmail -> _state.update { it.copy(contactEmail = event.value) }
            is GeneratorEvent.UpdateContactOrganization -> _state.update { it.copy(contactOrganization = event.value) }
            is GeneratorEvent.UpdateContactWebsite -> _state.update { it.copy(contactWebsite = event.value) }
            is GeneratorEvent.UpdatePhoneNumber -> _state.update { it.copy(phoneNumber = event.value) }
            is GeneratorEvent.UpdateSmsNumber -> _state.update { it.copy(smsNumber = event.value) }
            is GeneratorEvent.UpdateSmsMessage -> _state.update { it.copy(smsMessage = event.value) }
            is GeneratorEvent.UpdateEmailAddress -> _state.update { it.copy(emailAddress = event.value) }
            is GeneratorEvent.UpdateEmailSubject -> _state.update { it.copy(emailSubject = event.value) }
            is GeneratorEvent.UpdateEmailBody -> _state.update { it.copy(emailBody = event.value) }
            is GeneratorEvent.UpdateLocationLatitude -> _state.update { it.copy(locationLatitude = event.value) }
            is GeneratorEvent.UpdateLocationLongitude -> _state.update { it.copy(locationLongitude = event.value) }
            is GeneratorEvent.UpdateLocationLabel -> _state.update { it.copy(locationLabel = event.value) }
            is GeneratorEvent.UpdateSocialPlatform -> _state.update { it.copy(socialPlatform = event.value) }
            is GeneratorEvent.UpdateSocialUsername -> _state.update { it.copy(socialUsername = event.value) }
            is GeneratorEvent.UpdateAppPackageName -> _state.update { it.copy(appPackageName = event.value) }
            is GeneratorEvent.UpdateUpiId -> _state.update { it.copy(upiId = event.value) }
            is GeneratorEvent.UpdateUpiMobileNumber -> _state.update { it.copy(upiMobileNumber = event.value) }
            is GeneratorEvent.UpdateUpiInputMode -> _state.update { it.copy(upiInputMode = event.mode) }
            is GeneratorEvent.UpdateUpiMobileSuffix -> _state.update { it.copy(upiMobileSuffix = event.suffix) }
            is GeneratorEvent.UpdateUpiPayeeName -> _state.update { it.copy(upiPayeeName = event.value) }
            is GeneratorEvent.UpdateUpiAmount -> _state.update { it.copy(upiAmount = event.value) }
            is GeneratorEvent.UpdateUpiNote -> _state.update { it.copy(upiNote = event.value) }
            is GeneratorEvent.UpdatePlainText -> _state.update { it.copy(plainText = event.value) }
        }
    }
    
    private fun selectType(type: QrTypeOption) {
        _state.update { 
            it.copy(
                selectedType = type, 
                currentStep = GeneratorStep.FORM_INPUT,
                generatedBitmap = null
            ) 
        }
    }
    
    private fun clearType() {
        _state.update { GeneratorState() }
    }
    
    private fun backToForm() {
        _state.update { 
            it.copy(
                currentStep = GeneratorStep.FORM_INPUT,
                generatedBitmap = null,
                showCustomizationPanel = false,
                customization = QrCustomization()  // Reset customization
            ) 
        }
    }
    
    private fun updateForegroundColor(color: Int) {
        val newCustomization = _state.value.customization.copy(foregroundColor = color)
        updateCustomizationAndRegenerate(newCustomization)
    }
    
    private fun updateBackgroundColor(color: Int) {
        val newCustomization = _state.value.customization.copy(backgroundColor = color)
        updateCustomizationAndRegenerate(newCustomization)
    }
    
    private fun updateModuleShape(shape: ModuleShape) {
        val newCustomization = _state.value.customization.copy(moduleShape = shape)
        updateCustomizationAndRegenerate(newCustomization)
    }
    
    private fun updateFrameStyle(style: FrameStyle) {
        val newCustomization = _state.value.customization.copy(frameStyle = style)
        updateCustomizationAndRegenerate(newCustomization)
    }
    
    private fun updateLogo(bitmap: Bitmap?) {
        val newCustomization = _state.value.customization.copy(logoBitmap = bitmap)
        updateCustomizationAndRegenerate(newCustomization)
    }
    
    private fun updateLogoSize(percent: Float) {
        val clampedPercent = percent.coerceIn(
            QrCustomization.MIN_LOGO_SIZE_PERCENT,
            QrCustomization.MAX_LOGO_SIZE_PERCENT
        )
        val newCustomization = _state.value.customization.copy(logoSizePercent = clampedPercent)
        updateCustomizationAndRegenerate(newCustomization)
    }
    
    private fun updateCustomizationAndRegenerate(newCustomization: QrCustomization) {
        val showWarning = !newCustomization.hasAdequateContrast()
        _state.update { 
            it.copy(
                customization = newCustomization,
                showContrastWarning = showWarning
            ) 
        }
        regenerateWithCustomization()
    }
    
    private fun regenerateWithCustomization() {
        regenerateJob?.cancel()
        regenerateJob = viewModelScope.launch {
            delay(150) // Debounce
            
            val currentState = _state.value
            if (currentState.qrContent.isBlank()) return@launch
            
            _state.update { it.copy(isApplyingCustomization = true) }
            
            try {
                val bitmap = qrGenerator.generateCustomized(
                    content = currentState.qrContent,
                    size = 512,
                    customization = currentState.customization
                )
                
                _state.update { it.copy(generatedBitmap = bitmap, isApplyingCustomization = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isApplyingCustomization = false) }
            }
        }
    }
    
    private fun generate() {
        val currentState = _state.value
        val selectedType = currentState.selectedType ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true, error = null) }
            
            try {
                val (qrContent, displayLabel) = buildQrContent(selectedType, currentState)
                
                if (qrContent.isBlank()) {
                    _state.update { it.copy(isGenerating = false, error = "Please fill in required fields") }
                    return@launch
                }
                
                // Generate with current customization (default or user-modified)
                val bitmap = qrGenerator.generateCustomized(
                    content = qrContent,
                    size = 512,
                    customization = currentState.customization
                )
                
                if (bitmap != null) {
                    // Save to history
                    generatedQrRepository.saveGenerated(
                        qrType = selectedType.name,
                        qrContent = qrContent,
                        displayLabel = displayLabel
                    )
                    
                    // Record successful action for In-App Review
                    inAppReviewManager.recordSuccessfulAction()
                    
                    _state.update { 
                        it.copy(
                            isGenerating = false,
                            currentStep = GeneratorStep.PREVIEW,
                            generatedBitmap = bitmap,
                            qrContent = qrContent,
                            displayLabel = displayLabel,
                            showCustomizationPanel = false
                        ) 
                    }
                } else {
                    _state.update { it.copy(isGenerating = false, error = "Failed to generate QR code") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isGenerating = false, error = "Error: ${e.message}") }
            }
        }
    }
    
    private fun buildQrContent(type: QrTypeOption, state: GeneratorState): Pair<String, String> {
        return when (type) {
            QrTypeOption.URL -> {
                val qrType = QrType.Url(state.urlInput)
                qrType.toQrString() to state.urlInput
            }
            QrTypeOption.SMS -> {
                val qrType = QrType.Sms(state.smsNumber, state.smsMessage)
                qrType.toQrString() to "SMS: ${state.smsNumber}"
            }
            QrTypeOption.EMAIL -> {
                val qrType = QrType.Email(state.emailAddress, state.emailSubject, state.emailBody)
                qrType.toQrString() to state.emailAddress
            }
            QrTypeOption.LOCATION -> {
                val lat = state.locationLatitude.toDoubleOrNull() ?: 0.0
                val lng = state.locationLongitude.toDoubleOrNull() ?: 0.0
                val qrType = QrType.Location(lat, lng, state.locationLabel)
                qrType.toQrString() to state.locationLabel.ifBlank { "$lat, $lng" }
            }
            QrTypeOption.SOCIAL_MEDIA -> {
                val qrType = QrType.SocialMedia(state.socialPlatform, state.socialUsername)
                qrType.toQrString() to "${state.socialPlatform.displayName}: ${state.socialUsername}"
            }
            QrTypeOption.APP_DOWNLOAD -> {
                val qrType = QrType.AppDownload(state.appPackageName)
                qrType.toQrString() to state.appPackageName
            }
            QrTypeOption.UPI -> {
                val payeeAddress = UpiPayeeAddress.resolve(
                    mode = state.upiInputMode,
                    upiId = state.upiId,
                    mobileNumber = state.upiMobileNumber,
                    mobileSuffix = state.upiMobileSuffix,
                ) ?: error("Invalid UPI payee details")

                val qrType = QrType.Upi(
                    upiId = payeeAddress,
                    payeeName = state.upiPayeeName,
                    amount = state.upiAmount,
                    transactionNote = state.upiNote,
                )
                val summary = when (state.upiInputMode) {
                    UpiPayeeAddress.InputMode.MOBILE_NUMBER -> {
                        val mobile = UpiPayeeAddress.normalizeIndianMobile(state.upiMobileNumber).orEmpty()
                        "UPI: ${state.upiPayeeName} (+91 $mobile)"
                    }
                    UpiPayeeAddress.InputMode.UPI_ID -> "UPI: ${state.upiPayeeName}"
                }
                qrType.toQrString() to summary
            }
            QrTypeOption.TEXT -> {
                val qrType = QrType.Text(state.plainText)
                qrType.toQrString() to state.plainText.take(50)
            }
        }
    }
    
    private fun save() {
        val bitmap = _state.value.generatedBitmap ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            
            val success = bitmapUtils.saveToGallery(bitmap)
            
            _state.update { 
                it.copy(
                    isSaving = false,
                    showSuccess = success,
                    successMessage = if (success) "QR code saved to gallery" else "",
                    error = if (!success) "Failed to save QR code" else null
                )
            }
        }
    }
    
    private fun share() {
        val bitmap = _state.value.generatedBitmap ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isSharing = true) }
            
            bitmapUtils.shareBitmap(bitmap)
            
            _state.update { it.copy(isSharing = false) }
        }
    }
    
    private fun reset() {
        _state.update { 
            it.copy(
                currentStep = GeneratorStep.FORM_INPUT,
                generatedBitmap = null,
                showCustomizationPanel = false,
                customization = QrCustomization()
            )
        }
    }
    
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    private fun dismissSuccess() {
        _state.update { it.copy(showSuccess = false, successMessage = "") }
    }
}
