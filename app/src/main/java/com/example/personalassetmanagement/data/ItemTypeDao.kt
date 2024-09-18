package com.example.personalassetmanagement.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itemType: ItemType): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(itemTypes: List<ItemType>): List<Long>

    @Update
    suspend fun update(itemType: ItemType)

    @Delete
    suspend fun delete(itemType: ItemType)

    @Query("SELECT * FROM item_types WHERE id = :id")
    fun getItemTypeById(id: Long): Flow<ItemType>

    @Query("SELECT * FROM item_types WHERE name = :name")
    fun getItemTypeByName(name: String): Flow<ItemType?>

    @Query("SELECT EXISTS(SELECT 1 FROM item_types WHERE name = :name)")
    suspend fun itemTypeExists(name: String): Boolean

    @Query("SELECT * FROM item_types ORDER BY name ASC")
    fun getAllItemTypes(): Flow<List<ItemType>>

    @Query("SELECT COUNT(*) FROM item_types")
    fun getItemTypeCount(): Flow<Int>
}