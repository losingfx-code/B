package com.tetris.game

class GameEngine(
    val cols: Int = 10,
    val rows: Int = 20
) {
    // 0 = empty, otherwise stores the color int of the locked piece
    val board: Array<IntArray> = Array(rows) { IntArray(cols) { 0 } }

    var current: Tetromino = Tetromino.random()
    var next: Tetromino = Tetromino.random()
    var score: Long = 0L
    var lines: Int = 0
    var level: Int = 1
    var isGameOver: Boolean = false
    var isPaused: Boolean = false

    // Points per lines cleared (classic Tetris scoring)
    private val linePoints = intArrayOf(0, 100, 300, 500, 800)

    fun dropInterval(): Long {
        // milliseconds between drops, decreases with level
        return maxOf(100L, 1000L - (level - 1) * 90L)
    }

    fun moveLeft() {
        if (isPaused || isGameOver) return
        val moved = current.movedLeft()
        if (isValid(moved)) current = moved
    }

    fun moveRight() {
        if (isPaused || isGameOver) return
        val moved = current.movedRight()
        if (isValid(moved)) current = moved
    }

    fun rotate() {
        if (isPaused || isGameOver) return
        val rotated = current.rotated()
        if (isValid(rotated)) {
            current = rotated
            return
        }
        // Wall kick: try shifting left/right by 1 or 2
        for (kick in listOf(-1, 1, -2, 2)) {
            val kicked = rotated.copy(x = rotated.x + kick)
            if (isValid(kicked)) {
                current = kicked
                return
            }
        }
    }

    fun softDrop() {
        if (isPaused || isGameOver) return
        val moved = current.movedDown()
        if (isValid(moved)) {
            current = moved
            score += 1
        } else {
            lockPiece()
        }
    }

    fun hardDrop() {
        if (isPaused || isGameOver) return
        var dropped = current
        var dropCount = 0
        while (isValid(dropped.movedDown())) {
            dropped = dropped.movedDown()
            dropCount++
        }
        score += dropCount * 2
        current = dropped
        lockPiece()
    }

    // Called on each game tick
    fun tick() {
        if (isPaused || isGameOver) return
        val moved = current.movedDown()
        if (isValid(moved)) {
            current = moved
        } else {
            lockPiece()
        }
    }

    fun ghostPiece(): Tetromino {
        var ghost = current
        while (isValid(ghost.movedDown())) {
            ghost = ghost.movedDown()
        }
        return ghost
    }

    private fun lockPiece() {
        val color = current.color()
        for ((r, c) in current.cells()) {
            if (r < 0 || r >= rows || c < 0 || c >= cols) {
                isGameOver = true
                return
            }
            board[r][c] = color
        }
        // Check game over: any cell in top row filled
        if (current.cells().any { (r, _) -> r < 0 }) {
            isGameOver = true
            return
        }

        val cleared = clearLines()
        if (cleared > 0) {
            score += linePoints[cleared] * level
            lines += cleared
            level = (lines / 10) + 1
        }

        // Spawn next piece
        current = next.copy(x = 3, y = 0)
        next = Tetromino.random()

        // Check if spawn position is valid
        if (!isValid(current)) {
            isGameOver = true
        }
    }

    private fun clearLines(): Int {
        var cleared = 0
        val newBoard = Array(rows) { IntArray(cols) { 0 } }
        var writeRow = rows - 1
        for (r in rows - 1 downTo 0) {
            if (board[r].any { it == 0 }) {
                newBoard[writeRow] = board[r].copyOf()
                writeRow--
            } else {
                cleared++
            }
        }
        for (r in 0 until rows) board[r] = newBoard[r].copyOf()
        return cleared
    }

    private fun isValid(piece: Tetromino): Boolean {
        for ((r, c) in piece.cells()) {
            if (c < 0 || c >= cols) return false
            if (r >= rows) return false
            if (r >= 0 && board[r][c] != 0) return false
        }
        return true
    }

    fun togglePause() {
        if (!isGameOver) isPaused = !isPaused
    }

    fun reset() {
        for (r in 0 until rows) board[r].fill(0)
        current = Tetromino.random()
        next = Tetromino.random()
        score = 0L
        lines = 0
        level = 1
        isGameOver = false
        isPaused = false
    }
}
