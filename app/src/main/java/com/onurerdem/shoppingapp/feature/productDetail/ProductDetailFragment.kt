package com.onurerdem.shoppingapp.feature.productDetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.onurerdem.shoppingapp.R
import com.onurerdem.shoppingapp.data.model.Rating
import com.onurerdem.shoppingapp.databinding.FragmentProductDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val viewModel by viewModels<ProductDetailViewModel>()
    private lateinit var navController: NavController
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    var totalPrice: Float = 0F
    var quantity = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        firebaseAuth = Firebase.auth
        firestore = Firebase.firestore
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val userId = firebaseAuth.currentUser?.uid.toString()

        val productId = arguments?.getString("productId")?.toInt()
        val productTitle = arguments?.getString("productTitle")
        val productImage = arguments?.getString("productImage")
        val productCategory = arguments?.getString("productCategory")
        val productDescription = arguments?.getString("productDescription")
        val productPrice = arguments?.getDouble("productPrice")
        val productIsShoppingCart = arguments?.getString("productIsShoppingCart")
        val productQuantity = arguments?.getString("productQuantity")
        val productRating = arguments?.getString("productRating").let {
            val splitArray =
                it.toString().substring(1, it.toString().length - 1).removePrefix("rate=")
                    .replace("count=", "").replace(" ", "").replace(" ", "")
            val rate = splitArray.get(0).code.toDouble()
            val count = splitArray.get(1).code
            ToRating(rate, count)
        } as Rating?

        if (productQuantity != null) {
            quantity = productQuantity.toInt()
        }

        binding.tvProductDetailTotalPrice.text = "$productPrice $"
        binding.tvProductPrice.text = "$productPrice $"
        binding.tvProductDescription.text = productDescription
        binding.tvProductName.text = productTitle
        if (productPrice != null) {
            totalPrice = quantity * productPrice.toFloat()
        }
        binding.tvProductQuantity.text = quantity.toString()
        binding.tvProductDetailTotalPrice.text = "$totalPrice $"
        Glide.with(view.context)
            .load(productImage)
            .into(binding.ivProductImage)

        binding.bttnIncreaseQuantity.setOnClickListener {
            quantity++
            if (productPrice != null) {
                totalPrice = quantity * productPrice.toFloat()
            }
            binding.tvProductQuantity.text = quantity.toString()
            binding.tvProductDetailTotalPrice.text = "$totalPrice $"

        }

        binding.bttnDecreaseQuantity.setOnClickListener {
            if (quantity > 1) {
                quantity--
            }
            if (productPrice != null) {
                totalPrice = quantity * productPrice.toFloat()
            }
            binding.tvProductQuantity.text = quantity.toString()
            binding.tvProductDetailTotalPrice.text = "$totalPrice$"
        }


        binding.bttnAddToBasket.setOnClickListener {
            val product = Product(
                productId,
                productImage,
                productTitle,
                productPrice,
                productDescription,
                productCategory,
                productIsShoppingCart,
                productRating,
                quantity
            )
            firestore.collection("shoppingCartProduct").document(userId).collection("product")
                .document(productId.toString()).set(product)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Product added to shopping cart.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }

    fun ToRating(rate: Double, count: Int): Rating {
        return Rating(count, rate)
    }

}

data class Product(
    val id: Int? = null,
    val image: String? = null,
    val title: String? = null,
    val price: Double? = null,
    val description: String? = null,
    val category: String? = null,
    val isShoppingCart: String?,
    val rating: Rating?,
    val quantity: Int? = null
)