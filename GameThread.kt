package com.tetris.game

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(
    private val engine: GameEngine,
    private val holder: SurfaceHolder,
    private val drawFn: (Canvas) -> Unit,
    private val notifyScore: () -> Unit
) : Thread("TetrisGameThread") {

    @Volatile private var running = true
    private var lastDropTime = System.currentTimeMillis()

    fun quit() {
        running = false
        try { join(500) } catch (_: InterruptedException) {}
    }

    override fun run() {
        lastDropTime = System.currentTimeMillis()
        while (running) {
            val now = System.currentTimeMillis()

            // Tick the engine at drop interval
            if (!engine.isPaused && !engine.isGameOver) {
                if (now - lastDropTime >= engine.dropInterval()) {
                    engine.tick()
                    notifyScore()
                    lastDropTime = now
                }
            }

            // Draw frame
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas() ?: continue
                synchronized(holder) {
                    drawFn(canvas)
                }
            } finally {
                canvas?.let { holder.unlockCanvasAndPost(it) }
            }

            // Target ~60 fps
            val elapsed = System.currentTimeMillis() - now
            val sleep = 16L - elapsed
            if (sleep > 0) sleepMillis(sleep)
        }
    }

    private fun sleepMillis(ms: Long) {
        try { sleep(ms) } catch (_: InterruptedException) {}
    }
}
