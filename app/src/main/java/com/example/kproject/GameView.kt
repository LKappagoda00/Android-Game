package com.example.kproject

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View


class GameView(var c: Context, var gameTask: MainActivity) : View(c) {
    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var highestScore = 0
    private var myflyPosition = 0
    private val otherflys = ArrayList<HashMap<String, Any>>()

    var viewWidth = 0
    var viewHeight = 0
    private lateinit var sharedPreferences: SharedPreferences

    init {
        myPaint = Paint()
        sharedPreferences = c.getSharedPreferences("GamePreferences", Context.MODE_PRIVATE)
        highestScore = sharedPreferences.getInt("HighestScore", 0)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherflys.add(map)
        }
        time += 10 + speed

        val flyWidth = viewWidth / 4
        val flyHeight = flyWidth + 15


        // Draw player's rocket
        val jetDrawable = resources.getDrawable(R.drawable.adobestock_616490068_preview,null)
        jetDrawable.setBounds(
            myflyPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - flyHeight,
            myflyPosition * viewWidth / 3 + viewWidth / 15 + flyWidth - 25,
            viewHeight - 2
        )
        jetDrawable.draw(canvas)

        // Draw flying saucers

        val iterator = otherflys.iterator()
        while (iterator.hasNext()) {
            val fly = iterator.next()
            val lane = fly["lane"] as Int
            val flyX = lane * viewWidth / 3 + viewWidth / 15
            var flyY = time - fly["startTime"] as Int


            val flyingSaucerDrawable = resources.getDrawable(R.drawable.adobestock_538463882_preview, null)
            flyingSaucerDrawable.setBounds(
                flyX + 25, flyY - flyHeight, flyX + flyWidth - 25, flyY
            )
            flyingSaucerDrawable.draw(canvas)
            //close game
            if (lane == myflyPosition && flyY > viewHeight - 2 - flyHeight && flyY < viewHeight - 2) {
                gameTask.closeGame(score)
            }
            // make speed
            if (flyY > viewHeight + flyHeight) {
                iterator.remove()
                score++
                speed = 1 + score / 8
            }
        }

        // Update highest score if the current score exceeds it
        if (score > highestScore) {
            highestScore = score
            val editor = sharedPreferences.edit()
            editor.putInt("HighestScore", highestScore)
            editor.apply()
        }

        // Draw score, speed, and highest score
        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 45f
        canvas.drawText("Score : $score", 80f, 80f, myPaint!!)
        canvas.drawText("Speed : $speed", 380f, 80f, myPaint!!)
        canvas.drawText("Highest Score : $highestScore", 680f, 80f, myPaint!!)

        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myflyPosition > 0) {
                        myflyPosition--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (myflyPosition < 2) {
                        myflyPosition++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }
}