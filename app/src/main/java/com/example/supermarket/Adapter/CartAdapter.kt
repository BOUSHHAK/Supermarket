package com.example.supermarket.Adapter

import com.example.supermarket.R
import com.example.supermarket.Model.CartModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.supermarket.Activity.CartActivity
import com.example.supermarket.Model.ProductsModel
import com.google.firebase.database.FirebaseDatabase

class CartAdapter(
    private val items: MutableList<CartModel>,
    private val context: Context,
    private val userEmail: String
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.productTitleCart)
        val price: TextView = itemView.findViewById(R.id.priceOfItem)
        val totalPrice: TextView = itemView.findViewById(R.id.totalPriceOfItem)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val image: ImageView = itemView.findViewById(R.id.productImageCart)
        val plusBtn: TextView = itemView.findViewById(R.id.plusBtn)
        val minusBtn: TextView = itemView.findViewById(R.id.minusBtn)
        val removeBtn: ImageView = itemView.findViewById(R.id.removeProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.viewholder_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = items[position]
        val userEmailEnc = userEmail.replace(".", ",")
        val cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userEmailEnc)

        FirebaseDatabase.getInstance().getReference("Items")
            .child(cartItem.productId)
            .get().addOnSuccessListener { snapshot ->
                val product = snapshot.getValue(ProductsModel::class.java)
                if (product != null) {
                    holder.title.text = product.title
                    holder.price.text = "${product.price}€"
                    holder.quantity.text = cartItem.quantity.toString()
                    holder.totalPrice.text = "${product.price * cartItem.quantity}€"
                    Glide.with(context).load(product.picUrl).into(holder.image)

                    holder.plusBtn.setOnClickListener {
                        val newQuantity = cartItem.quantity + 1
                        cartItem.quantity = newQuantity
                        cartRef.child(cartItem.productId).child("quantity").setValue(newQuantity)
                        notifyItemChanged(position)
                    }

                    holder.minusBtn.setOnClickListener {
                        if (cartItem.quantity > 1) {
                            val newQuantity = cartItem.quantity - 1
                            cartItem.quantity = newQuantity
                            cartRef.child(cartItem.productId).child("quantity").setValue(newQuantity)
                            notifyItemChanged(position)
                        }
                    }

                    holder.removeBtn.setOnClickListener {
                        cartRef.child(cartItem.productId).removeValue().addOnSuccessListener {
                            val itemIdToRemove = cartItem.productId
                            val indexToRemove = items.indexOfFirst { it.productId == itemIdToRemove }

                            if (indexToRemove != -1) {
                                items.removeAt(indexToRemove)
                                notifyItemRemoved(indexToRemove)
                                notifyItemRangeChanged(indexToRemove, items.size)

                                if (context is CartActivity) {
                                    context.totalPrice()
                                }
                            }
                        }
                    }
                }
            }

    }

    override fun getItemCount(): Int = items.size
}