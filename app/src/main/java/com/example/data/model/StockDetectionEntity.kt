package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_detections",
    indices = [
        Index(value = ["scanId", "symbol", "sessionDate"], unique = true),
        Index(value = ["firstSeenTimestamp"])
    ]
)
data class StockDetectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scanId: Long,
    val symbol: String,
    val companyName: String,
    val livePrice: Double,
    val previousClose: Double = 0.0,
    val percentChange: Double,
    val volume: Long = 0L,
    val firstSeenTimestamp: Long, // Immutable time when first detected
    val lastUpdatedTimestamp: Long,
    val sessionDate: String, // YYYY-MM-DD
    val dayHigh: Double = 0.0,
    val dayLow: Double = 0.0
)
