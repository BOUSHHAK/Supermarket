package com.example.supermarket.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.supermarket.Activity.MainActivity
import com.example.supermarket.Model.DiscountModel
import com.example.supermarket.R

class DiscountAdapter(
    private var discountItems: List<DiscountModel>,
    private val context: Context
) : RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder>() {

    class DiscountViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.discountViewPager)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.slider_image_container, parent, false)
        return DiscountViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscountViewHolder, position: Int) {
        val discount = discountItems[position]

        Glide.with(context).load(discount.url).into(holder.image)
        holder.image.visibility = View.VISIBLE

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("discountName", discount.url)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = discountItems.size


}