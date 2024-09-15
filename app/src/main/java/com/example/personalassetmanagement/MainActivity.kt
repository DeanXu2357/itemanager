gpackage com.example.personalassetmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.personalassetmanagement.data.AssetDatabase
import com.example.personalassetmanagement.data.Item
import com.example.personalassetmanagement.data.ItemType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var sortSpinner: AutoCompleteTextView
    private lateinit var filterSpinner: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        recyclerView = findViewById(R.id.recyclerview)
        fab = findViewById(R.id.fab)
        sortSpinner = findViewById(R.id.spinner_sort)
        filterSpinner = findViewById(R.id.spinner_filter)

        val adapter = ItemListAdapter { item ->
            val intent = Intent(this, EditItemActivity::class.java)
            intent.putExtra("ITEM_ID", item.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val database = AssetDatabase.getDatabase(applicationContext)
        val itemDao = database.itemDao()
        val itemTypeDao = database.itemTypeDao()

        // Set up sort spinner
        val sortOptions = arrayOf("Name", "Type")
        val sortAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sortOptions)
        sortSpinner.setAdapter(sortAdapter)

        // Set up filter spinner
        itemTypeDao.getAllItemTypes().observe(this) { itemTypes ->
            val filterOptions = mutableListOf("All")
            filterOptions.addAll(itemTypes.map { it.name })
            val filterAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, filterOptions)
            filterSpinner.setAdapter(filterAdapter)
        }

        // Observe changes in sort and filter spinners
        sortSpinner.setOnItemClickListener { _, _, _, _ -> updateItemList() }
        filterSpinner.setOnItemClickListener { _, _, _, _ -> updateItemList() }

        fab.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        updateItemList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_statistics -> {
                val intent = Intent(this, StatisticsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateItemList() {
        val database = AssetDatabase.getDatabase(applicationContext)
        val itemDao = database.itemDao()

        val sortBy = sortSpinner.text.toString()
        val filterBy = filterSpinner.text.toString()

        val pagingSource = when {
            filterBy == "All" && sortBy == "Name" -> itemDao.getAllItemsSortedByName()
            filterBy == "All" && sortBy == "Type" -> itemDao.getAllItemsSortedByType()
            filterBy != "All" && sortBy == "Name" -> itemDao.getItemsByType(filterBy)
            filterBy != "All" && sortBy == "Type" -> itemDao.getItemsByType(filterBy)
            else -> itemDao.getAllItemsSortedByName()
        }

        val pager = Pager(PagingConfig(pageSize = 20)) {
            pagingSource
        }

        lifecycleScope.launch {
            pager.flow.collectLatest { pagingData ->
                (recyclerView.adapter as ItemListAdapter).submitData(pagingData)
            }
        }
    }
}

class ItemListAdapter(private val onItemClick: (Item) -> Unit) :
    PagingDataAdapter<Item, ItemListAdapter.ItemViewHolder>(ITEM_COMPARATOR) {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: android.widget.TextView = itemView.findViewById(R.id.textView_name)
        val typeView: android.widget.TextView = itemView.findViewById(R.id.textView_type)

        init {
            itemView.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { item ->
                    onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        if (current != null) {
            holder.nameView.text = current.name
            holder.typeView.text = current.type
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }
}