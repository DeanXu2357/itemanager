package com.example.personalassetmanagement.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.personalassetmanagement.data.AssetDatabase
import com.example.personalassetmanagement.data.ItemType
import com.example.personalassetmanagement.data.ItemTypeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val itemTypeDao: ItemTypeDao
    val allCategories: LiveData<List<ItemType>>

    init {
        val database = AssetDatabase.getDatabase(application, viewModelScope)
        itemTypeDao = database.itemTypeDao()
        allCategories = itemTypeDao.getAllItemTypes()
    }

    fun addCategory(category: ItemType) = viewModelScope.launch(Dispatchers.IO) {
        itemTypeDao.insert(category)
    }

    fun updateCategory(category: ItemType) = viewModelScope.launch(Dispatchers.IO) {
        itemTypeDao.update(category)
    }

    fun deleteCategory(category: ItemType) = viewModelScope.launch(Dispatchers.IO) {
        itemTypeDao.delete(category)
    }
}