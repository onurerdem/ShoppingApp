package com.onurerdem.shoppingapp.feature.shoppingcart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.databinding.FragmentShoppingCartBinding
import com.onurerdem.shoppingapp.feature.home.adapter.HomeProductAdapter
import com.onurerdem.shoppingapp.feature.home.adapter.OnShoppingCartClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingCartFragment : Fragment(), OnShoppingCartClickListener {
    private lateinit var binding: FragmentShoppingCartBinding
    private val viewModel by viewModels<ShoppingCartViewModel>()
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

        binding.toolbar.title = "Shopping Cart Product"
        searchProduct()
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    when (it) {
                        is ShoppingCartViewState.Success -> {
                            if (it.filteredData.isEmpty().not()) {
                                initAdapter(it.filteredData)
                            } else {
                                initAdapter(it.data)
                            }
                        }
                        is ShoppingCartViewState.Loading -> {

                        }
                        is ShoppingCartViewState.Error -> {}

                        else -> {}
                    }
                }
            }

        }

    }

    private fun initAdapter(data: MutableList<ProductsItem>) {
        binding.rvShoppingCart.adapter =
            HomeProductAdapter(this@ShoppingCartFragment).apply {
                submitList(data)
            }
    }

    private fun searchProduct() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (newText.length > 4) {
                        viewModel.searchMovie(newText)
                    } else {
                        viewModel.searchMovie("")
                    }
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchMovie(it) }
                return false
            }
        })
    }

    override fun onAddShoppingCartClick(id: Int?) {
        TODO("Not yet implemented")
    }

    override fun onRemoveShoppingCartClick(productsItem: ProductsItem) {
        TODO("Not yet implemented")
    }
}