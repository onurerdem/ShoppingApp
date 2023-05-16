package com.onurerdem.shoppingapp.feature.profile

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.onurerdem.shoppingapp.R
import com.onurerdem.shoppingapp.databinding.FragmentProfileBinding
import java.util.*

class ProfileFragment() : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var navController: NavController?= null
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        firebaseAuth = Firebase.auth
        firestore = Firebase.firestore
        val userId = firebaseAuth.currentUser?.uid.toString()
        sharedPrefs = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE)

        val docRef: DocumentReference = firestore.collection("users").document(userId)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.getResult()
                if (document != null) {
                    binding.tvProfileUsername.text = requireContext().getString(R.string.space) + document.getString("second")

                } else {
                    Log.d("LOGGER", "No such document")
                }
            } else {
                Log.d("LOGGER", "get failed with ", task.exception)
            }
        }

        binding.tvProfileEmail.text = requireContext().getString(R.string.space) + firebaseAuth.currentUser?.email

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

        binding.bttnSignOut.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

            builder.setMessage(requireContext().getString(R.string.are_you_sure_you_want_to_sign_out))
                .setTitle(requireContext().getString(R.string.sign_out))

            builder.apply {
                setPositiveButton(requireContext().getString(R.string.yes)) { dialog, id ->
                    Toast.makeText(requireContext(), requireContext().getString(R.string.session_terminated), Toast.LENGTH_SHORT).show()
                    firebaseAuth.signOut()
                    navController?.navigate(
                        resId = R.id.action_profileFragment_to_login_graph,
                        null,
                        navOptions = NavOptions.Builder().setPopUpTo(0, true).build()
                    )
                }
                setNegativeButton(requireContext().getString(R.string.no)) { dialog, id ->
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

        activity?.recreate()

    }
}