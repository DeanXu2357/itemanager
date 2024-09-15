package com.example.personalassetmanagement.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAllItemsSortedByName(): PagingSource<Int, Item>

    @Query("SELECT * FROM items ORDER BY type ASC")
    fun getAllItemsSortedByType(): PagingSource<Int, Item>

    @Query("SELECT * FROM items WHERE type = :itemType ORDER BY name ASC")
    fun getItemsByType(itemType: String): PagingSource<Int, Item>

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemById(id: Int): LiveData<Item>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Update
    suspend fun update(item: Item)

    @Query("SELECT COUNT(*) FROM items")
    suspend fun getTotalItems(): Int

    @Query("SELECT type, COUNT(*) as count FROM items GROUP BY type")
    suspend fun getItemsPerType(): List<ItemTypeCount>
}

data class ItemTypeCount(
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "count") val count: Int
)