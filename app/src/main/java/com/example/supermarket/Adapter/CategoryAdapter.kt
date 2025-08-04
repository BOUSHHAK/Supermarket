package com.example.supermarket.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.supermarket.Activity.ProductsActivity
import com.example.supermarket.Model.CategoryModel
import com.example.supermarket.R

class CategoryAdapter(
    private val categoryList: List<CategoryModel>,
    private val context: Context
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.categoryName)
        val image: ImageView = view.findViewById(R.id.categoryImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.viewholder_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.name.text = category.title

        Glide.with(context).load(category.picUrl).into(holder.image)
        holder.image.visibility = View.VISIBLE

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductsActivity::class.java).apply {
                putExtra("categoryName", category.title)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = categoryList.size
}