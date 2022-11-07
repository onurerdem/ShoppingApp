package com.onurerdem.shoppingapp.data.di

import com.onurerdem.shoppingapp.data.remote.api.ProductsService
import com.onurerdem.shoppingapp.data.remote.source.ProductsRemoteDataSource
import com.onurerdem.shoppingapp.data.remote.source.impl.ProductsRemoteDataSourceImpl
import com.onurerdem.shoppingapp.domain.repository.ProductsRepository
import com.onurerdem.shoppingapp.domain.repository.impl.ProductsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProductsModule {

    @Singleton
    @Provides
    fun provideMoviesService(retrofit: Retrofit) = retrofit.create(ProductsService::class.java)

    @Singleton
    @Provides
    fun provideProductsRemoteDataSource(productsService: ProductsService): ProductsRemoteDataSource =
        ProductsRemoteDataSourceImpl(productsService)

    @Singleton
    @Provides
    fun provideProductsRepository(moviesRemoteDataSource: ProductsRemoteDataSource): ProductsRepository =
        ProductsRepositoryImpl(moviesRemoteDataSource)
}