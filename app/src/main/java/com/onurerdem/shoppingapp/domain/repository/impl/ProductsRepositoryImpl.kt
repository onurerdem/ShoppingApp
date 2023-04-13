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
    override suspend fun getAllProducts(): Flow<DataState<ArrayList<ProductsItem>>> {
        return productsRemoteDataSource.getAllProducts()
    }
}