package com.tetris.game

import android.view.MotionEvent
import kotlin.math.abs

class GestureHandler(
    private val onMoveLeft: () -> Unit,
    private val onMoveRight: () -> Unit,
    private val onRotate: () -> Unit,
    private val onSoftDrop: () -> Unit,
    private val onHardDrop: () -> Unit,
    private val onTap: () -> Unit
) {
    private var startX = 0f
    private var startY = 0f
    private var startTime = 0L
    private var lastMoveX = 0f

    private val SWIPE_THRESHOLD = 40f
    private val TAP_TIME = 200L
    private val MOVE_STEP = 50f      // pixels per lateral move trigger
    private val HARD_DROP_SWIPE = 120f

    fun onTouch(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                lastMoveX = event.x
                startTime = System.currentTimeMillis()
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastMoveX
                val totalDy = event.y - startY

                // Continuous lateral movement
                if (abs(dx) > MOVE_STEP && abs(totalDy) < SWIPE_THRESHOLD * 2) {
                    if (dx > 0) onMoveRight() else onMoveLeft()
                    lastMoveX = event.x
                }

                // Soft drop: dragging downward
                if (totalDy > SWIPE_THRESHOLD && abs(event.x - startX) < SWIPE_THRESHOLD) {
                    onSoftDrop()
                    startY = event.y  // reset so it keeps droping
                }
            }

            MotionEvent.ACTION_UP -> {
                val dx = event.x - startX
                val dy = event.y - startY
                val dt = System.currentTimeMillis() - startTime

                when {
                    // Hard drop: fast strong swipe down
                    dy > HARD_DROP_SWIPE && abs(dx) < SWIPE_THRESHOLD && dt < 350 -> {
                        onHardDrop()
                    }
                    // Swipe up: rotate
                    dy < -SWIPE_THRESHOLD && abs(dx) < abs(dy) -> {
                        onRotate()
                    }
                    // Tap: rotate (quick tap anywhere)
                    dt < TAP_TIME && abs(dx) < SWIPE_THRESHOLD && abs(dy) < SWIPE_THRESHOLD -> {
                        onTap()
                    }
                }
            }
        }
        return true
    }
}
