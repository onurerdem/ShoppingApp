package com.onurerdem.shoppingapp.feature.shoppingcart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.data.model.Rating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ShoppingCartViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<ShoppingCartViewState>(
            ShoppingCartViewState.Success(
                mutableListOf(),
                mutableListOf()
            )
        )
    val uiState: StateFlow<ShoppingCartViewState> = _uiState

    init {
        getShoppingCartList()
    }

    fun getShoppingCartList() {
        viewModelScope.launch {
            val id = auth.currentUser?.uid
            val docRef =
                fireStore.collection("shoppingCartProduct").document(id.toString()).collection("product")
                    .get()
            docRef.addOnSuccessListener { result ->
                if (result.isEmpty.not()) {
                    val list = result.documents.map {
                        ProductsItem(
                            id = it.get("id") as Int?,
                            title = it.get("title") as String?,
                            image = it.get("image") as String?,
                            description = it.get("description") as String?,
                            isShoppingCart = true,
                            category = it.get("category") as String?,
                            price = it.get("price") as Double?,
                            rating = it.get("rating") as Rating?,
                            results = it.get("results") as List<ProductsItem?>?
                        )
                    }
                    _uiState.value =
                        ShoppingCartViewState.Success(list.toMutableList(), mutableListOf())
                }
            }
                .addOnFailureListener {}
        }
    }

    fun searchMovie(query: String) {
        viewModelScope.launch {
            val updateQuery = query.lowercase(Locale.getDefault())

            val currentData = (_uiState.value as ShoppingCartViewState.Success).data
            if (updateQuery != "") {
                currentData.let {
                    val filteredList = it.filter {
                        it.title?.lowercase(Locale.getDefault())?.contains(updateQuery) ?: false
                    }
                    _uiState.value =
                        ShoppingCartViewState.Success(currentData, filteredList.toMutableList())
                }
            } else {
                _uiState.value =
                    ShoppingCartViewState.Success(currentData, mutableListOf())
            }
        }
    }
}

sealed class ShoppingCartViewState {
    data class Success(
        val data: MutableList<ProductsItem>,
        val filteredData: MutableList<ProductsItem>
    ) : ShoppingCartViewState()

    object Loading : ShoppingCartViewState()
    data class Error(val message: String?) : ShoppingCartViewState()
}