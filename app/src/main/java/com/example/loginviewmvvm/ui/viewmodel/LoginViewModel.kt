package com.example.loginviewmvvm.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.loginviewmvvm.R
import com.example.loginviewmvvm.ui.data.ConfirmationData
import com.example.loginviewmvvm.ui.data.Result
import com.example.loginviewmvvm.ui.model.LoggedInUserView
import com.example.loginviewmvvm.ui.model.LoginFormState
import com.example.loginviewmvvm.ui.model.LoginResult
import com.example.loginviewmvvm.ui.repo.LoginRepository


class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _confirmationData = MutableLiveData<ConfirmationData>()
    val confirmationData: LiveData<ConfirmationData>
        get() = _confirmationData


    /**
     * can be used for future to save the data to the database
     */
    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    // Method to set the confirmation data
    fun setConfirmationData(username: String, email: String, website: String, profilePhotoUri: Uri?) {
        val data = ConfirmationData(username, email, website, profilePhotoUri)
        _confirmationData.value = data
    }

    fun loginDataChanged(username: String, password: String, email: String) {
        val isUsernameValid = isUserNameValid(username)
        val isPasswordValid = isPasswordValid(password)
        val isEmailValid = isEmailValid(email)

        _loginForm.value = LoginFormState(
            userFirstNameError = if (username.isNotEmpty() && !isUsernameValid) R.string.invalid_username else null,
            passwordError = if (password.isNotEmpty() && !isPasswordValid) R.string.invalid_password else null,
            userEmailError = if (email.isNotEmpty() && !isEmailValid) R.string.invalid_email else null,
            isDataValid = isUsernameValid && isPasswordValid && isEmailValid
        )
    }
    private fun isUserNameValid(username: String): Boolean {
        return  username.matches("[a-zA-Z0-9]+".toRegex()) && username.length >= 4
    }

    // check for password validation
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
    // check for email verification
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}