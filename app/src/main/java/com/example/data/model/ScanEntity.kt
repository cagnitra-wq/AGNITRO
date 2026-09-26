package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val urlOrClause: String,
    val description: String = "",
    val intervalSeconds: Int = 15,
    val isActive: Boolean = true,
    val isSimulation: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
