package com.example.personalassetmanagement.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.personalassetmanagement.data.AssetDatabase
import com.example.personalassetmanagement.data.Item
import com.example.personalassetmanagement.data.ItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database: AssetDatabase = AssetDatabase.getDatabase(application, viewModelScope)
    private val itemDao: ItemDao = database.itemDao()

    val allItems =
            Pager(PagingConfig(pageSize = 20, enablePlaceholders = true, maxSize = 100)) {
                        itemDao.getAllItemsWithType()
                    }
                    .flow
                    .cachedIn(viewModelScope)

    fun insert(item: Item) = viewModelScope.launch(Dispatchers.IO) { itemDao.insert(item) }

    fun update(item: Item) = viewModelScope.launch(Dispatchers.IO) { itemDao.update(item) }

    fun delete(item: Item) = viewModelScope.launch(Dispatchers.IO) { itemDao.delete(item) }
}
