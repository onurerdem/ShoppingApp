package com.onurerdem.shoppingapp.feature.shoppingCart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.onurerdem.shoppingapp.data.model.ProductsItemDTO
import com.onurerdem.shoppingapp.data.model.Rating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
    private val _uiShoppingState = MutableStateFlow<ShoppingCartViewState>(ShoppingCartViewState.SuccessShopping(mutableListOf()))
    val uiShoppingState: StateFlow<ShoppingCartViewState> = _uiShoppingState

    private val _uiEvent = MutableSharedFlow<ShoppingCartViewEvent>(replay = 0)
    val uiEvent: SharedFlow<ShoppingCartViewEvent> = _uiEvent

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
                        ProductsItemDTO(
                            id = (it.get("id") as Long?)?.toInt(),
                            title = it.get("title") as String?,
                            image = it.get("image") as String?,
                            description = it.get("description") as String?,
                            isShoppingCart = true,
                            category = it.get("category") as String?,
                            price = it.get("price") as Double?,
                            rating = it.get("rating").let {
                                val splitArray = it.toString().substring(1,it.toString().length - 1).removePrefix("rate=").replace("count=","").replace(" ","").replace(" ","")
                                val rate = splitArray.get(0).code.toDouble()
                                val count = splitArray.get(1).code
                                ToRating(rate, count)
                            } as Rating?,
                            quantity = (it.get("quantity") as Long).toInt()
                        )
                    }
                    _uiState.value =
                        ShoppingCartViewState.Success(list.toMutableList(), mutableListOf())
                }
            }
                .addOnFailureListener {}
        }
    }

    fun onShoppingCartProduct(data: ProductsItemDTO) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (data.isShoppingCart) {
                deleteProduct(userId.toString(), data.id)
            }
        }
    }

    private fun deleteProduct(userId: String, id: Int?) {
        fireStore.collection("shoppingCartProduct").document(userId.toString()).collection("product")
            .let { ref ->
                ref.document("$id")
                    .delete()
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            _uiShoppingState.value =
                                ShoppingCartViewState.SuccessShopping((_uiShoppingState.value as ShoppingCartViewState.SuccessShopping).products?.map { safeList ->
                                    if (safeList?.id == id) {
                                        safeList.isShoppingCart = false
                                    }
                                    safeList
                                }?.toMutableList())

                            _uiEvent.emit(ShoppingCartViewEvent.ShowError("Product deleted from shopping cart."))

                        }
                    }
                    .addOnFailureListener { error ->
                        viewModelScope.launch {
                            _uiEvent.emit(ShoppingCartViewEvent.ShowError(error.message.toString()))

                        }
                    }
            }
    }
    fun ToRating(rate: Double,count: Int): Rating {
        return Rating(count,rate)
    }
}

sealed class ShoppingCartViewEvent {
    data class ShowError(val message: String?) : ShoppingCartViewEvent()
    object NavigateToDetail : ShoppingCartViewEvent()
}

sealed class ShoppingCartViewState {
    data class Success(
        val data: MutableList<ProductsItemDTO>,
        val filteredData: MutableList<ProductsItemDTO>
    ) : ShoppingCartViewState()

    class SuccessShopping(val products: MutableList<ProductsItemDTO>?) : ShoppingCartViewState()

    object Loading : ShoppingCartViewState()
    data class Error(val message: String?) : ShoppingCartViewState()
}