package com.tetris.game

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tetrisView: TetrisView
    private lateinit var gestureHandler: GestureHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        setContentView(R.layout.activity_main)

        tetrisView = TetrisView(this)

        val gameContainer = findViewById<FrameLayout>(R.id.game_container)
        gameContainer.addView(tetrisView)

        // Gesture handler wired to engine
        gestureHandler = GestureHandler(
            onMoveLeft  = { tetrisView.engine.moveLeft() },
            onMoveRight = { tetrisView.engine.moveRight() },
            onRotate    = { tetrisView.engine.rotate() },
            onSoftDrop  = { tetrisView.engine.softDrop() },
            onHardDrop  = { tetrisView.engine.hardDrop() },
            onTap       = { tetrisView.onTap() }
        )

        tetrisView.setOnTouchListener { _, event ->
            gestureHandler.onTouch(event)
            true
        }

        // Button controls
        setupButtons()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btn_left).setOnTouchListener { _, e ->
            if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_MOVE)
                tetrisView.engine.moveLeft()
            true
        }
        findViewById<Button>(R.id.btn_right).setOnTouchListener { _, e ->
            if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_MOVE)
                tetrisView.engine.moveRight()
            true
        }
        findViewById<Button>(R.id.btn_rotate).setOnClickListener {
            tetrisView.engine.rotate()
        }
        findViewById<Button>(R.id.btn_down).setOnTouchListener { _, e ->
            if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_MOVE)
                tetrisView.engine.softDrop()
            true
        }
        findViewById<Button>(R.id.btn_drop).setOnClickListener {
            tetrisView.engine.hardDrop()
        }
        findViewById<Button>(R.id.btn_pause).setOnClickListener {
            tetrisView.onTap()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!tetrisView.engine.isGameOver && !tetrisView.engine.isPaused) {
            tetrisView.engine.togglePause()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }
}
