package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ScanEntity
import com.example.data.model.StockDetectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {

    // Scans Management
    @Query("SELECT * FROM scans ORDER BY createdAt ASC")
    fun getAllScans(): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scans WHERE id = :id LIMIT 1")
    suspend fun getScanById(id: Long): ScanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanEntity): Long

    @Update
    suspend fun updateScan(scan: ScanEntity)

    @Delete
    suspend fun deleteScan(scan: ScanEntity)

    // Stock Detections Management (Chronological sorting by firstSeenTimestamp)
    @Query("SELECT * FROM stock_detections WHERE scanId = :scanId AND sessionDate = :sessionDate ORDER BY firstSeenTimestamp ASC")
    fun getDetectionsForScanAsc(scanId: Long, sessionDate: String): Flow<List<StockDetectionEntity>>

    @Query("SELECT * FROM stock_detections WHERE scanId = :scanId AND sessionDate = :sessionDate ORDER BY firstSeenTimestamp DESC")
    fun getDetectionsForScanDesc(scanId: Long, sessionDate: String): Flow<List<StockDetectionEntity>>

    @Query("SELECT * FROM stock_detections WHERE scanId = :scanId AND symbol = :symbol AND sessionDate = :sessionDate LIMIT 1")
    suspend fun getDetectionBySymbol(scanId: Long, symbol: String, sessionDate: String): StockDetectionEntity?

    @Query("SELECT COUNT(*) FROM stock_detections WHERE scanId = :scanId AND sessionDate = :sessionDate")
    fun getDetectionCount(scanId: Long, sessionDate: String): Flow<Int>

    @Query("SELECT * FROM stock_detections WHERE scanId = :scanId AND sessionDate = :sessionDate")
    suspend fun getDetectionsListForScan(scanId: Long, sessionDate: String): List<StockDetectionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDetection(entity: StockDetectionEntity): Long

    @Query("UPDATE stock_detections SET livePrice = :price, percentChange = :change, volume = :volume, lastUpdatedTimestamp = :lastUpdated, dayHigh = :high, dayLow = :low WHERE id = :id")
    suspend fun updateDetectionPrice(
        id: Long,
        price: Double,
        change: Double,
        volume: Long,
        lastUpdated: Long,
        high: Double,
        low: Double
    )

    @Query("DELETE FROM stock_detections WHERE scanId = :scanId AND sessionDate = :sessionDate")
    suspend fun clearDetectionsForScan(scanId: Long, sessionDate: String)

    @Query("DELETE FROM stock_detections WHERE scanId = :scanId")
    suspend fun clearAllDetectionsForScan(scanId: Long)

    @Query("DELETE FROM stock_detections")
    suspend fun clearAllDetections()
}
