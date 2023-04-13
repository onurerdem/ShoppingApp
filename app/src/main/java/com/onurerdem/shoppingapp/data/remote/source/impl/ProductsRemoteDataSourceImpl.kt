package com.onurerdem.shoppingapp.data.remote.source.impl

import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.data.model.Rating
import com.onurerdem.shoppingapp.data.remote.api.ProductsService
import com.onurerdem.shoppingapp.data.remote.source.ProductsRemoteDataSource
import com.onurerdem.shoppingapp.data.remote.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductsRemoteDataSourceImpl @Inject constructor(private val productsService: ProductsService) :
    BaseRemoteDataSource(), ProductsRemoteDataSource {
    override suspend fun getAllProducts(): Flow<DataState<ArrayList<ProductsItem>>> {
        return getResult { productsService.getAllProducts() }
    }
}