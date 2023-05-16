package com.onurerdem.shoppingapp.feature.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.android.material.snackbar.Snackbar
import com.onurerdem.shoppingapp.R
import com.onurerdem.shoppingapp.data.model.ProductsItemDTO
import com.onurerdem.shoppingapp.databinding.FragmentHomeBinding
import com.onurerdem.shoppingapp.feature.home.adapter.HomeProductAdapter
import com.onurerdem.shoppingapp.feature.home.adapter.OnShoppingCartClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(), OnShoppingCartClickListener {
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding
    private var navController: NavController? = null
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE)
        val locale = sharedPrefs.getString("language", "")?.let { Locale(it) }
        if (locale != null) {
            Locale.setDefault(locale)
        }
        val config = Configuration()
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
    }

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

        binding.toolbar.title = requireContext().getString(R.string.products)
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
        binding.swipeRefreshLayout.setOnRefreshListener {
            navController?.navigate(R.id.homeFragment)
            binding.swipeRefreshLayout.isRefreshing = false
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
        navController?.navigate(R.id.action_homeFragment_to_productDetailFragment, Bundle().apply {
            putString("productId", product.id.toString())
            putString("productImage", product.image.toString())
            putString("productTitle", product.title)
            putString("productCategory", product.category.toString())
            putString("productDescription", product.description.toString())
            putDouble("productPrice", product.price as Double)
            putString("productIsShoppingCart", product.isShoppingCart.toString())
            putString("productQuantity", product.quantity.toString())
            putString("productRating", product.rating.toString())

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

                    builder.setMessage(requireContext().getString(R.string.are_you_sure_you_want_to_exit))
                        .setTitle(requireContext().getString(R.string.exit))

                    builder.apply {
                        setPositiveButton(requireContext().getString(R.string.yes)) { dialog, id ->
                            getActivity()?.finish()
                        }
                        setNegativeButton(requireContext().getString(R.string.no)) { dialog, id ->
                        }
                    }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }
}