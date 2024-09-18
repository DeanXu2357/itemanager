package com.example.personalassetmanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = ItemType::class,
            parentColumns = ["id"],
            childColumns = ["typeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(Converters::class)
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String,
    var typeId: Long,
    var barcode: String? = null,
    var barcodeType: String? = null,
    var barcodeImage: String? = null, // Store as Base64 encoded string
    var dateAdded: Date = Date(),
    var dateModified: Date = Date(),
    var photos: List<String> = emptyList(), // Store as list of Base64 encoded strings
    var details: Map<String, Any> = emptyMap() // Store custom fields as key-value pairs
)

// Add this class to handle type conversions for Room
class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> = value.split(",")

    @TypeConverter
    fun toString(list: List<String>): String = list.joinToString(",")

    @TypeConverter
    fun fromMap(value: String): Map<String, Any> = value.split(",").associate {
        val (key, value) = it.split(":")
        key to when {
            value.startsWith("s:") -> value.substring(2)
            value.startsWith("n:") -> value.substring(2).toDouble()
            value.startsWith("b:") -> value.substring(2).toBoolean()
            value.startsWith("d:") -> Date(value.substring(2).toLong())
            else -> value
        }
    }

    @TypeConverter
    fun toMap(map: Map<String, Any>): String = map.entries.joinToString(",") { (key, value) ->
        "$key:${
            when (value) {
                is String -> "s:$value"
                is Number -> "n:$value"
                is Boolean -> "b:$value"
                is Date -> "d:${value.time}"
                else -> value.toString()
            }
        }"
    }
}