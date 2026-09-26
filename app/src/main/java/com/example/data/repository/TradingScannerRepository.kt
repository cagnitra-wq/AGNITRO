package com.example.data.repository

import com.example.data.db.ScanDao
import com.example.data.model.ScanEntity
import com.example.data.model.StockDetectionEntity
import com.example.data.network.ChartinkService
import com.example.data.network.MarketHoursUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class SyncResult(
    val totalCount: Int,
    val newCount: Int,
    val newlyDetectedSymbols: Set<String>,
    val isSimulated: Boolean,
    val message: String
)

class TradingScannerRepository(
    private val scanDao: ScanDao,
    private val chartinkService: ChartinkService = ChartinkService()
) {

    fun getAllScans(): Flow<List<ScanEntity>> = scanDao.getAllScans()

    fun getDetections(scanId: Long, ascending: Boolean): Flow<List<StockDetectionEntity>> {
        val today = MarketHoursUtil.getCurrentSessionDate()
        return if (ascending) {
            scanDao.getDetectionsForScanAsc(scanId, today)
        } else {
            scanDao.getDetectionsForScanDesc(scanId, today)
        }
    }

    suspend fun insertScan(scan: ScanEntity): Long = scanDao.insertScan(scan)

    suspend fun updateScan(scan: ScanEntity) = scanDao.updateScan(scan)

    suspend fun deleteScan(scan: ScanEntity) {
        scanDao.clearAllDetectionsForScan(scan.id)
        scanDao.deleteScan(scan)
    }

    suspend fun clearSessionDetections(scanId: Long) {
        val today = MarketHoursUtil.getCurrentSessionDate()
        scanDao.clearDetectionsForScan(scanId, today)
    }

    suspend fun seedDefaultScansIfEmpty() = withContext(Dispatchers.IO) {
        val defaultScans = listOf(
            ScanEntity(
                name = "Scan 1: 15-Min ORB Breakout",
                urlOrClause = "( {cash} ( [0] 15 minute close > [0] 15 minute max( 20 , [0] 15 minute high ) and [0] 15 minute volume > 25000 ) )",
                description = "Opening range breakout with volume surge",
                intervalSeconds = 15,
                isActive = true,
                isSimulation = false
            ),
            ScanEntity(
                name = "Scan 2: RSI 60+ SuperTrend",
                urlOrClause = "( {cash} ( [0] 5 minute rsi( 14 ) > 60 and [0] 5 minute close > [0] 5 minute supertrend( 7 , 3 ) ) )",
                description = "Intraday momentum crossover scanner",
                intervalSeconds = 15,
                isActive = true,
                isSimulation = false
            ),
            ScanEntity(
                name = "Scan 3: Volume Surge Shockers",
                urlOrClause = "( {cash} ( [0] 15 minute volume > [0] 15 minute sma( volume, 20 ) * 3 and [0] 15 minute close > [0] 15 minute open ) )",
                description = "Stocks trading at 3x average volume with bullish candle",
                intervalSeconds = 20,
                isActive = true,
                isSimulation = false
            ),
            ScanEntity(
                name = "Scan 4: Custom Chartink Screener",
                urlOrClause = "https://chartink.com/screener/bullish-intraday-breakout",
                description = "Direct Chartink web screener URL",
                intervalSeconds = 30,
                isActive = true,
                isSimulation = false
            )
        )

        for (scan in defaultScans) {
            scanDao.insertScan(scan)
        }
    }

    suspend fun executeScanAndSync(
        scanId: Long,
        urlOrClause: String,
        forceSimulation: Boolean
    ): SyncResult = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val sessionDate = MarketHoursUtil.getCurrentSessionDate()

        val scanResult = chartinkService.runScan(urlOrClause, forceSimulation = forceSimulation)
        val newlyDetectedSymbols = mutableSetOf<String>()
        var newCount = 0

        for (stock in scanResult.stocks) {
            val existing = scanDao.getDetectionBySymbol(scanId, stock.symbol, sessionDate)
            if (existing == null) {
                // First appearance! Record immutable firstSeenTimestamp
                val entity = StockDetectionEntity(
                    scanId = scanId,
                    symbol = stock.symbol,
                    companyName = stock.companyName,
                    livePrice = stock.price,
                    previousClose = stock.price / (1 + (stock.percentChange / 100)),
                    percentChange = stock.percentChange,
                    volume = stock.volume,
                    firstSeenTimestamp = now, // Fixed immutable time
                    lastUpdatedTimestamp = now,
                    sessionDate = sessionDate,
                    dayHigh = stock.high,
                    dayLow = stock.low
                )
                scanDao.insertDetection(entity)
                newlyDetectedSymbols.add(stock.symbol)
                newCount++
            } else {
                // Stock already appeared earlier: Update live price & metrics but KEEP original firstSeenTimestamp!
                scanDao.updateDetectionPrice(
                    id = existing.id,
                    price = stock.price,
                    change = stock.percentChange,
                    volume = stock.volume,
                    lastUpdated = now,
                    high = maxOf(existing.dayHigh, stock.high),
                    low = if (existing.dayLow > 0) minOf(existing.dayLow, stock.low) else stock.low
                )
            }
        }

        SyncResult(
            totalCount = scanResult.stocks.size,
            newCount = newCount,
            newlyDetectedSymbols = newlyDetectedSymbols,
            isSimulated = scanResult.isSimulated,
            message = scanResult.message
        )
    }

    suspend fun refreshLivePricesForActiveDetections(scanId: Long, forceSimulation: Boolean) = withContext(Dispatchers.IO) {
        val today = MarketHoursUtil.getCurrentSessionDate()
        val activeStocks = scanDao.getDetectionsListForScan(scanId, today)
        if (activeStocks.isEmpty()) return@withContext

        val now = System.currentTimeMillis()
        for (stock in activeStocks) {
            if (!forceSimulation) {
                val quote = chartinkService.fetchLiveQuote(stock.symbol)
                if (quote != null) {
                    scanDao.updateDetectionPrice(
                        id = stock.id,
                        price = quote.price,
                        change = quote.percentChange,
                        volume = if (quote.volume > 0) quote.volume else stock.volume,
                        lastUpdated = now,
                        high = maxOf(stock.dayHigh, quote.high),
                        low = if (stock.dayLow > 0) minOf(stock.dayLow, quote.low) else quote.low
                    )
                    continue
                }
            }

            // Real-time micro tick updates during off-market hours or simulation mode
            if (forceSimulation || !MarketHoursUtil.isMarketOpen()) {
                val tickPercent = (kotlin.random.Random.nextDouble(-0.15, 0.20) / 100.0)
                val newPrice = Math.round(stock.livePrice * (1.0 + tickPercent) * 100.0) / 100.0
                val newChange = Math.round((stock.percentChange + kotlin.random.Random.nextDouble(-0.06, 0.08)) * 100.0) / 100.0
                val newVol = stock.volume + kotlin.random.Random.nextLong(150, 1800)
                scanDao.updateDetectionPrice(
                    id = stock.id,
                    price = newPrice,
                    change = newChange,
                    volume = newVol,
                    lastUpdated = now,
                    high = maxOf(stock.dayHigh, newPrice),
                    low = if (stock.dayLow > 0) minOf(stock.dayLow, newPrice) else newPrice
                )
            }
        }
    }

    fun resetSimulation() {
        chartinkService.resetSimulation()
    }
}
