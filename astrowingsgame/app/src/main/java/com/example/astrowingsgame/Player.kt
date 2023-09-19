package com.example.astrowingsgame

import android.content.res.Resources
import android.util.Log
import androidx.core.math.MathUtils.clamp

const val PLAYER_HEIGHT = 75
const val ACCELERATION = 1.1f
const val MIN_VEL = 0.1f
const val MAX_VEL = 20f
const val GRAVITY = 1.1f
const val LIFT = -(GRAVITY*2f)
const val DRAG = 0.97F
const val PLAYER_STARTING_HEALTH = 3
const val PLAYER_STARTING_POSITION = 0f
class Player(res: Resources) : BitmapEntity() {
    private val TAG = "Player"
    var health  = PLAYER_STARTING_HEALTH
    private var lastRespawnedY: Float = 0f
    init {
        setSprite(loadBitmap(res, R.drawable.tm_1, PLAYER_HEIGHT))
        respawn()
    }

    override fun respawn() {
        health = PLAYER_STARTING_HEALTH

        do {
            x = PLAYER_STARTING_POSITION
            y = RNG.nextInt(STAGE_HEIGHT - PLAYER_HEIGHT).toFloat()
        } while (Math.abs(y - lastRespawnedY) < PLAYER_HEIGHT)

        lastRespawnedY = y
    }

    override fun onCollision(that: Entity) {
        Log.d(TAG, "onCollision")
        if (that is Enemy) {
            health--
        } else {
            return
        }
    }

    override fun update() {
        velX *= DRAG
        velY += GRAVITY
        if(isBoosting) {
            velX *= ACCELERATION
            velY += LIFT
        }
        velX = clamp(velX, MIN_VEL, MAX_VEL)
        velY = clamp(velY, -MAX_VEL, MAX_VEL)

        y += velY
        playerSpeed = velX

        if(bottom() > STAGE_HEIGHT) {
            setBottom(STAGE_HEIGHT.toFloat())
        } else if (top() < 0f) {
            setTop(0f)
        }
    }
}