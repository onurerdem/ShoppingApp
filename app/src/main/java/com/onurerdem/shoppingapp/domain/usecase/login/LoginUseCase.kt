package com.onurerdem.shoppingapp.domain.usecase.login

import com.google.firebase.auth.FirebaseAuth
import com.onurerdem.shoppingapp.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    BaseUseCase<LoginUseCaseParams, LoginUseCaseState> {
    override fun invoke(params: LoginUseCaseParams): Flow<LoginUseCaseState> {
        return flow {
            emit(LoginUseCaseState.Loading)
            sign(params.email, params.password).collect {
                emit(it)
            }
        }
    }

    private fun sign(email: String, password: String) = callbackFlow {
        val callback = firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySendBlocking(LoginUseCaseState.Success)
            }.addOnFailureListener {
                trySendBlocking(LoginUseCaseState.Error(it.message))
            }
        awaitClose { callback.isCanceled() }
    }
}


data class LoginUseCaseParams(
    val email: String,
    val password: String
)

sealed class LoginUseCaseState {
    object Success : LoginUseCaseState()
    data class Error(val error: String?) : LoginUseCaseState()
    object Loading : LoginUseCaseState()
}