package com.example.personalassetmanagement.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Item::class, ItemType::class], version = 1, exportSchema = false)
abstract class AssetDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun itemTypeDao(): ItemTypeDao

    companion object {
        @Volatile
        private var INSTANCE: AssetDatabase? = null

        fun getDatabase(context: Context): AssetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AssetDatabase::class.java,
                    "asset_database"
                )
                .addCallback(AssetDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AssetDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.itemTypeDao())
                }
            }
        }

        suspend fun populateDatabase(itemTypeDao: ItemTypeDao) {
            // Add default item types
            itemTypeDao.insert(ItemType(name = "Electronic"))
            itemTypeDao.insert(ItemType(name = "Collectible"))
        }
    }
}