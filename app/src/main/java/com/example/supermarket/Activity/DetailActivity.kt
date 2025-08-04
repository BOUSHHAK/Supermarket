package com.example.supermarket.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.supermarket.Model.ProductsModel
import com.example.supermarket.R
import com.example.supermarket.Model.CartModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailActivity : BaseActivity() {
    private lateinit var title: TextView
    private lateinit var price: TextView
    private lateinit var image: ImageView
    private lateinit var backBtn: ImageView
    private lateinit var heartIcon: ImageView
    private lateinit var unit: TextView
    private lateinit var rating: TextView
    private lateinit var description: TextView
    private lateinit var addToCartBtn: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setupBottomNavigation()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        title = findViewById(R.id.titleTxt)
        price = findViewById(R.id.priceTxt)
        image = findViewById(R.id.imageDetail)
        unit = findViewById(R.id.unitTxt)
        rating = findViewById(R.id.ratingTxt)
        description = findViewById(R.id.descriptionTextView)
        addToCartBtn = findViewById(R.id.addToCartBtn)
        backBtn = findViewById(R.id.backBtn)
        heartIcon = findViewById(R.id.fav_icon)



        var productId = intent.getStringExtra("product_id") ?: ""
        var categoryName = intent.getStringExtra("categoryName") ?: ""


        if (productId.isNotEmpty()) {
            loadProductDetails(productId)
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        addToCartBtn.setOnClickListener {
            addToCart(productId)
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            intent.putExtra("categoryName", categoryName)
            startActivity(intent)
            finish()
        }
    }

    private fun loadProductDetails(productId: String) {
        val productRef = database.getReference("Items").child(productId)
        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.getValue(ProductsModel::class.java)
                    product?.let {
                        title.text = it.title
                        price.text = "${it.price}€"
                        unit.text = it.unit
                        rating.text = it.rating.toString()
                        description.text = it.description
                        Glide.with(this@DetailActivity).load(it.picUrl).into(image)

                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val userEmailEnc = currentUser.email!!.replace(".", ",")
                            val wishRef = database.getReference("WishList")
                                .child(userEmailEnc)
                                .child(product.id.toString())

                            wishRef.get().addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    heartIcon.setImageResource(R.drawable.red_fav_icon)
                                } else {
                                    heartIcon.setImageResource(R.drawable.fav_icon)
                                }
                            }

                            heartIcon.setOnClickListener {
                                wishRef.get().addOnSuccessListener { snapshot ->
                                    if (snapshot.exists()) {
                                        wishRef.removeValue().addOnSuccessListener {
                                            heartIcon.setImageResource(R.drawable.fav_icon)
                                        }
                                    } else {
                                        wishRef.setValue(product).addOnSuccessListener {
                                            heartIcon.setImageResource(R.drawable.red_fav_icon)
                                        }
                                    }
                                }.addOnFailureListener {
                                    Toast.makeText(this@DetailActivity, "Error accessing wishlist", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@DetailActivity, "Product not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailActivity, "Failed to load product", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun addToCart(productId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null && !currentUser.email.isNullOrEmpty()) {

            val userEmailEncoded = currentUser.email!!.replace(".", ",")

            val cartRef = database.getReference("Cart")
                .child(userEmailEncoded)
                .child(productId)

            cartRef.get().addOnSuccessListener { itemSnapshot ->
                val quantity = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                cartRef.setValue(CartModel(
                    productId = productId,
                    quantity = quantity + 1
                ))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to access cart", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}