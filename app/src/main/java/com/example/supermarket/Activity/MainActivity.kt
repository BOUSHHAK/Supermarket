package com.example.supermarket.Activity

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.supermarket.Adapter.CategoryAdapter
import com.example.supermarket.Adapter.DiscountAdapter
import com.example.supermarket.Model.CategoryModel
import com.example.supermarket.Model.DiscountModel
import com.example.supermarket.R
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class MainActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private val categoryList = mutableListOf<CategoryModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigation()
        initCategories()
        initDiscounts()
    }

    private fun initCategories() {
        recyclerView = findViewById(R.id.viewCategory)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarCategory)

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        categoryAdapter = CategoryAdapter(categoryList, this)
        recyclerView.adapter = categoryAdapter

        FirebaseDatabase.getInstance().getReference("Category")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    categoryList.clear()
                    for (child in snapshot.children) {
                        val category = child.getValue(CategoryModel::class.java)
                        val keyAsInt = child.key?.toIntOrNull() ?: 0
                        category?.let {
                            categoryList.add(it.copy(id = keyAsInt))
                        }
                    }
                    categoryAdapter.notifyDataSetChanged()

                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun initDiscounts(){
        val viewPagerSlider = findViewById<ViewPager2>(R.id.discountViewPager)
        val dotIndicator = findViewById<DotsIndicator>(R.id.dotIndicator)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarDiscount)

        progressBar.visibility = View.VISIBLE
        viewPagerSlider.visibility = View.GONE

        FirebaseDatabase.getInstance().getReference("Banner")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val images = mutableListOf<DiscountModel>()
                    for (child in snapshot.children) {
                        child.getValue(DiscountModel::class.java)?.let {
                            images.add(it)
                        }
                    }

                    viewPagerSlider.adapter = DiscountAdapter(images, this@MainActivity)

                    viewPagerSlider.clipToPadding = false
                    viewPagerSlider.clipChildren = false
                    viewPagerSlider.offscreenPageLimit = 3
                    viewPagerSlider.getChildAt(0)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

                    val compositePageTransformer = CompositePageTransformer().apply {
                        addTransformer(MarginPageTransformer(40))
                    }
                    viewPagerSlider.setPageTransformer(compositePageTransformer)

                    if (images.size > 1) {
                        dotIndicator.visibility = View.VISIBLE
                        dotIndicator.attachTo(viewPagerSlider)
                    }

                    progressBar.visibility = View.GONE
                    viewPagerSlider.visibility = View.VISIBLE
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
}