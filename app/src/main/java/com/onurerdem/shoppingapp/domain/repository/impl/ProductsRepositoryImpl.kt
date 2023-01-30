package com.onurerdem.shoppingapp.domain.repository.impl

import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.data.model.Rating
import com.onurerdem.shoppingapp.data.remote.source.ProductsRemoteDataSource
import com.onurerdem.shoppingapp.data.remote.utils.DataState
import com.onurerdem.shoppingapp.domain.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(private val productsRemoteDataSource: ProductsRemoteDataSource) :
    ProductsRepository {
    override suspend fun getProductDetail(productId: Int): Flow<DataState<ProductsItem>> {
        return productsRemoteDataSource.getProductDetail(productId)
    }

    override suspend fun getRateProduct(rate: Double): Flow<DataState<Rating>> {
        return productsRemoteDataSource.getRateProducts(rate)
    }

    override suspend fun getAllProducts(): Flow<DataState<ArrayList<ProductsItem>>> {
        return productsRemoteDataSource.getAllProducts()
    }
}