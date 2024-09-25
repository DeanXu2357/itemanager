package com.example.personalassetmanagement

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.personalassetmanagement.data.AssetDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatisticsActivity : AppCompatActivity() {

    private lateinit var totalItemsTextView: TextView
    private lateinit var itemsPerTypeTextView: TextView
    private lateinit var mostCommonTypeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        totalItemsTextView = findViewById(R.id.textView_total_items)
        itemsPerTypeTextView = findViewById(R.id.textView_items_per_type)
        mostCommonTypeTextView = findViewById(R.id.textView_most_common_type)

        val database = AssetDatabase.getDatabase(applicationContext, lifecycleScope)
        val itemDao = database.itemDao()

        lifecycleScope.launch {
            val totalItems = withContext(Dispatchers.IO) {
                itemDao.getAllItemsWithTypeSorted("name", "ASC")
            }
            totalItemsTextView.text = getString(R.string.total_items, totalItems)

            val itemsPerType = withContext(Dispatchers.IO) {
                itemDao.getItemsPerType()
            }
            val itemsPerTypeText = itemsPerType.joinToString("\n") { "${it.type}: ${it.count}" }
            itemsPerTypeTextView.text = itemsPerTypeText

            val mostCommonType = itemsPerType.maxByOrNull { it.count }
            mostCommonTypeTextView.text = getString(R.string.most_common_type, mostCommonType?.type ?: "N/A")
        }
    }
}