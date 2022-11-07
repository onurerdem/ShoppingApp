package com.onurerdem.shoppingapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.data.remote.utils.DataState
import com.onurerdem.shoppingapp.domain.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) :
    ViewModel() {
    private val _uiState = MutableStateFlow<HomeViewState>(HomeViewState.Success(mutableListOf()))
    val uiState: StateFlow<HomeViewState> = _uiState

    private val _uiEvent = MutableSharedFlow<HomeViewEvent>(replay = 0)
    val uiEvent: SharedFlow<HomeViewEvent> = _uiEvent

    init {
        getProducts()
    }

    private fun getProducts() {
        viewModelScope.launch {
            productsRepository.getProductDetail(1).collect {
                when (it) {
                    is DataState.Success -> {
                        _uiState.value = HomeViewState.Success(it.data.results?.map {
                            val data = getShoppingCartList(it?.id).first()

                            ProductsItem(
                                id = it?.id,
                                title = it?.title,
                                image = it?.image,
                                description = it?.description,
                                category = it?.category,
                                price = it?.price,
                                rating = it?.rating,
                                results = it?.results,
                                isShoppingCart = data?.find { c -> c == it?.id.toString() } != null
                            )
                        }?.toMutableList())
                    }
                    is DataState.Error -> {
                        _uiEvent.emit(HomeViewEvent.ShowError(it.error?.status_message))
                    }
                    is DataState.Loading -> {
                        _uiState.value = HomeViewState.Loading
                    }
                }

            }
        }
    }

    private fun getShoppingCartList(id: Int?): Flow<MutableList<String>?> = channelFlow {
        val shoppingCartList = mutableListOf<String>()
        val callBack =
            fireStore.collection("shoppingCartProduct").document(firebaseAuth.currentUser?.uid.toString())
                .collection("product").document(id.toString()).get().addOnSuccessListener {
                    it.data?.values?.forEach { data ->
                        shoppingCartList.add(data.toString())
                    }
                    trySendBlocking(shoppingCartList)

                }.addOnFailureListener {
                    trySendBlocking(mutableListOf())
                }
        awaitClose { callBack.isCanceled() }
    }

    fun onShoppingCartProduct(data: ProductsItem) {
        viewModelScope.launch {
            val userId = firebaseAuth.currentUser?.uid
            if (data.isShoppingCart) {
                deleteProduct(userId.toString(), data.id)
            } else {
                insertProduct(userId.toString(), data)
            }

        }
    }

    private fun insertProduct(userId: String, data: ProductsItem) {
        fireStore.collection("shoppingCartProduct").document(userId.toString()).collection("Product")
            .let { ref ->
                ref.document("${data.id}")
                    .set(
                        mapOf(
                            "productId" to "${data.id}",
                            "title" to data.title,
                            "price" to data.price,
                            "image" to data.image
                        )
                    )

                    .addOnSuccessListener { documentReference ->
                        viewModelScope.launch {
                            _uiState.value =
                                HomeViewState.Success((_uiState.value as HomeViewState.Success).products?.map { safeList ->
                                    if (safeList?.id == data.id) {
                                        safeList.isShoppingCart = true
                                    }
                                    safeList
                                }?.toMutableList())

                            _uiEvent.emit(HomeViewEvent.ShowError("Product added to shopping cart."))

                        }
                    }
                    .addOnFailureListener { error ->
                        viewModelScope.launch {
                            _uiEvent.emit(HomeViewEvent.ShowError(error.message.toString()))

                        }
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
                            _uiState.value =
                                HomeViewState.Success((_uiState.value as HomeViewState.Success).products?.map { safeList ->
                                    if (safeList?.id == id) {
                                        safeList.isShoppingCart = false
                                    }
                                    safeList
                                }?.toMutableList())

                            _uiEvent.emit(HomeViewEvent.ShowError("Product deleted from shopping cart."))

                        }
                    }
                    .addOnFailureListener { error ->
                        viewModelScope.launch {
                            _uiEvent.emit(HomeViewEvent.ShowError(error.message.toString()))

                        }
                    }
            }
    }
}

sealed class HomeViewEvent {
    data class ShowError(val message: String?) : HomeViewEvent()
}

sealed class HomeViewState {
    class Success(val products: MutableList<ProductsItem>?) : HomeViewState()
    object Loading : HomeViewState()
}