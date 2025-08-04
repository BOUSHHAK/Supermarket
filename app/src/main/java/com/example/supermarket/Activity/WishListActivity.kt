package com.example.supermarket.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.supermarket.R
import androidx.recyclerview.widget.RecyclerView
import com.example.supermarket.Adapter.ProductAdapter
import com.example.supermarket.Model.ProductsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class WishListActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val wishlistProducts = mutableListOf<ProductsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)
        setupBottomNavigation()

        recyclerView = findViewById(R.id.wishlistRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email ?: return

        val userEmailEnc = userEmail.replace(".", ",")
        val wishlistRef = FirebaseDatabase.getInstance().getReference("WishList").child(userEmailEnc)

        wishlistRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                wishlistProducts.clear()
                for (itemSnapshot in snapshot.children) {
                    val product = itemSnapshot.getValue(ProductsModel::class.java)
                    product?.let { wishlistProducts.add(it) }
                }
                adapter = ProductAdapter(wishlistProducts, this@WishListActivity)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WishListActivity, "Failed to load Wish List", Toast.LENGTH_SHORT).show()
            }
        })
    }
}