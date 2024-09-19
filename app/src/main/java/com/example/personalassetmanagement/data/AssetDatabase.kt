package com.example.personalassetmanagement.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@Database(entities = [Item::class, ItemType::class], version = 2, exportSchema = false)
@TypeConverters(AssetConverters::class)
abstract class AssetDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun itemTypeDao(): ItemTypeDao

    companion object {
        @Volatile
        private var INSTANCE: AssetDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AssetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AssetDatabase::class.java,
                    "asset_database"
                )
                .addCallback(AssetDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AssetDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.itemTypeDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(itemTypeDao: ItemTypeDao) {
            // Add default item types with custom fields
            val electronicsFields = listOf(
                CustomField("Brand", FieldType.STRING),
                CustomField("Model", FieldType.STRING),
                CustomField("Purchase Date", FieldType.DATE),
                CustomField("Warranty Expiry", FieldType.DATE),
                CustomField("Price", FieldType.AMOUNT)
            )
            itemTypeDao.insert(ItemType(name = "Electronics", customFields = electronicsFields))

            val collectiblesFields = listOf(
                CustomField("Series", FieldType.STRING),
                CustomField("Condition", FieldType.CUSTOM_OPTIONS, listOf("Mint", "Near Mint", "Good", "Fair", "Poor")),
                CustomField("Year", FieldType.NUMBER),
                CustomField("Estimated Value", FieldType.AMOUNT)
            )
            itemTypeDao.insert(ItemType(name = "Collectibles", customFields = collectiblesFields))
        }
    }
}

// Updated Converters class
class AssetConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromString(value: String): List<String> = value.split(",")

    @TypeConverter
    fun toString(list: List<String>): String = list.joinToString(",")

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