package com.example.personalassetmanagement

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.personalassetmanagement.data.Item
import com.example.personalassetmanagement.data.ItemType
import com.example.personalassetmanagement.ui.MainViewModel
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddItemActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var nameEditText: TextInputEditText
    private lateinit var typeSpinner: Spinner
    private lateinit var barcodeEditText: TextInputEditText
    private lateinit var itemTypes: List<ItemType>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        nameEditText = findViewById(R.id.nameEditText)
        typeSpinner = findViewById(R.id.typeSpinner)
        barcodeEditText = findViewById(R.id.barcodeEditText)

        lifecycleScope.launch {
            itemTypes = viewModel.allItemTypes.first()
            val itemTypeNames = itemTypes.map { it.name }
            val adapter =
                    ArrayAdapter(
                            this@AddItemActivity,
                            android.R.layout.simple_spinner_item,
                            itemTypeNames
                    )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = adapter
        }

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener { saveItem() }

        val scanBarcodeButton: Button = findViewById(R.id.scanBarcodeButton)
        scanBarcodeButton.setOnClickListener {
            // TODO: Implement barcode scanning functionality
            Toast.makeText(this, "Barcode scanning not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveItem() {
        val name = nameEditText.text.toString().trim()
        val selectedTypePosition = typeSpinner.selectedItemPosition
        val barcode = barcodeEditText.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an item name", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedTypePosition < 0 || selectedTypePosition >= itemTypes.size) {
            Toast.makeText(this, "Please select a valid item type", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedType = itemTypes[selectedTypePosition]

        val newItem =
                Item(
                        name = name,
                        typeId = selectedType.id,
                        barcode = if (barcode.isNotEmpty()) barcode else null,
                        dateAdded = Date(),
                        dateModified = Date()
                )

        viewModel.insert(newItem)
        Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}
