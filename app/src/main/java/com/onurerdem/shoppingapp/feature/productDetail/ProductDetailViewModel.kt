package com.onurerdem.shoppingapp.feature.productDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.onurerdem.shoppingapp.data.model.ProductsItemDTO
import com.onurerdem.shoppingapp.data.remote.utils.DataState
import com.onurerdem.shoppingapp.domain.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firebase: FirebaseFirestore,
    private val state: SavedStateHandle
): ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailViewState>(ProductDetailViewState.Success(mutableListOf()))
    val uiState: StateFlow<ProductDetailViewState> = _uiState

    private val _uiEvent = MutableSharedFlow<ProductDetailViewEvent>(replay = 0)
    val uiEvent: SharedFlow<ProductDetailViewEvent> = _uiEvent

    init {
        getProducts()

    }


    private fun getProducts() {
        viewModelScope.launch {
            productsRepository.getAllProducts().collect {
                when(it) {
                    is DataState.Success -> {
                        _uiState.value = ProductDetailViewState.Success(it.data.map {
                            ProductsItemDTO(
                                id = it?.id,
                                title = it?.title,
                                price = it?.price,
                                description = it?.description,
                                category = it?.category,
                                image = it?.image,
                                rating = it?.rating,
                                isShoppingCart = false,
                                quantity = 0
                            )
                        }.toMutableList())
                    }
                    is DataState.Error -> {
                        _uiEvent.emit(ProductDetailViewEvent.ShowError(it.error?.status_message))
                    }
                    is DataState.Loading -> {
                        _uiState.value = ProductDetailViewState.Loading
                    }
                }
            }
        }
    }



}

sealed class ProductDetailViewEvent {
    data class ShowError(val message: String?) : ProductDetailViewEvent()
    object NavigateToDetail : ProductDetailViewEvent()

}

sealed class ProductDetailViewState {
    class Success(val products: MutableList<ProductsItemDTO>?) : ProductDetailViewState()
    object Loading : ProductDetailViewState()
}