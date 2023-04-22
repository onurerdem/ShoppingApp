package com.onurerdem.shoppingapp.feature.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.onurerdem.shoppingapp.R
import com.onurerdem.shoppingapp.data.model.ProductsItemDTO
import com.onurerdem.shoppingapp.databinding.FragmentHomeBinding
import com.onurerdem.shoppingapp.feature.home.adapter.HomeProductAdapter
import com.onurerdem.shoppingapp.feature.home.adapter.OnShoppingCartClickListener
import com.onurerdem.shoppingapp.feature.shoppingCart.ShoppingCartViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), OnShoppingCartClickListener {
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.toolbar.title = "Products"
        searchProduct()

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    when (it) {
                        is HomeViewState.Success -> {
                            if (it.filteredData.isEmpty().not()) {
                                initAdapter(it.filteredData)
                            } else {
                                it.data?.let { it1 -> initAdapter(it1) }
                            }
                        }

                        else -> {}

                    }
                }
            }

            launch {
                viewModel.uiEvent.collect {
                    when (it) {
                        is HomeViewEvent.ShowError -> {
                            Snackbar.make(
                                binding.root,
                                it.message.toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is HomeViewEvent.NavigateToDetail -> {

                        }
                    }
                }
            }
        }
    }

    private fun initAdapter(data: MutableList<ProductsItemDTO>) {
        binding.rvProductList.adapter =
            HomeProductAdapter(this@HomeFragment).apply {
                submitList(data)
            }
    }

    private fun searchProduct() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (newText.length > 4) {
                        viewModel.searchProduct(newText)
                    } else {
                        viewModel.searchProduct("")
                    }
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchProduct(it) }
                return false
            }
        })
    }

    override fun onProductClick(product: ProductsItemDTO) {
        navController?.navigate(R.id.action_homeFragment_to_productDetailFragment,Bundle().apply {
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

    override fun onAddShoppingCartClick(id: Int?) {
        TODO("Not yet implemented")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRemoveShoppingCartClick(productsItem: ProductsItemDTO) {
        viewModel.onShoppingCartProduct(productsItem)
        binding.rvProductList.adapter?.notifyDataSetChanged()
    }
}