package com.onurerdem.shoppingapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.onurerdem.shoppingapp.data.model.ProductsItemDTO
import com.onurerdem.shoppingapp.data.remote.utils.DataState
import com.onurerdem.shoppingapp.domain.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) :
    ViewModel() {
    private val _uiState =
        MutableStateFlow<HomeViewState>(
            HomeViewState.Success(
                mutableListOf(),
                mutableListOf()
            )
        )
    val uiState: StateFlow<HomeViewState> = _uiState

    private val _uiEvent = MutableSharedFlow<HomeViewEvent>(replay = 0)
    val uiEvent: SharedFlow<HomeViewEvent> = _uiEvent

    init {
        getProducts()
    }

    private fun getProducts() {
        viewModelScope.launch {
            productsRepository.getAllProducts().collect {
                when (it) {
                    is DataState.Success -> {
                        _uiState.value = HomeViewState.Success(it.data.map {
                            val data = getShoppingCartList(it?.id).first()

                            ProductsItemDTO(
                                id = it?.id,
                                title = it?.title,
                                image = it?.image,
                                description = it?.description,
                                category = it?.category,
                                price = it?.price,
                                rating = it?.rating,
                                isShoppingCart = data?.find { c -> c == it?.id.toString() } != null,
                                quantity = 1
                            )
                        }?.toMutableList(), mutableListOf())
                    }
                    is DataState.Error -> {
                        _uiEvent.emit(HomeViewEvent.ShowError(it.error?.status_message))
                    }
                    is DataState.Loading -> {
                        _uiState.value = HomeViewState.Loading
                    }
                    else -> {}
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

    fun onShoppingCartProduct(data: ProductsItemDTO) {
        viewModelScope.launch {
            val userId = firebaseAuth.currentUser?.uid
            if (data.isShoppingCart) {
                deleteProduct(userId.toString(), data.id)
            } else {
                insertProduct(userId.toString(), data)
            }

        }
    }

    private fun insertProduct(userId: String, data: ProductsItemDTO) {
        fireStore.collection("shoppingCartProduct").document(userId.toString()).collection("product")
            .let { ref ->
                ref.document("${data.id}")
                    .set(
                        mapOf(
                            "id" to "${data.id}".toLong(),
                            "title" to data.title,
                            "price" to data.price,
                            "image" to data.image,
                            "description" to data.description,
                            "isShoppingCart" to data.isShoppingCart,
                            "category" to data.category,
                            "rating" to data.rating,
                            "quantity" to data.quantity
                        )
                    )

                    .addOnSuccessListener { documentReference ->
                        viewModelScope.launch {
                            _uiState.value =
                                HomeViewState.Success((_uiState.value as HomeViewState.Success).data?.map { safeList ->
                                    if (safeList?.id == data.id) {
                                        safeList.isShoppingCart = true
                                    }
                                    safeList
                                }?.toMutableList(), mutableListOf())

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
                                HomeViewState.Success((_uiState.value as HomeViewState.Success).data?.map { safeList ->
                                    if (safeList?.id == id) {
                                        safeList.isShoppingCart = false
                                    }
                                    safeList
                                }?.toMutableList(), mutableListOf())

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

    fun searchProduct(query: String) {
        viewModelScope.launch {
            val updateQuery = query.lowercase(Locale.getDefault())

            val currentData = (_uiState.value as HomeViewState.Success).data
            if (updateQuery != "") {
                currentData?.let {
                    val filteredList = it.filter {
                        it.title?.lowercase(Locale.getDefault())?.contains(updateQuery) ?: false
                    }
                    _uiState.value =
                        HomeViewState.Success(currentData, filteredList.toMutableList())
                }
            } else {
                _uiState.value =
                    currentData?.let { HomeViewState.Success(it, mutableListOf()) }!!
            }
        }
    }
}

sealed class HomeViewEvent {
    data class ShowError(val message: String?) : HomeViewEvent()
    object NavigateToDetail : HomeViewEvent()
}

sealed class HomeViewState {
    class Success(val data: MutableList<ProductsItemDTO>?,
                  val filteredData: MutableList<ProductsItemDTO>) : HomeViewState()
    object Loading : HomeViewState()
    data class Error(val message: String?) : HomeViewState()
}