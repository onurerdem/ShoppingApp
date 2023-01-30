package com.onurerdem.shoppingapp.domain.repository

import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.data.model.Rating
import com.onurerdem.shoppingapp.data.remote.utils.DataState
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun getProductDetail(productId: Int): Flow<DataState<ProductsItem>>
    suspend fun getRateProduct(rate: Double): Flow<DataState<Rating>>
    suspend fun getAllProducts(): Flow<DataState<ArrayList<ProductsItem>>>
}