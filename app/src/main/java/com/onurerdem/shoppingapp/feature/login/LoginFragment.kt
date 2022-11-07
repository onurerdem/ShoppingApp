package com.onurerdem.shoppingapp.feature.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.onurerdem.shoppingapp.R
import com.onurerdem.shoppingapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentLoginBinding
    private var navController: androidx.navigation.NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiEvent.collect {
                    when (it) {
                        is LoginViewEvent.NavigateToMain -> {
                            navController?.navigate(
                                R.id.action_loginFragment_to_homeFragment,
                                null,
                                androidx.navigation.NavOptions.Builder().setPopUpTo(0, true).build()
                            )
                            Toast.makeText(requireContext(), "Login Success", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is LoginViewEvent.ShowError -> {
                            Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.etMailLogin.text.trim().toString(),
                binding.etPasswordLogin.text.trim().toString()
            )
        }

        binding.btnRegister.setOnClickListener {
            navController?.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}