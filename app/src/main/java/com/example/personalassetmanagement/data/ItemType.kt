package com.example.personalassetmanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "item_types")
@TypeConverters(Converters::class)
data class ItemType(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String,
    var icon: String? = null, // Store as resource name or Base64 encoded string
    var customFields: List<CustomField> = emptyList()
)

data class CustomField(
    val name: String,
    val type: FieldType,
    val options: List<String> = emptyList() // For custom options type
)

enum class FieldType {
    STRING, NUMBER, DECIMAL, DATE, TIME, DATETIME, DURATION, BOOLEAN, CUSTOM_OPTIONS, AMOUNT
}

// Add this class to handle type conversions for Room
class Converters {
    @TypeConverter
    fun fromCustomFields(value: String): List<CustomField> = value.split("|").map { field ->
        val (name, type, options) = field.split(":")
        CustomField(
            name,
            FieldType.valueOf(type),
            if (options.isNotEmpty()) options.split(",") else emptyList()
        )
    }

    @TypeConverter
    fun toCustomFields(customFields: List<CustomField>): String = customFields.joinToString("|") { field ->
        "${field.name}:${field.type}:${field.options.joinToString(",")}"
    }
}