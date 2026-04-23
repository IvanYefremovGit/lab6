package com.example.lab6

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()

    private var playerX = 0f
    private var playerY = 0f
    private var velocityY = -20f

    private val gravity = 1f

    private val platforms = mutableListOf<Pair<Float, Float>>()

    private val MAX_GAP_Y = 220f
    private val MAX_GAP_X = 220f

    private var score = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        playerX = w / 2f - 40
        playerY = h - 300f

        platforms.clear()

        var lastY = h - 200f

        for (i in 0..7) {

            val baseX = if (platforms.isEmpty()) {
                w / 2f
            } else {
                platforms.random().first
            }

            val offset = Random.nextFloat() * MAX_GAP_X * 2 - MAX_GAP_X
            var newX = baseX + offset

            if (newX < 0) newX = 0f
            if (newX > w - 200) newX = w - 200f

            val newY = lastY - MAX_GAP_Y

            platforms.add(Pair(newX, newY))
            lastY = newY
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.WHITE)

        paint.color = Color.BLUE
        canvas.drawRect(playerX, playerY, playerX + 80, playerY + 80, paint)

        paint.color = Color.BLACK
        for (platform in platforms) {
            canvas.drawRect(
                platform.first,
                platform.second,
                platform.first + 200,
                platform.second + 30,
                paint
            )
        }

        paint.color = Color.RED
        paint.textSize = 60f
        canvas.drawText("Score: $score", 50f, 80f, paint)

        update()
        invalidate()
    }

    private fun update() {
        velocityY += gravity
        playerY += velocityY

        if (playerX < -80) {
            playerX = width.toFloat()
        } else if (playerX > width) {
            playerX = -80f
        }

        if (playerY < height / 2) {
            val diff = height / 2 - playerY
            playerY = height / 2f

            score += (diff / 10).toInt()

            for (i in platforms.indices) {
                val (x, y) = platforms[i]
                platforms[i] = Pair(x, y + diff)
            }
        }

        for (platform in platforms) {
            if (playerY + 80 >= platform.second &&
                playerY + 80 <= platform.second + 30 &&
                playerX + 80 >= platform.first &&
                playerX <= platform.first + 200 &&
                velocityY > 0
            ) {
                velocityY = -32f
            }
        }

        for (i in platforms.indices) {
            val (x, y) = platforms[i]

            if (y > height) {

                var minY = Float.MAX_VALUE
                for (p in platforms) {
                    if (p.second < minY) {
                        minY = p.second
                    }
                }

                val baseX = platforms.random().first

                val offset = Random.nextFloat() * MAX_GAP_X * 2 - MAX_GAP_X
                var newX = baseX + offset

                if (newX < 0) newX = 0f
                if (newX > width - 200) newX = width - 200f

                val newY = minY - MAX_GAP_Y

                platforms[i] = Pair(newX, newY)
            }
        }

        if (playerY > height) {
            playerX = width / 2f - 40
            playerY = height - 300f
            velocityY = -20f
            score = 0
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (event.x < width / 2) {
                playerX -= 100
            } else {
                playerX += 100
            }
        }
        return true
    }
}