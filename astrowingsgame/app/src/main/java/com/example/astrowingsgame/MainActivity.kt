package com.example.astrowingsgame

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.startGameButton)?.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }
        @SuppressLint("SetTextI18n")
        override fun onResume() {
            super.onResume()
            val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
            val longestDistance = prefs.getInt(LONGEST_DIST, 0)
            val highscore = findViewById<TextView>(R.id.highscore)
            highscore.text = "Longest distance: $longestDistance km"
        }
    }