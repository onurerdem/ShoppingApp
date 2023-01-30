package com.onurerdem.shoppingapp.data.remote.source

import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.data.model.Rating
import com.onurerdem.shoppingapp.data.remote.utils.DataState
import kotlinx.coroutines.flow.Flow

interface ProductsRemoteDataSource {
    suspend fun getProductDetail(productId: Int): Flow<DataState<ProductsItem>>
    suspend fun getRateProducts(rate: Double): Flow<DataState<Rating>>
    suspend fun getAllProducts(): Flow<DataState<ArrayList<ProductsItem>>>
}