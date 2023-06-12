# kotlin-signUp-MVVM
A demo signUp App using MVVM architecture, built using the Model-View-ViewModel (MVVM) architecture pattern. It provides a seamless and user-friendly sign-up experience, allowing users to create portfolio..

LoginViewMVVM is an Android application that demonstrates a simple login view using the MVVM architecture pattern. It provides a user interface for entering login credentials and displays a welcome message upon successful login.

## Features

- Login screen with input fields for username, password, email, photo and web url.
- Submit button that validates the input and displays the information in welcome message.
- MVVM architecture pattern for separation of concerns.
- Data Binding for declarative UI updates.
- ViewModel and LiveData for managing and observing data changes.

## Best Practisces 
The applicatioin follows best practices. Here's a summary of the best practices followed in this code:

- Binding: The code uses View Binding to efficiently access and manipulate views, which is recommended over findViewById.

- ViewModel: The ViewModel is used to separate the logic and data handling from the UI. It provides a clean and lifecycle-aware way to manage data and UI updates.

- Lifecycle Awareness: The Fragment observes the LiveData from the ViewModel using viewLifecycleOwner, ensuring that the observations are bound to the Fragment's   lifecycle and avoid memory leaks.

- Null Safety: The code uses the safe call operator (?.) and non-null assertion operator (!!) to handle potential null values safely, reducing the risk of       NullPointerExceptions.

- String Resources: The code uses string resources (R.string) to store and retrieve localized strings, promoting easier localization and maintenance of the app's text.

- SpannableString: The code utilizes SpannableString to apply text styling, such as underline and color, to specific parts of the text. This is a flexible way to format text within a TextView.

- Variable Naming: The variable names follow the recommended naming conventions, using descriptive names that convey their purpose and improve code readability.


## Installation

Clone the repository and open the project in Android Studio.

git clone https://github.com/abdulqadirtr/kotlin-signUp-MVVM.git

## Usage

1. Build and run the application on an Android emulator or device.
2. Enter your name, password, email address, web url, in the provided input fields.
3. Name, Email and password is important.
4. Click the "submit" button to validate the input and display the welcome message.


