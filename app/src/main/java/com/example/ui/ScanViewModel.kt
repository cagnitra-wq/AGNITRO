package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.ScanEntity
import com.example.data.model.StockDetectionEntity
import com.example.data.network.MarketHoursUtil
import com.example.data.repository.TradingScannerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TradingScannerRepository

    val allScans: StateFlow<List<ScanEntity>>
    private val _selectedScan = MutableStateFlow<ScanEntity?>(null)
    val selectedScan: StateFlow<ScanEntity?> = _selectedScan.asStateFlow()

    private val _isPollingActive = MutableStateFlow(true)
    val isPollingActive: StateFlow<Boolean> = _isPollingActive.asStateFlow()

    private val _secondsRemaining = MutableStateFlow(15)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _sortChronologicalAsc = MutableStateFlow(true)
    val sortChronologicalAsc: StateFlow<Boolean> = _sortChronologicalAsc.asStateFlow()

    private val _isSimulationMode = MutableStateFlow(false)
    val isSimulationMode: StateFlow<Boolean> = _isSimulationMode.asStateFlow()

    private val _newlyDetectedSymbols = MutableStateFlow<Set<String>>(emptySet())
    val newlyDetectedSymbols: StateFlow<Set<String>> = _newlyDetectedSymbols.asStateFlow()

    private val _statusMessage = MutableStateFlow("Initializing Chartink scanner...")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _marketStatus = MutableStateFlow(MarketHoursUtil.getMarketStatusLabel())
    val marketStatus: StateFlow<String> = _marketStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var pollingJob: Job? = null
    private var highlightClearJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TradingScannerRepository(database.scanDao())

        allScans = repository.getAllScans()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        viewModelScope.launch {
            // Seed defaults if empty
            repository.seedDefaultScansIfEmpty()

            // Select first scan once loaded
            allScans.collect { list ->
                if (_selectedScan.value == null && list.isNotEmpty()) {
                    _selectedScan.value = list.first()
                    startPollingLoop()
                }
            }
        }

        // Market status update loop
        viewModelScope.launch {
            while (true) {
                _marketStatus.value = MarketHoursUtil.getMarketStatusLabel()
                delay(10000)
            }
        }
    }

    val detectedStocks: StateFlow<List<StockDetectionEntity>> = combine(
        _selectedScan,
        _sortChronologicalAsc,
        _searchQuery
    ) { scan, asc, query ->
        Triple(scan, asc, query)
    }.flatMapLatest { (scan, asc, query) ->
        if (scan == null) {
            flowOf(emptyList())
        } else {
            repository.getDetections(scan.id, asc).combine(flowOf(query)) { list, q ->
                if (q.isBlank()) {
                    list
                } else {
                    list.filter {
                        it.symbol.contains(q, ignoreCase = true) ||
                                it.companyName.contains(q, ignoreCase = true)
                    }
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectScan(scan: ScanEntity) {
        if (_selectedScan.value?.id == scan.id) return
        _selectedScan.value = scan
        _newlyDetectedSymbols.value = emptySet()
        _secondsRemaining.value = scan.intervalSeconds
        startPollingLoop()
        pollNow()
    }

    fun togglePolling() {
        val newState = !_isPollingActive.value
        _isPollingActive.value = newState
        if (newState) {
            startPollingLoop()
            startPriceTickerLoop()
            _statusMessage.value = "Live scan & price polling resumed"
        } else {
            pollingJob?.cancel()
            priceTickerJob?.cancel()
            _statusMessage.value = "Live scan polling paused"
        }
    }

    fun triggerManualPoll() {
        pollNow()
    }

    fun toggleSortOrder() {
        _sortChronologicalAsc.value = !_sortChronologicalAsc.value
    }

    fun toggleSimulationMode() {
        val newSim = !_isSimulationMode.value
        _isSimulationMode.value = newSim
        _statusMessage.value = if (newSim) "Switched to Market Simulator Mode" else "Switched to Live Chartink Network Mode"
        pollNow()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSessionDetections() {
        viewModelScope.launch {
            val scan = _selectedScan.value ?: return@launch
            repository.clearSessionDetections(scan.id)
            repository.resetSimulation()
            _newlyDetectedSymbols.value = emptySet()
            _statusMessage.value = "Cleared detections for today's session"
        }
    }

    fun addScan(
        name: String,
        urlOrClause: String,
        intervalSeconds: Int,
        description: String,
        isSimulation: Boolean
    ) {
        viewModelScope.launch {
            val entity = ScanEntity(
                name = name,
                urlOrClause = urlOrClause,
                intervalSeconds = intervalSeconds,
                description = description,
                isSimulation = isSimulation
            )
            val newId = repository.insertScan(entity)
            _selectedScan.value = entity.copy(id = newId)
            _statusMessage.value = "Added new scan: $name"
            startPollingLoop()
            pollNow()
        }
    }

    fun updateScan(updatedScan: ScanEntity) {
        viewModelScope.launch {
            repository.updateScan(updatedScan)
            if (_selectedScan.value?.id == updatedScan.id) {
                _selectedScan.value = updatedScan
                _secondsRemaining.value = updatedScan.intervalSeconds
                startPollingLoop()
                pollNow()
            }
            _statusMessage.value = "Updated ${updatedScan.name}"
        }
    }

    fun toggleScanActive(scan: ScanEntity, isActive: Boolean) {
        viewModelScope.launch {
            val updated = scan.copy(isActive = isActive)
            repository.updateScan(updated)
            if (_selectedScan.value?.id == scan.id) {
                _selectedScan.value = updated
                if (!isActive) {
                    pollingJob?.cancel()
                } else if (_isPollingActive.value) {
                    startPollingLoop()
                }
            }
        }
    }

    fun triggerPollForScan(scan: ScanEntity) {
        _selectedScan.value = scan
        pollNow()
    }

    fun deleteScan(scan: ScanEntity) {
        viewModelScope.launch {
            repository.deleteScan(scan)
            val currentList = allScans.value
            val remaining = currentList.filter { it.id != scan.id }
            if (remaining.isNotEmpty()) {
                _selectedScan.value = remaining.first()
            } else {
                _selectedScan.value = null
            }
        }
    }

    private fun startPollingLoop() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (_isPollingActive.value) {
                val interval = _selectedScan.value?.intervalSeconds ?: 15
                for (sec in interval downTo 1) {
                    _secondsRemaining.value = sec
                    delay(1000)
                    if (!_isPollingActive.value) break
                }
                if (_isPollingActive.value) {
                    pollNow()
                }
            }
        }
        startPriceTickerLoop()
    }

    private var priceTickerJob: Job? = null

    /**
     * Continuous 3-second live price ticker engine during market hours.
     * Updates real-time prices, highs, lows, and % daily change for all discovered stocks.
     */
    private fun startPriceTickerLoop() {
        priceTickerJob?.cancel()
        priceTickerJob = viewModelScope.launch {
            while (_isPollingActive.value) {
                delay(3000)
                val scan = _selectedScan.value ?: continue
                val forceSim = _isSimulationMode.value || scan.isSimulation
                try {
                    repository.refreshLivePricesForActiveDetections(scan.id, forceSim)
                } catch (e: Exception) {
                    // Ignore transient network hiccups during fast price ticks
                }
            }
        }
    }

    private fun pollNow() {
        val scan = _selectedScan.value ?: return
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val forceSim = _isSimulationMode.value || scan.isSimulation
                val result = repository.executeScanAndSync(
                    scanId = scan.id,
                    urlOrClause = scan.urlOrClause,
                    forceSimulation = forceSim
                )

                _statusMessage.value = result.message

                if (result.newlyDetectedSymbols.isNotEmpty()) {
                    // Update newly detected symbols to show the visual highlight
                    _newlyDetectedSymbols.value = result.newlyDetectedSymbols
                    highlightClearJob?.cancel()
                    // Keep the visual highlight active for 10 seconds before easing off
                    highlightClearJob = launch {
                        delay(10000)
                        _newlyDetectedSymbols.value = emptySet()
                    }
                }
            } catch (e: Exception) {
                _statusMessage.value = "Scan error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
        priceTickerJob?.cancel()
        highlightClearJob?.cancel()
    }
}
