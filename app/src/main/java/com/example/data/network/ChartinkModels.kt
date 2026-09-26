package com.example.data.network

data class ChartinkStockRaw(
    val sr: Int? = null,
    val name: String? = null,
    val bsecode: String? = null,
    val nsecode: String? = null,
    val per_chg: Double? = null,
    val close: Double? = null,
    val volume: Long? = null
)

data class ChartinkScanResponse(
    val data: List<ChartinkStockRaw> = emptyList(),
    val error: String? = null
)

data class ScannedStockResult(
    val symbol: String,
    val companyName: String,
    val price: Double,
    val percentChange: Double,
    val volume: Long,
    val high: Double = 0.0,
    val low: Double = 0.0
)

data class LiveQuoteResult(
    val symbol: String,
    val price: Double,
    val percentChange: Double,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val volume: Long = 0L
)
