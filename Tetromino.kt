package com.tetris.game

import android.graphics.Color

enum class TetrominoType {
    I, O, T, S, Z, J, L
}

data class Tetromino(
    val type: TetrominoType,
    val rotation: Int = 0,
    val x: Int = 0,
    val y: Int = 0
) {
    companion object {
        // Each piece: list of rotations, each rotation: list of (row, col) cells
        val SHAPES: Map<TetrominoType, List<List<Pair<Int, Int>>>> = mapOf(
            TetrominoType.I to listOf(
                listOf(Pair(1,0), Pair(1,1), Pair(1,2), Pair(1,3)),
                listOf(Pair(0,2), Pair(1,2), Pair(2,2), Pair(3,2)),
                listOf(Pair(2,0), Pair(2,1), Pair(2,2), Pair(2,3)),
                listOf(Pair(0,1), Pair(1,1), Pair(2,1), Pair(3,1))
            ),
            TetrominoType.O to listOf(
                listOf(Pair(0,0), Pair(0,1), Pair(1,0), Pair(1,1)),
                listOf(Pair(0,0), Pair(0,1), Pair(1,0), Pair(1,1)),
                listOf(Pair(0,0), Pair(0,1), Pair(1,0), Pair(1,1)),
                listOf(Pair(0,0), Pair(0,1), Pair(1,0), Pair(1,1))
            ),
            TetrominoType.T to listOf(
                listOf(Pair(0,1), Pair(1,0), Pair(1,1), Pair(1,2)),
                listOf(Pair(0,1), Pair(1,1), Pair(1,2), Pair(2,1)),
                listOf(Pair(1,0), Pair(1,1), Pair(1,2), Pair(2,1)),
                listOf(Pair(0,1), Pair(1,0), Pair(1,1), Pair(2,1))
            ),
            TetrominoType.S to listOf(
                listOf(Pair(0,1), Pair(0,2), Pair(1,0), Pair(1,1)),
                listOf(Pair(0,1), Pair(1,1), Pair(1,2), Pair(2,2)),
                listOf(Pair(1,1), Pair(1,2), Pair(2,0), Pair(2,1)),
                listOf(Pair(0,0), Pair(1,0), Pair(1,1), Pair(2,1))
            ),
            TetrominoType.Z to listOf(
                listOf(Pair(0,0), Pair(0,1), Pair(1,1), Pair(1,2)),
                listOf(Pair(0,2), Pair(1,1), Pair(1,2), Pair(2,1)),
                listOf(Pair(1,0), Pair(1,1), Pair(2,1), Pair(2,2)),
                listOf(Pair(0,1), Pair(1,0), Pair(1,1), Pair(2,0))
            ),
            TetrominoType.J to listOf(
                listOf(Pair(0,0), Pair(1,0), Pair(1,1), Pair(1,2)),
                listOf(Pair(0,1), Pair(0,2), Pair(1,1), Pair(2,1)),
                listOf(Pair(1,0), Pair(1,1), Pair(1,2), Pair(2,2)),
                listOf(Pair(0,1), Pair(1,1), Pair(2,0), Pair(2,1))
            ),
            TetrominoType.L to listOf(
                listOf(Pair(0,2), Pair(1,0), Pair(1,1), Pair(1,2)),
                listOf(Pair(0,1), Pair(1,1), Pair(2,1), Pair(2,2)),
                listOf(Pair(1,0), Pair(1,1), Pair(1,2), Pair(2,0)),
                listOf(Pair(0,0), Pair(0,1), Pair(1,1), Pair(2,1))
            )
        )

        val COLORS: Map<TetrominoType, Int> = mapOf(
            TetrominoType.I to Color.parseColor("#00F0F0"), // Cyan
            TetrominoType.O to Color.parseColor("#F0F000"), // Yellow
            TetrominoType.T to Color.parseColor("#A000F0"), // Purple
            TetrominoType.S to Color.parseColor("#00F000"), // Green
            TetrominoType.Z to Color.parseColor("#F00000"), // Red
            TetrominoType.J to Color.parseColor("#0000F0"), // Blue
            TetrominoType.L to Color.parseColor("#F0A000")  // Orange
        )

        fun random(): Tetromino {
            val type = TetrominoType.values().random()
            return Tetromino(type = type, rotation = 0, x = 3, y = 0)
        }
    }

    fun cells(): List<Pair<Int, Int>> {
        val shape = SHAPES[type]!![rotation % 4]
        return shape.map { (r, c) -> Pair(y + r, x + c) }
    }

    fun color(): Int = COLORS[type]!!

    fun rotated(): Tetromino = copy(rotation = (rotation + 1) % 4)
    fun movedLeft(): Tetromino = copy(x = x - 1)
    fun movedRight(): Tetromino = copy(x = x + 1)
    fun movedDown(): Tetromino = copy(y = y + 1)
}
