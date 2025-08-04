package com.example.supermarket.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.supermarket.Activity.DetailActivity
import com.example.supermarket.R
import com.example.supermarket.Model.ProductsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProductAdapter(
    private val productList: List<ProductsModel>,
    private val context: Context
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val prodTitle: TextView = view.findViewById(R.id.titleTxt)
        val image: ImageView = view.findViewById(R.id.productImageView)
        val price: TextView = view.findViewById(R.id.priceTxt)
        val rating: TextView = view.findViewById(R.id.ratingTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.viewholder_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.prodTitle.text = product.title
        holder.price.text = "${product.price}€"
        holder.rating.text = product.rating?.toString() ?: "0.0"
        Glide.with(context).load(product.picUrl).into(holder.image)

        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val currentUser = auth.currentUser
        val heartIcon = holder.itemView.findViewById<ImageView>(R.id.fav_icon)

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
                        wishRef.removeValue()
                        heartIcon.setImageResource(R.drawable.fav_icon)
                    } else {
                        wishRef.setValue(product)
                        heartIcon.setImageResource(R.drawable.red_fav_icon)
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Error accessing wishlist", Toast.LENGTH_SHORT).show()
                }
            }
        }
        holder.image.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("product_id", product.id.toString())
            intent.putExtra("categoryName", product.category)

            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int = productList.size
}