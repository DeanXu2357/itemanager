package com.example.personalassetmanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val barcodeType: String? = null,
    val barcodeContent: String? = null,
    val details: String? = null
)