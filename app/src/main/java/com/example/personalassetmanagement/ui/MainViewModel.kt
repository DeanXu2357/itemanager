package com.example.personalassetmanagement.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.personalassetmanagement.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database: AssetDatabase = AssetDatabase.getDatabase(application, viewModelScope)
    private val itemDao: ItemDao = database.itemDao()
    private val itemTypeDao: ItemTypeDao = database.itemTypeDao()

    private var currentSortBy: String = "dateAdded"
    private var currentSortOrder: String = "DESC"

    val allItems: Flow<PagingData<ItemWithType>> =
            Pager(PagingConfig(pageSize = 20, enablePlaceholders = true, maxSize = 100)) {
                        itemDao.getAllItemsWithTypeSorted(currentSortBy, currentSortOrder)
                    }
                    .flow
                    .cachedIn(viewModelScope)

    val allItemTypes: Flow<List<ItemType>> = itemTypeDao.getAllItemTypes()

    fun insert(item: Item) = viewModelScope.launch(Dispatchers.IO) { itemDao.insert(item) }

    fun update(item: Item) = viewModelScope.launch(Dispatchers.IO) { itemDao.update(item) }

    fun delete(item: Item) = viewModelScope.launch(Dispatchers.IO) { itemDao.delete(item) }

    fun insertAll(items: List<Item>) =
            viewModelScope.launch(Dispatchers.IO) { itemDao.insertAll(items) }

    fun deleteAll(items: List<Item>) =
            viewModelScope.launch(Dispatchers.IO) { itemDao.deleteAll(items) }

    fun getItemById(id: Long) = itemDao.getItemById(id)

    fun getItemByBarcode(barcode: String) =
            viewModelScope.launch(Dispatchers.IO) { itemDao.getItemByBarcode(barcode) }

    fun getItemCount() = itemDao.getItemCount()

    fun getItemCountByType(typeId: Long) = itemDao.getItemCountByType(typeId)

//    fun getAllItemTypes() = allItemTypes

    fun setSorting(sortBy: String, sortOrder: String) {
        currentSortBy = sortBy
        currentSortOrder = sortOrder
        // Refresh the allItems Flow
        // Note: In a real app, you might want to use MutableStateFlow for currentSortBy and
        // currentSortOrder
        // and combine them with the Pager to automatically refresh when they change
    }

    // ItemType operations
    fun insertItemType(itemType: ItemType) =
            viewModelScope.launch(Dispatchers.IO) { itemTypeDao.insert(itemType) }

    fun updateItemType(itemType: ItemType) =
            viewModelScope.launch(Dispatchers.IO) { itemTypeDao.update(itemType) }

    fun deleteItemType(itemType: ItemType) =
            viewModelScope.launch(Dispatchers.IO) { itemTypeDao.delete(itemType) }

    fun getItemTypeById(id: Long) = itemTypeDao.getItemTypeById(id)

    fun getItemTypeByName(name: String) = itemTypeDao.getItemTypeByName(name)

    fun itemTypeExists(name: String) =
            viewModelScope.launch(Dispatchers.IO) { itemTypeDao.itemTypeExists(name) }

    fun getItemTypeCount() = itemTypeDao.getItemTypeCount()
}
