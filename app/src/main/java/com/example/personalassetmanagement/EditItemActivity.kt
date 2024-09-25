package com.example.personalassetmanagement

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.example.personalassetmanagement.data.AssetDatabase
import com.example.personalassetmanagement.data.Item
import com.example.personalassetmanagement.data.ItemDao
import com.example.personalassetmanagement.data.ItemTypeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditItemActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private fun initializeViews() {
        nameEditText = findViewById(R.id.edit_name)
        typeSpinner = findViewById(R.id.spinner_type)
        saveButton = findViewById(R.id.button_save)
        deleteButton = findViewById(R.id.button_delete)
    }

    private fun setupTypeSpinner(itemTypeDao: ItemTypeDao) {
        lifecycleScope.launch {
            itemTypeDao.getAllItemTypes().collect { itemTypes ->
                val adapter = ArrayAdapter(
                    this@EditItemActivity,
                    android.R.layout.simple_spinner_item,
                    itemTypes.map { it.name }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                typeSpinner.adapter = adapter
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_item_activity)

        initializeViews()
        val itemId = getItemIdFromIntent()
        if (itemId == -1) {
            showToastAndFinish("Error: Item not found")
            return
        }

        val database = AssetDatabase.getDatabase(applicationContext, lifecycleScope)
        val itemDao = database.itemDao()
        val itemTypeDao = database.itemTypeDao()

        setupTypeSpinner(itemTypeDao)
        populateFields(itemDao, itemId)

        saveButton.setOnClickListener { saveItem(itemDao, itemId) }
        deleteButton.setOnClickListener { deleteItem(itemDao, itemId) }
    }

    private fun getItemIdFromIntent(): Int {
        return intent.getIntExtra("ITEM_ID", -1)
    }

    private fun showToastAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun populateFields(itemDao: ItemDao, itemId: Int) {
        itemDao.getItemById(itemId.toLong()).observe(this@EditItemActivity) { item ->
            nameEditText.setText(item.name)
            typeSpinner.setSelection(
                (typeSpinner.adapter as ArrayAdapter<String>).getPosition(item.typeId.toString())
            )
        }
    }

    private fun saveItem(itemDao: ItemDao, itemId: Int) {
        val name = nameEditText.text.toString().trim()
        val type = typeSpinner.selectedItem?.toString() ?: ""
        val typeId = typeSpinner.selectedItemPosition.toLong()

        if (name.isNotBlank() && type.isNotBlank()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val updatedItem = Item(id = itemId.toLong(), name = name, typeId = typeId)
                itemDao.update(updatedItem)
                withContext(Dispatchers.Main) {
                    showToastAndFinish("Item updated")
                }
            }
        } else {
            showToast("Please fill all fields")
        }
    }

    private fun deleteItem(itemDao: ItemDao, itemId: Int) {
        itemDao.getItemById(itemId.toLong()).observe(this@EditItemActivity) { item ->
            if (item != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    itemDao.delete(item)
                    withContext(Dispatchers.Main) {
                        showToastAndFinish("Item deleted")
                    }
                }
            } else {
                showToast("Item not found")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}