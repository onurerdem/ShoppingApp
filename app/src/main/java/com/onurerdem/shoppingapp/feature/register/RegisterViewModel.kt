package com.onurerdem.shoppingapp.feature.register

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.onurerdem.shoppingapp.R
import com.onurerdem.shoppingapp.data.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val dataStoreManager: DataStoreManager,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<RegisterViewEvent>(replay = 0)
    val uiEvent: SharedFlow<RegisterViewEvent> = _uiEvent

    @SuppressLint("StaticFieldLeak")
    val getContext = context

    fun register(email: String, password: String, confirmPassword: String, userName: String) {
        viewModelScope.launch {
            isValidFields(email, password, confirmPassword, userName)?.let {
                _uiEvent.emit(RegisterViewEvent.ShowError(it))
            } ?: kotlin.run {
                firebaseAuth.createUserWithEmailAndPassword(
                    email,
                    password
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        setUserName(userName, task.result.user?.uid)

                    } else {
                        viewModelScope.launch {
                            _uiEvent.emit(RegisterViewEvent.ShowError(task.exception?.message.toString()))
                        }
                    }
                }
            }
        }
    }

    private fun setUserName(userName: String, uuid: String?) {
        viewModelScope.launch {
            dataStoreManager.setUserName(userName)
            fireStore.collection("users").document(uuid.toString()).set("username" to userName)
                .addOnSuccessListener { documentReference ->
                    viewModelScope.launch { _uiEvent.emit(RegisterViewEvent.NavigateToMain) }
                }.addOnFailureListener { error ->
                    viewModelScope.launch {
                        _uiEvent.emit(RegisterViewEvent.ShowError(error.message.toString()))
                    }
                }
        }
    }

    private fun isValidFields(
        email: String,
        password: String,
        confirmPassword: String,
        userName: String
    ): String? {
        fun isValidEmptyField() =
            email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && userName.isNotEmpty()

        fun isValidConfirmPassword() = password == confirmPassword
        fun isValidPasswordLength() = password.length >= 6
        fun isValidEmail() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (isValidEmptyField().not()) {
            return getContext.getString(R.string.please_fill_all_fields)
        } else if (isValidEmail().not()) {
            return getContext.getString(R.string.please_enter_a_valid_email_address)
        } else if (isValidConfirmPassword().not()) {
            return getContext.getString(R.string.passwords_do_not_match)
        } else if (isValidPasswordLength().not()) {
            return getContext.getString(R.string.password_must_be_at_least_6_characters)
        }
        return null
    }
}

sealed class RegisterViewEvent {
    object NavigateToMain : RegisterViewEvent()
    class ShowError(val error: String) : RegisterViewEvent()
}