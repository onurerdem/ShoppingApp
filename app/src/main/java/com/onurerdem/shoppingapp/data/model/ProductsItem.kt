package com.onurerdem.shoppingapp.data.model


import com.google.gson.annotations.SerializedName

data class ProductsItem(
    @SerializedName("category")
    val category: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("rating")
    val rating: Rating?,
    @SerializedName("title")
    val title: String?,
)

data class ProductsItemDTO(
    val id: Int?,
    val title: String?,
    val price: Double?,
    val description: String?,
    val category: String?,
    val image: String?,
    val rating: Rating?,
    var isShoppingCart: Boolean = false
)