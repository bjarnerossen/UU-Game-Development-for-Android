package com.example.astrowingsgame

import android.content.res.Resources

const val ENEMY_HEIGHT = 50
const val ENEMY_SPAWN_OFFSET = STAGE_WIDTH * 2

class Enemy(res: Resources) : BitmapEntity() {
    private var lastSpawnedX: Float = 0f
    init {
        var id = R.drawable.tm_2
        when(RNG.nextInt(1,6)) {
            1 -> id = R.drawable.tm_2
            2 -> id = R.drawable.tm_3
            3 -> id = R.drawable.tm_4
            4 -> id = R.drawable.tm_5
            5 -> id = R.drawable.tm_6
        }
        val bmp = loadBitmap(res, id, ENEMY_HEIGHT)
        setSprite(flipVertically(bmp))
    }

    override fun respawn() {
        do {
            x = (STAGE_WIDTH + RNG.nextInt(ENEMY_SPAWN_OFFSET)).toFloat()
            y = RNG.nextInt(STAGE_HEIGHT - ENEMY_HEIGHT).toFloat()
        } while (Math.abs(x - lastSpawnedX) < ENEMY_HEIGHT)
        lastSpawnedX = x
    }

    override fun update() {
        velX = -playerSpeed
        x += velX
        if(right() < 0) {
            respawn()
        }
    }

    override fun onCollision(that: Entity) {
        respawn()
    }
}