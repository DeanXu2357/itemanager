package com.example.personalassetmanagement.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemTypeDao {
    @Query("SELECT * FROM item_types ORDER BY name ASC")
    fun getAllItemTypes(): LiveData<List<ItemType>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(itemType: ItemType)

    @Delete
    suspend fun delete(itemType: ItemType)

    @Update
    suspend fun update(itemType: ItemType)
}