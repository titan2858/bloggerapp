package com.example.bloggerapp.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bloggerapp.databinding.ActivityWelcomeBinding
import com.example.bloggerapp.signInAndRegistrationActivity

class WelcomeActivity : AppCompatActivity() {
    private val binding: ActivityWelcomeBinding by lazy {
        ActivityWelcomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Set up insets to handle system bars (like status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up onClick listener for the login button
        binding.loginButton2.setOnClickListener {
            val intent = Intent(this, signInAndRegistrationActivity::class.java)
            intent.putExtra("action", "login")
            startActivity(intent)
        }

        // Set up onClick listener for the register button
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, signInAndRegistrationActivity::class.java)
            intent.putExtra("action", "register")
            startActivity(intent)
        }
    }
}
