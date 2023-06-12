package com.example.loginviewmvvm.ui.model

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val userFirstNameError: Int? = null,
    val userEmailError: Int? = null,
    val passwordError: Int? = null,
    val userWebsite: Int? = null,
    val isDataValid: Boolean = false
)
