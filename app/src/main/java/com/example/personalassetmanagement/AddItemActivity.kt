package com.example.personalassetmanagement

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalassetmanagement.data.AssetDatabase
import com.example.personalassetmanagement.data.Item
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddItemActivity : AppCompatActivity() {

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var typeInputLayout: TextInputLayout
    private lateinit var typeAutoCompleteTextView: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_item_activity)

        nameInputLayout = findViewById(R.id.name_input_layout)
        typeInputLayout = findViewById(R.id.type_input_layout)
        typeAutoCompleteTextView = findViewById(R.id.type_autocomplete)

        val database = AssetDatabase.getDatabase(applicationContext)
        val itemDao = database.itemDao()
        val itemTypeDao = database.itemTypeDao()

        itemTypeDao.getAllItemTypes().observe(this) { itemTypes ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itemTypes.map { it.name })
            typeAutoCompleteTextView.setAdapter(adapter)
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.button_save).setOnClickListener {
            val name = nameInputLayout.editText?.text.toString().trim()
            val type = typeAutoCompleteTextView.text.toString().trim()

            if (validateInput(name, type)) {
                lifecycleScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            val newItem = Item(name = name, type = type)
                            itemDao.insert(newItem)
                        }
                        Toast.makeText(this@AddItemActivity, "Item saved successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddItemActivity, "Error saving item: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun validateInput(name: String, type: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            nameInputLayout.error = "Name cannot be empty"
            isValid = false
        } else {
            nameInputLayout.error = null
        }

        if (type.isEmpty()) {
            typeInputLayout.error = "Type cannot be empty"
            isValid = false
        } else {
            typeInputLayout.error = null
        }

        return isValid
    }
}