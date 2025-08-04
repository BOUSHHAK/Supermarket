package com.example.supermarket.Activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supermarket.Adapter.ProductAdapter
import com.example.supermarket.Model.ProductsModel
import com.example.supermarket.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductsActivity : BaseActivity()  {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<ProductsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)
        setupBottomNavigation()

        val categoryName = intent.getStringExtra("categoryName") ?: ""
        val categoryTitle = findViewById<TextView>(R.id.categoryTitle)
        categoryTitle.text = categoryName

        recyclerView = findViewById(R.id.productsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        adapter = ProductAdapter(productList, this)
        recyclerView.adapter = adapter

        loadProductsByCategory(categoryName)
    }

    private fun loadProductsByCategory(categoryName: String) {
        FirebaseDatabase.getInstance().getReference("Items")
            .orderByChild("category")
            .equalTo(categoryName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productList.clear()
                    for (child in snapshot.children) {
                        val product = child.getValue(ProductsModel::class.java)
                        product?.let {
                            if (it.category == categoryName) {
                                productList.add(it)
                            }
                        }
                    }

                    if (productList.isEmpty()) {
                        Toast.makeText(this@ProductsActivity, "No products found in this category", Toast.LENGTH_SHORT).show()
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProductsActivity, "Failed to load products: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}