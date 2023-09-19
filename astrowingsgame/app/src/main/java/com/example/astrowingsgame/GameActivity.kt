package com.example.astrowingsgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class GameActivity : AppCompatActivity() {
    private val TAG = "GameActivity"
    private lateinit var game : Game
    override fun onCreate(savedInstanceState: Bundle?) {
        game = Game(this)
        super.onCreate(savedInstanceState)
        setContentView(game)
        Log.d(TAG, "onCreate called!")
    }

    override fun onPause() {
        game.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        game.resume()
    }
}