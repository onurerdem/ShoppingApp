package com.onurerdem.shoppingapp.feature.login

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.onurerdem.shoppingapp.R
import com.onurerdem.shoppingapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentLoginBinding
    private var navController: androidx.navigation.NavController? = null
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
                            Toast.makeText(requireContext(), requireContext().getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        }
                        is LoginViewEvent.ShowError -> {
                            Toast.makeText(requireContext(), requireContext().getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
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

        binding.btnLanguage.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

            builder.setMessage(requireContext().getString(R.string.you_can_choose_the_language_you_want_to_use))
                .setTitle(requireContext().getString(R.string.warning))

            builder.apply {
                setPositiveButton(requireContext().getString(R.string.turkish)) { dialog, id ->
                    changeLanguage("tr")
                }
                setNegativeButton(requireContext().getString(R.string.english)) { dialog, id ->
                    changeLanguage("en")
                }
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun changeLanguage(language: String) {

        val editor = sharedPrefs.edit()
        editor.putString("language", language)
        editor.apply()

        val locale = sharedPrefs.getString("language", "")?.let { Locale(it) }
        if (locale != null) {
            Locale.setDefault(locale)
        }
        val config = Configuration()
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        navController?.navigate(R.id.loginFragment)

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