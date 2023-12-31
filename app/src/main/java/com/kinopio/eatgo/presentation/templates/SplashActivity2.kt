package com.kinopio.eatgo.presentation.templates

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.kinopio.eatgo.R
import com.kinopio.eatgo.presentation.naverlogin.NaverLoginActivity

class SplashActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)

        supportActionBar?.hide()

        var handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            Intent(this, NaverLoginActivity::class.java).apply{
                Thread.sleep(2000)
                startActivity(this)
                finish()
            }
        },3000)
    }
}