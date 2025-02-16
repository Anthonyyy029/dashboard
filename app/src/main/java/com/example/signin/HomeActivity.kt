package com.example.signin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        // Retrieve email from intent
        val userEmail = intent.getStringExtra("USER_EMAIL")

        // Extract username (before @ in email)
        val username = userEmail?.substringBefore("@") ?: "User"

        // Set welcome message with only the username
        welcomeTextView.text = "Welcome, $username!"

        logoutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close HomeActivity on logout
        }
    }
}
