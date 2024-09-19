package com.example.personalassetmanagement.data

import androidx.room.*
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Item>)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Delete
    suspend fun deleteAll(items: List<Item>)

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemById(id: Long): Flow<Item>

    @Query("SELECT items.*, item_types.name AS typeName FROM items INNER JOIN item_types ON items.typeId = item_types.id ORDER BY items.dateModified DESC")
    fun getAllItemsWithType(): PagingSource<Int, ItemWithType>

    @Query("SELECT items.*, item_types.name AS typeName FROM items INNER JOIN item_types ON items.typeId = item_types.id WHERE items.typeId = :typeId ORDER BY items.dateModified DESC")
    fun getItemsByTypeWithType(typeId: Long): PagingSource<Int, ItemWithType>

    @Query("SELECT items.*, item_types.name AS typeName FROM items INNER JOIN item_types ON items.typeId = item_types.id ORDER BY " +
           "CASE WHEN :sortBy = 'name' AND :sortOrder = 'ASC' THEN items.name END ASC, " +
           "CASE WHEN :sortBy = 'name' AND :sortOrder = 'DESC' THEN items.name END DESC, " +
           "CASE WHEN :sortBy = 'dateAdded' AND :sortOrder = 'ASC' THEN items.dateAdded END ASC, " +
           "CASE WHEN :sortBy = 'dateAdded' AND :sortOrder = 'DESC' THEN items.dateAdded END DESC")
    fun getAllItemsWithTypeSorted(sortBy: String, sortOrder: String): PagingSource<Int, ItemWithType>

    @Query("SELECT COUNT(*) FROM items")
    fun getItemCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM items WHERE typeId = :typeId")
    fun getItemCountByType(typeId: Long): Flow<Int>

    @Query("SELECT * FROM items WHERE barcode = :barcode LIMIT 1")
    suspend fun getItemByBarcode(barcode: String): Item?
}

data class ItemWithType(
    @Embedded val item: Item,
    @ColumnInfo(name = "typeName") val typeName: String
)