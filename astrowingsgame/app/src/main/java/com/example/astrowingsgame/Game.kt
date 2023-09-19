package com.example.astrowingsgame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.random.Random

const val STAGE_WIDTH = 1080
const val STAGE_HEIGHT = 720
const val STAR_COUNT = 55
const val ENEMY_COUNT = 5
val RNG = Random(SystemClock.uptimeMillis())
@Volatile var isBoosting = false
var playerSpeed = 0f

private val TAG = "Game"
const val PREFS = "com.example.astrowingsgame"
const val LONGEST_DIST = "longest_distance"
class Game(context: Context) : SurfaceView(context), Runnable, SurfaceHolder.Callback {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private var gameThread = Thread(this)
    @Volatile
    private var isRunning = false
    private var isGameOver = false
    private val jukebox = Jukebox(context.assets)
    private val player = Player(resources)
    private val entities = ArrayList<Entity>()
    private val paint = Paint()
    private var distanceTravelled = 0
    private var maxDistanceTravelled = 0

    private val backgroundColors = intArrayOf(
        Color.rgb(0, 0, 0),       // Black
        Color.rgb(0, 0, 16),      // Dark Blue
        Color.rgb(0, 0, 32),      // Dark Blue
        Color.rgb(0, 0, 48),      // Dark Blue
        Color.rgb(0, 0, 64)       // Dark Blue
    )
    init {
        holder.addCallback(this)
        holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT)
        for (i in 0 until STAR_COUNT) {
            entities.add(Star())
        }
        for (i in 0 until ENEMY_COUNT) {
            entities.add(Enemy(resources))
        }
    }

    private fun restart() {
        for (entity in entities) {
            entity.respawn()
        }
        player.respawn()
        distanceTravelled = 0
        maxDistanceTravelled = prefs.getInt(LONGEST_DIST, 0)
        isGameOver = false
    }

    override fun run() {
        while (isRunning) {
            update()
            render()
        }
    }

    private fun update() {
        player.update()
        for (entity in entities) {
            entity.update()
        }
        distanceTravelled += playerSpeed.toInt()
        checkCollisions()
        checkGameOver()
    }

    private fun checkGameOver() {
        if(player.health < 1) {
            isGameOver = true
            if(distanceTravelled > maxDistanceTravelled) {
                editor.putInt(LONGEST_DIST, distanceTravelled)
                editor.apply()
            }
        }
    }

    private fun checkCollisions() {
        for (i in STAR_COUNT until entities.size) {
            val enemy = entities[i]
            if (enemy is Enemy && isColliding(enemy, player)) {
                enemy.onCollision(player)
                player.onCollision(enemy)
                jukebox.play(SFX.crash)
            }
        }
    }
    private fun render() {
        val canvas = acquireAndLockCanvas() ?: return

        //Calculate the background color based on distanceTravelled
        val backgroundColorIndex = distanceTravelled / 10000
        val backgroundColor = backgroundColors.getOrNull(backgroundColorIndex) ?: backgroundColors.last()

        canvas.drawColor(backgroundColor)

        for (entity in entities) {
            entity.render(canvas, paint)
        }
        player.render(canvas, paint)
        renderHud(canvas, paint)
        holder.unlockCanvasAndPost(canvas)
    }
    private fun renderHud(canvas: Canvas, paint: Paint) {
        val textSize = 48f
        val margin = 10f
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = textSize
        if(!isGameOver) {
            canvas.drawText("Health: ${player.health}", margin, textSize, paint)
            canvas.drawText("Distance: $distanceTravelled km", margin, textSize*2, paint)
           }  else {
                   paint.textAlign = Paint.Align.CENTER
                   val centerX = STAGE_WIDTH * 0.5f
                   val centerY = STAGE_HEIGHT * 0.5f
                canvas.drawText("GAME OVER!", centerX, centerY, paint)
                canvas.drawText("(press to restart)", centerX, centerY+textSize, paint)
           }
    }

    private fun acquireAndLockCanvas(): Canvas? {
        if (holder?.surface?.isValid == false) {
            return null
        }
        return holder.lockCanvas()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "slowing down")
                isBoosting = false
            }
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "isBoosting")
                if(isGameOver) {
                    restart()
                } else { isBoosting = true }
            }
        }
        return true
    }

    fun pause() {
        isRunning = false
        try {
            gameThread.join()
        } catch (e: Exception) {
        }
    }

    fun resume() {
        isRunning = true // Start the game loop
        gameThread = Thread(this)
        gameThread.start()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated")
        resume()
    }

    override fun surfaceChanged(p0: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(TAG, "surfaceChanged, width: $width, height: $height")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        Log.d(TAG, "surfaceDestroyed")
        pause()
    }
}