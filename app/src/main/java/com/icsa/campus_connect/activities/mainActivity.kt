package com.icsa.campus_connect.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.icsa.campus_connect.R

class mainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigate to LoginActivity
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Navigate to SignUpActivity
        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
