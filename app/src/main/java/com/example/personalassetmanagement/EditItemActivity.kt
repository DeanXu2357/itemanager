package com.example.personalassetmanagement

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.example.personalassetmanagement.data.AssetDatabase
import com.example.personalassetmanagement.data.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditItemActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_item_activity)

        nameEditText = findViewById(R.id.edit_name)
        typeSpinner = findViewById(R.id.spinner_type)
        saveButton = findViewById(R.id.button_save)
        deleteButton = findViewById(R.id.button_delete)

        val itemId = intent.getIntExtra("ITEM_ID", -1)
        if (itemId == -1) {
            Toast.makeText(this, "Error: Item not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val database = AssetDatabase.getDatabase(applicationContext, lifecycleScope)
        val itemDao = database.itemDao()
        val itemTypeDao = database.itemTypeDao()
        itemTypeDao.getAllItemTypes().observe(this@EditItemActivity) { itemTypes ->
            val adapter =
                ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    itemTypes.map { it.name }
                )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = adapter

            itemDao.getItemById(itemId).observe(this@EditItemActivity) { item ->
                nameEditText.setText(item.name)
                typeSpinner.setSelection(itemTypes.indexOfFirst { it.name == item.type })
            }
        }
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val type = typeSpinner.selectedItem.toString() ? : ""

            if (name.isNotBlank() && type.isNotBlank()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val updatedItem = Item(id = itemId, name = name, type = type)
                    itemDao.update(updatedItem)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditItemActivity, "Item updated", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            val itemIdLong = itemId.toLong()
            itemDao.getItemById(itemIdLong)
                .observe(
                    this@EditItemActivity,
                    { item ->
                        if (item != null) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                itemDao.delete(item)
                                runOnUiThread {
                                    Toast.makeText(
                                        this@EditItemActivity,
                                        "Item deleted",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    finish()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )
        }
    }
}
