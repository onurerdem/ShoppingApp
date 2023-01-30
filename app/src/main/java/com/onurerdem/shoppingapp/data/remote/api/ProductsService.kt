package com.onurerdem.shoppingapp.data.remote.api

import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.data.model.Rating
import retrofit2.Response
import retrofit2.http.GET

interface ProductsService {
    @GET("product")
    suspend fun getProducts(): Response<ProductsItem>

    @GET("rate")
    suspend fun getRates(): Response<Rating>

    @GET("products")
    suspend fun getAllProducts(): Response<ArrayList<ProductsItem>>
}