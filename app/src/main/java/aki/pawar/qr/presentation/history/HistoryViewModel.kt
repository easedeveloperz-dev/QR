package aki.pawar.qr.presentation.history

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aki.pawar.qr.data.local.entity.GeneratedQrEntity
import aki.pawar.qr.data.local.entity.ScanHistoryEntity
import aki.pawar.qr.data.repository.GeneratedQrRepository
import aki.pawar.qr.data.repository.ScanHistoryRepository
import aki.pawar.qr.util.IntentHandler
import aki.pawar.qr.util.QrGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Tabs for History Screen
 */
enum class HistoryTab {
    SCANNED,
    GENERATED
}

/**
 * UI State for History Screen
 */
data class HistoryState(
    val selectedTab: HistoryTab = HistoryTab.SCANNED,
    val scannedHistory: List<ScanHistoryEntity> = emptyList(),
    val generatedHistory: List<GeneratedQrEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedItem: Any? = null,
    val regeneratedBitmap: Bitmap? = null
)

/**
 * Events for History Screen
 */
sealed class HistoryEvent {
    data class SelectTab(val tab: HistoryTab) : HistoryEvent()
    data class Search(val query: String) : HistoryEvent()
    data class SelectScanItem(val item: ScanHistoryEntity) : HistoryEvent()
    data class SelectGeneratedItem(val item: GeneratedQrEntity) : HistoryEvent()
    data object ClearSelection : HistoryEvent()
    data class DeleteScan(val id: Long) : HistoryEvent()
    data class DeleteGenerated(val id: Long) : HistoryEvent()
    data class ToggleScanFavorite(val id: Long, val isFavorite: Boolean) : HistoryEvent()
    data class ToggleGeneratedFavorite(val id: Long, val isFavorite: Boolean) : HistoryEvent()
    data object ClearAllScans : HistoryEvent()
    data object ClearAllGenerated : HistoryEvent()
    data class CopyScanContent(val content: String) : HistoryEvent()
    data class ShareScanContent(val content: String) : HistoryEvent()
    data class RegenerateQr(val content: String) : HistoryEvent()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val scanHistoryRepository: ScanHistoryRepository,
    private val generatedQrRepository: GeneratedQrRepository,
    private val intentHandler: IntentHandler,
    private val qrGenerator: QrGenerator
) : ViewModel() {
    
    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()
    
    init {
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            combine(
                scanHistoryRepository.getAllScans(),
                generatedQrRepository.getAllGenerated()
            ) { scans, generated ->
                _state.update { 
                    it.copy(
                        scannedHistory = scans,
                        generatedHistory = generated,
                        isLoading = false
                    )
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, Unit)
        }
        
        // Observe scan history
        viewModelScope.launch {
            scanHistoryRepository.getAllScans().collect { scans ->
                _state.update { it.copy(scannedHistory = scans, isLoading = false) }
            }
        }
        
        // Observe generated history
        viewModelScope.launch {
            generatedQrRepository.getAllGenerated().collect { generated ->
                _state.update { it.copy(generatedHistory = generated) }
            }
        }
    }
    
    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.SelectTab -> selectTab(event.tab)
            is HistoryEvent.Search -> search(event.query)
            is HistoryEvent.SelectScanItem -> selectScanItem(event.item)
            is HistoryEvent.SelectGeneratedItem -> selectGeneratedItem(event.item)
            is HistoryEvent.ClearSelection -> clearSelection()
            is HistoryEvent.DeleteScan -> deleteScan(event.id)
            is HistoryEvent.DeleteGenerated -> deleteGenerated(event.id)
            is HistoryEvent.ToggleScanFavorite -> toggleScanFavorite(event.id, event.isFavorite)
            is HistoryEvent.ToggleGeneratedFavorite -> toggleGeneratedFavorite(event.id, event.isFavorite)
            is HistoryEvent.ClearAllScans -> clearAllScans()
            is HistoryEvent.ClearAllGenerated -> clearAllGenerated()
            is HistoryEvent.CopyScanContent -> copyContent(event.content)
            is HistoryEvent.ShareScanContent -> shareContent(event.content)
            is HistoryEvent.RegenerateQr -> regenerateQr(event.content)
        }
    }
    
    private fun selectTab(tab: HistoryTab) {
        _state.update { it.copy(selectedTab = tab, selectedItem = null, regeneratedBitmap = null) }
    }
    
    private fun search(query: String) {
        _state.update { it.copy(searchQuery = query) }
        
        viewModelScope.launch {
            if (query.isBlank()) {
                loadHistory()
            } else {
                when (_state.value.selectedTab) {
                    HistoryTab.SCANNED -> {
                        scanHistoryRepository.searchScans(query).collect { results ->
                            _state.update { it.copy(scannedHistory = results) }
                        }
                    }
                    HistoryTab.GENERATED -> {
                        generatedQrRepository.searchGenerated(query).collect { results ->
                            _state.update { it.copy(generatedHistory = results) }
                        }
                    }
                }
            }
        }
    }
    
    private fun selectScanItem(item: ScanHistoryEntity) {
        _state.update { it.copy(selectedItem = item, regeneratedBitmap = null) }
        regenerateQr(item.rawValue)
    }
    
    private fun selectGeneratedItem(item: GeneratedQrEntity) {
        _state.update { it.copy(selectedItem = item, regeneratedBitmap = null) }
        regenerateQr(item.qrContent)
    }
    
    private fun clearSelection() {
        _state.update { it.copy(selectedItem = null, regeneratedBitmap = null) }
    }
    
    private fun deleteScan(id: Long) {
        viewModelScope.launch {
            scanHistoryRepository.deleteScanById(id)
            if ((_state.value.selectedItem as? ScanHistoryEntity)?.id == id) {
                clearSelection()
            }
        }
    }
    
    private fun deleteGenerated(id: Long) {
        viewModelScope.launch {
            generatedQrRepository.deleteGeneratedById(id)
            if ((_state.value.selectedItem as? GeneratedQrEntity)?.id == id) {
                clearSelection()
            }
        }
    }
    
    private fun toggleScanFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            scanHistoryRepository.toggleFavorite(id, !isFavorite)
        }
    }
    
    private fun toggleGeneratedFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            generatedQrRepository.toggleFavorite(id, !isFavorite)
        }
    }
    
    private fun clearAllScans() {
        viewModelScope.launch {
            scanHistoryRepository.deleteAllScans()
            clearSelection()
        }
    }
    
    private fun clearAllGenerated() {
        viewModelScope.launch {
            generatedQrRepository.deleteAllGenerated()
            clearSelection()
        }
    }
    
    private fun copyContent(content: String) {
        intentHandler.copyToClipboard(content)
    }
    
    private fun shareContent(content: String) {
        intentHandler.shareText(content)
    }
    
    private fun regenerateQr(content: String) {
        viewModelScope.launch {
            val bitmap = qrGenerator.generate(content, size = 256)
            _state.update { it.copy(regeneratedBitmap = bitmap) }
        }
    }
}

