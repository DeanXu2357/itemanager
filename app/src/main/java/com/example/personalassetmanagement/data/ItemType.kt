package com.example.personalassetmanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_types")
data class ItemType(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)