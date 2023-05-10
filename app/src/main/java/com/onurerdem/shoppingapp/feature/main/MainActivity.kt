package com.onurerdem.shoppingapp.feature.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.onurerdem.shoppingapp.R
import com.onurerdem.shoppingapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    companion object {
        const val KEY_NAVIGATE_HOME = "KEY_NAVIGATE_HOME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiState.collect {
                    when (it) {
                        is MainUiState.Success -> {
                            initNavController(it.isNavigateHome)
                        }
                    }
                }
            }
        }
    }

    private fun initNavController(isNavigateToHome: Boolean) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        if (isNavigateToHome.not()) {
            navController.navigate(R.id.login_graph)
        }
        binding.isVisibleBar = isNavigateToHome

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.registerFragment || destination.id == R.id.loginFragment) {
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

    }
}