package com.example.loginviewmvvm.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.loginviewmvvm.R
import com.example.loginviewmvvm.databinding.FragmentLoginBinding
import com.example.loginviewmvvm.ui.model.LoggedInUserView
import com.example.loginviewmvvm.ui.viewmodel.LoginViewModel
import com.example.loginviewmvvm.ui.viewmodel.LoginViewModelFactory
import java.io.ByteArrayOutputStream

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var binding: FragmentLoginBinding? = null

    private var profilePhotoUri: Uri? = null

    private companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val GALLERY_REQUEST_CODE = 1002
        private const val CAMERA_REQUEST_CODE = 200
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModelFactory()
        )[LoginViewModel::class.java]

        val usernameEditText = binding?.firstName
        val emailEditText = binding?.email
        val passwordEditText = binding?.password
        val websiteEditText = binding?.website
        val avatarFrame = binding?.frameLayout
        val loginButton = binding?.login

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton?.isEnabled = loginFormState.isDataValid
                loginFormState.userFirstNameError?.let {
                    usernameEditText?.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText?.error = getString(it)
                }
                loginFormState.userEmailError?.let {
                    emailEditText?.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    usernameEditText?.text.toString(),
                    passwordEditText?.text.toString(),
                    emailEditText?.text.toString()
                )
            }
        }
        usernameEditText?.addTextChangedListener(afterTextChangedListener)
        passwordEditText?.addTextChangedListener(afterTextChangedListener)
        emailEditText?.addTextChangedListener(afterTextChangedListener)
        passwordEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    usernameEditText?.text.toString(),
                    passwordEditText?.text.toString()
                )
            }
            false
        }

        avatarFrame?.setOnClickListener {
            checkCameraPermission()
        }

        loginButton?.setOnClickListener {
            loginViewModel.setConfirmationData(
                usernameEditText?.text.toString(),
                emailEditText?.text.toString(),
                websiteEditText?.text.toString(),
                profilePhotoUri
            )

            // Navigate to the ConfirmationFragment
            val confirmationFragment = ConfirmationFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, confirmationFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select an option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    binding?.textViewOverlay?.visibility = View.GONE
                    binding?.imageViewAvatar?.visibility = View.VISIBLE
                    val imageUri = data.data
                    imageUri?.let { uri ->
                        Glide.with(this)
                            .load(uri)
                            .into(binding?.imageViewAvatar!!)
                        profilePhotoUri = imageUri
                    }
                }
            }
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    binding?.textViewOverlay?.visibility = View.GONE
                    binding?.imageViewAvatar?.visibility = View.VISIBLE
                    val thumbnail = data?.extras?.get("data")
                    Glide.with(this)
                        .load(thumbnail as Bitmap?)
                        .into(binding?.imageViewAvatar!!)

                    val thumbnailBitmap: Bitmap =
                        thumbnail as Bitmap  // Assuming thumbnail is a Bitmap
                    val outputStream = ByteArrayOutputStream()
                    thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                    val imageUri = Uri.parse(
                        MediaStore.Images.Media.insertImage(
                            requireContext().contentResolver,
                            thumbnailBitmap,
                            null,
                            null
                        )
                    )
                    profilePhotoUri = imageUri
                }
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        // TODO: Initiate successful logged-in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openImagePicker()
        }
    }
}
