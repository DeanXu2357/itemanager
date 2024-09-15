package com.example.personalassetmanagement

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalassetmanagement.data.AssetDatabase
import com.example.personalassetmanagement.data.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        val database = AssetDatabase.getDatabase(applicationContext)
        val itemDao = database.itemDao()
        val itemTypeDao = database.itemTypeDao()

        itemTypeDao.getAllItemTypes().observe(this) { itemTypes ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemTypes.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = adapter

            itemDao.getItemById(itemId).observe(this) { item ->
                nameEditText.setText(item.name)
                typeSpinner.setSelection(itemTypes.indexOfFirst { it.name == item.type })
            }
        }

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val type = typeSpinner.selectedItem as String

            if (name.isNotEmpty() && type.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val updatedItem = Item(id = itemId, name = name, type = type)
                    itemDao.update(updatedItem)
                }
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val item = itemDao.getItemById(itemId).value
                if (item != null) {
                    itemDao.delete(item)
                }
            }
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}