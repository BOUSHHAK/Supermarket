package com.example.supermarket.Activity

import com.example.supermarket.R
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supermarket.Adapter.CartAdapter
import com.example.supermarket.Model.CartModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartAdapter
    private val cartItems = mutableListOf<CartModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        setupBottomNavigation()

        recyclerView = findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email ?: return

        val userEmailEnc = userEmail.replace(".", ",")
        val cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userEmailEnc)

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                for (itemSnapshot in snapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartModel::class.java)
                    cartItem?.let { cartItems.add(it) }
                }
                adapter = CartAdapter(cartItems, this@CartActivity, userEmail)
                recyclerView.adapter = adapter
                totalPrice()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Failed to load cart", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun totalPrice() {
        var total = 0.0
        val itemsRef = FirebaseDatabase.getInstance().getReference("Items")
        val totalPriceTv = findViewById<TextView>(R.id.totalPriceText)

        if (cartItems.isEmpty()) {
            totalPriceTv.text = "0.00€"
            return
        }

        var loaded = 0
        for (item in cartItems) {
            itemsRef.child(item.productId).get().addOnSuccessListener { snapshot ->
                val price = snapshot.child("price").getValue(Double::class.java) ?: 0.0
                total += price * item.quantity
                loaded++
                if (loaded == cartItems.size) {
                    totalPriceTv.text = "%.2f€".format(total)
                }
            }
        }
    }
}