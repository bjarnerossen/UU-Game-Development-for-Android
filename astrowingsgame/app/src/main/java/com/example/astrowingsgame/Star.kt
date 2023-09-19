package com.example.astrowingsgame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.random.Random

class Star : Entity() {
    private val TAG = "Star"

    private val starColors = listOf(
        Color.YELLOW,
        Color.CYAN,
        Color.WHITE,
        Color.RED,
        Color.GREEN
    )

    private val minRadius = 1f
    private val maxRadius = 3f

    private val radius = Random.nextFloat() * (maxRadius - minRadius) + minRadius
    private val speed = Random.nextFloat() * 3f + 1f
    private val twinkleColors = mutableListOf<Int>()

    private var isTwinkling = false
    private var twinkleCounter = 0

    init {
        respawn()
    }

    override fun respawn() {
        x = RNG.nextInt(STAGE_WIDTH).toFloat()
        y = RNG.nextInt(STAGE_HEIGHT).toFloat()
        width = radius * 2f
        height = width
        // Generate random twinkling colors
        for (i in 0 until Random.nextInt(1, 5)) {
            twinkleColors.add(starColors[Random.nextInt(starColors.size)])
        }
        isTwinkling = !twinkleColors.isEmpty()
    }

    override fun update() {
        super.update()
        x += -playerSpeed * speed
        // If the star goes off-screen, reset its position to the right
        if (right() < 0) {
            setLeft(STAGE_WIDTH.toFloat())
            setTop(RNG.nextInt(STAGE_HEIGHT - height.toInt()).toFloat())
            // Randomly start or stop twinkling
            if (Random.nextBoolean()) {
                isTwinkling = !isTwinkling
                twinkleCounter = 0
            }
        }
        if (top() > STAGE_HEIGHT) setBottom(0f)
        // Increment the twinkling counter
        if (isTwinkling) {
            twinkleCounter++
            if (twinkleCounter > 10) {
                isTwinkling = false
            }
        }
    }

    override fun render(canvas: Canvas, paint: Paint) {
        super.render(canvas, paint)
        // Draw the star with its twinkle effect
        if (isTwinkling) {
            paint.color = twinkleColors[Random.nextInt(twinkleColors.size)]
        } else {
            paint.color = Color.WHITE
        }
        canvas.drawCircle(x, y, radius, paint)
    }
}