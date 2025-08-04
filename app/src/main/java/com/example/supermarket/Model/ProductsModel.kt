package com.example.supermarket.Model

data class ProductsModel(
    val id: Int = 0,
    var title: String = "",
    var description: String = "",
    var picUrl: String = "",
    var price: Double = 0.0,
    var category: String = "",
    var unit: String = "",
    var rating: Double = 0.0,
)