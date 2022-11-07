package com.onurerdem.shoppingapp.domain.di

import com.google.firebase.auth.FirebaseAuth
import com.onurerdem.shoppingapp.domain.usecase.login.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

    @ViewModelScoped
    @Provides
    fun provideLoginUseCase(firebaseAuth: FirebaseAuth) = LoginUseCase(firebaseAuth)
}