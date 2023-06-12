package com.example.loginviewmvvm.ui.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.loginviewmvvm.R
import com.example.loginviewmvvm.databinding.FragmentConfirmationBinding
import com.example.loginviewmvvm.ui.viewmodel.LoginViewModel
import com.example.loginviewmvvm.ui.viewmodel.LoginViewModelFactory
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback

class ConfirmationFragment : Fragment() {

    private var _binding: FragmentConfirmationBinding? = null

    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var onBackPressedCallback: OnBackPressedCallback


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConfirmationBinding.inflate(inflater, container, false)
         return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the LoginViewModel
        loginViewModel = ViewModelProvider(requireActivity(), LoginViewModelFactory())[LoginViewModel::class.java]

        // Observe the confirmation data
        loginViewModel.confirmationData.observe(viewLifecycleOwner) { confirmationData ->
            // Display the confirmation information
            confirmationData?.let {
                binding.textViewFirstName.text = it.firstName
                val welcomeMessage = getString(R.string.welcome_message, it.firstName)
                binding.welcomeMessage.text = welcomeMessage
                binding.textViewEmail.text = it.email
                binding.imageViewPhoto.setImageURI(it.photoUri)
                 binding.textViewWebsite.text = it.website
                }
            }
        // Enable back button press for the Fragment
        requireActivity().onBackPressedDispatcher.addCallback(this) {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}