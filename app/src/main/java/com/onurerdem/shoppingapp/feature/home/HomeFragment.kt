package com.onurerdem.shoppingapp.feature.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.databinding.FragmentHomeBinding
import com.onurerdem.shoppingapp.feature.home.adapter.HomeProductAdapter
import com.onurerdem.shoppingapp.feature.home.adapter.OnShoppingCartClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), OnShoppingCartClickListener {
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    when (it) {
                        is HomeViewState.Success -> {
                            binding.rvProductList.adapter =
                                HomeProductAdapter(this@HomeFragment).apply {
                                    submitList(it.products)
                                }
                        }
                        is HomeViewState.Loading -> {

                        }

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
                    }
                }
            }
        }
    }

    override fun onAddShoppingCartClick(id: Int?) {
        TODO("Not yet implemented")
    }

    override fun onRemoveShoppingCartClick(productsItem: ProductsItem) {
        viewModel.onShoppingCartProduct(productsItem)
    }
}