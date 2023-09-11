package com.example.todoproject
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.todoproject.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString().trim()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (isPasswordValid(password)) {
                        if (password == confirmPassword) {
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        // Clear input fields after successful sign-up
                                        binding.signupEmail.text = null
                                        binding.signupPassword.text = null
                                        binding.signupConfirm.text = null
                                    } else {
                                        val errorMessage = when (task.exception) {
                                            is FirebaseAuthWeakPasswordException ->
                                                "Password is too weak. Please choose a stronger password."
                                            is FirebaseAuthInvalidCredentialsException ->
                                                "Invalid email or password."
                                            else -> "Sign-up failed. ${task.exception?.message}"
                                        }
                                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Password must contain at least 8 characters, including uppercase, lowercase, and a number.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }
}
