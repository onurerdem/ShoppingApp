package com.onurerdem.shoppingapp.feature.shoppingCart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.onurerdem.shoppingapp.R
import com.onurerdem.shoppingapp.data.model.ProductsItemDTO
import com.onurerdem.shoppingapp.databinding.FragmentShoppingCartBinding
import com.onurerdem.shoppingapp.feature.home.adapter.HomeProductAdapter
import com.onurerdem.shoppingapp.feature.home.adapter.OnShoppingCartClickListener
import com.onurerdem.shoppingapp.utils.BindingAdapters.round
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingCartFragment : Fragment(), OnShoppingCartClickListener {
    private lateinit var binding: FragmentShoppingCartBinding
    private val viewModel by viewModels<ShoppingCartViewModel>()
    private var navController: NavController? = null
    private val firestore = Firebase.firestore
    private val firebaseAuth = Firebase.auth

    private var userId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getShoppingCartList()

        binding.cartToolbar.title = requireContext().getString(R.string.shopping_cart_product)

        navController = findNavController()

        firebaseAuth.currentUser?.uid?.let {
            userId = it
        }

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    when (it) {
                        is ShoppingCartViewState.Success -> {
                            binding.rvShoppingCart.adapter =
                                HomeProductAdapter(this@ShoppingCartFragment).apply {
                                    submitList(it.products)
                                }
                        }
                        is ShoppingCartViewState.Loading -> {

                        }
                    }
                }
            }

            launch {
                viewModel.uiEvent.collect {
                    when (it) {
                        is ShoppingCartViewEvent.ShowError -> {
                            Snackbar.make(
                                binding.root,
                                it.message.toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is ShoppingCartViewEvent.NavigateToDetail -> {

                        }
                        else -> {}
                    }
                }
            }

        }

        binding.btPurchase.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

            builder.setMessage(requireContext().getString(R.string.are_you_sure_you_want_to_complete_order))
                .setTitle(requireContext().getString(R.string.complete_order))

            builder.apply {
                setPositiveButton(requireContext().getString(R.string.yes)) { dialog, id ->
                    firestore.collection("shoppingCartProduct").document(userId).collection("product").get().addOnSuccessListener {
                        it.documents.forEach(){
                            it.reference.delete()
                        }
                    navController!!.navigate(R.id.action_shoppingCartFragment_to_homeFragment)
                    }
                    Toast.makeText(requireContext(), requireContext().getString(R.string.purchasing_is_successfull), Toast.LENGTH_LONG).show()
                }
                setNegativeButton(requireContext().getString(R.string.no)) { dialog, id ->
                    Toast.makeText(requireContext(), requireContext().getString(R.string.you_can_continue_shopping), Toast.LENGTH_LONG).show()
                }
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        calculateTotalPrice {
            val totalPrice = it.round(2)
            binding.tvShoppinCartTotalPrice.text = requireContext().getString(R.string.total_price) + ": $totalPrice $"
        }
    }

    override fun onAddShoppingCartClick(id: Int?) {
        TODO("Not yet implemented")
    }

    override fun onRemoveShoppingCartClick(productsItem: ProductsItemDTO) {
        viewModel.onShoppingCartProduct(productsItem)
        calculateTotalPrice {
            val totalPrice = it.round(2)
            binding.tvShoppinCartTotalPrice.text = requireContext().getString(R.string.total_price) + ": $totalPrice $"
        }
        navController!!.navigate(R.id.homeFragment)
        navController!!.navigate(R.id.shoppingCartFragment)
    }

    override fun onProductClick(product: ProductsItemDTO) {
        navController?.navigate(R.id.action_shoppingCartFragment_to_productDetailFragment,Bundle().apply {
            putString("productId", product.id.toString())
            putString("productImage",product.image.toString())
            putString("productTitle",product.title)
            putString("productCategory",product.category.toString())
            putString("productDescription",product.description.toString())
            putDouble("productPrice",product.price as Double)
            putString("productIsShoppingCart",product.isShoppingCart.toString())
            putString("productQuantity",product.quantity.toString())
            putString("productRating",product.rating.toString())

        })
    }

    fun getUserShoppingCart(shoppingCartList: (List<ShoppingCartItemModel>) -> Unit){
        Firebase.firestore.collection("shoppingCartProduct").document(userId).collection("product").get()
            .addOnSuccessListener {
                val docRef = it
                val shoppingCartList = docRef.documents.map {
                    ShoppingCartItemModel(
                        id = it.get("id").toString(),
                        quantity = it.get("quantity").toString().toDouble(),
                        price = it.get("price") as Double
                    )
                }
                shoppingCartList(shoppingCartList)
            }.addOnFailureListener {
                println(requireContext().getString(R.string.localized_message) + ": ${it.localizedMessage}")
            }
    }

    data class ShoppingCartItemModel(val id:String = "", val quantity: Double = 0.0, val price: Double = 0.0)

    fun calculateTotalPrice(totalPrice: (Double) -> Unit){
        getUserShoppingCart { list ->
            var totalPrice = 0.0
            totalPrice = list.sumOf { it.price * it.quantity }
            totalPrice(totalPrice)
        }
    }
}