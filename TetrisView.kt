package com.tetris.game

import android.content.Context
import android.graphics.*
import android.view.SurfaceHolder
import android.view.SurfaceView

class TetrisView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    val engine = GameEngine()
    private var gameThread: GameThread? = null

    // Paints
    private val bgPaint = Paint().apply { color = Color.parseColor("#0D0D1A") }
    private val gridPaint = Paint().apply {
        color = Color.parseColor("#1A1A33")
        style = Paint.Style.STROKE
        strokeWidth = 0.5f
    }
    private val ghostPaint = Paint().apply {
        color = Color.WHITE
        alpha = 50
        style = Paint.Style.FILL
    }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
    }
    private val labelPaint = Paint().apply {
        color = Color.parseColor("#AAAACC")
        textSize = 28f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
    }
    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#CC000020")
    }
    private val overlayTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }
    private val subTextPaint = Paint().apply {
        color = Color.parseColor("#AAAACC")
        textSize = 36f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    // Layout dims computed after surface created
    private var cellSize = 0f
    private var boardLeft = 0f
    private var boardTop = 0f
    private var boardWidth = 0f
    private var boardHeight = 0f
    private var panelLeft = 0f

    var onScoreChanged: ((Long, Int, Int) -> Unit)? = null

    init {
        holder.addCallback(this)
        setZOrderOnTop(false)
    }

    // ─── SurfaceHolder.Callback ──────────────────────────────────────────────

    override fun surfaceCreated(h: SurfaceHolder) {
        computeLayout()
        startThread()
    }

    override fun surfaceChanged(h: SurfaceHolder, fmt: Int, w: Int, h2: Int) {
        computeLayout()
    }

    override fun surfaceDestroyed(h: SurfaceHolder) {
        stopThread()
    }

    // ─── Layout ─────────────────────────────────────────────────────────────

    private fun computeLayout() {
        val w = width.toFloat()
        val h = height.toFloat()
        // Board occupies left ~65% of width
        val boardW = w * 0.62f
        cellSize = minOf(boardW / engine.cols, h / engine.rows)
        boardWidth = cellSize * engine.cols
        boardHeight = cellSize * engine.rows
        boardLeft = (w * 0.02f)
        boardTop = (h - boardHeight) / 2f
        panelLeft = boardLeft + boardWidth + w * 0.03f
    }

    // ─── Game Thread ─────────────────────────────────────────────────────────

    private fun startThread() {
        gameThread?.quit()
        gameThread = GameThread(engine, holder, ::draw, ::notifyScore).also { it.start() }
    }

    private fun stopThread() {
        gameThread?.quit()
        gameThread = null
    }

    private fun notifyScore() {
        onScoreChanged?.invoke(engine.score, engine.lines, engine.level)
    }

    // ─── Draw ────────────────────────────────────────────────────────────────

    fun draw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()

        // Background
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        drawBoard(canvas)
        drawGhost(canvas)
        drawCurrentPiece(canvas)
        drawGrid(canvas)
        drawPanel(canvas)

        if (engine.isPaused && !engine.isGameOver) drawPauseOverlay(canvas)
        if (engine.isGameOver) drawGameOverOverlay(canvas)
    }

    private fun drawBoard(canvas: Canvas) {
        // Board background
        val bgRect = RectF(boardLeft, boardTop, boardLeft + boardWidth, boardTop + boardHeight)
        canvas.drawRect(bgRect, Paint().apply { color = Color.parseColor("#11112A") })

        // Locked cells
        for (r in 0 until engine.rows) {
            for (c in 0 until engine.cols) {
                val color = engine.board[r][c]
                if (color != 0) {
                    drawCell(canvas, r, c, color, 255)
                }
            }
        }
    }

    private fun drawGhost(canvas: Canvas) {
        val ghost = engine.ghostPiece()
        for ((r, c) in ghost.cells()) {
            if (r >= 0) drawCell(canvas, r, c, Color.WHITE, 40)
        }
    }

    private fun drawCurrentPiece(canvas: Canvas) {
        val piece = engine.current
        for ((r, c) in piece.cells()) {
            if (r >= 0) drawCell(canvas, r, c, piece.color(), 255)
        }
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int, color: Int, alpha: Int) {
        val left = boardLeft + col * cellSize
        val top = boardTop + row * cellSize
        val right = left + cellSize
        val bottom = top + cellSize
        val margin = cellSize * 0.04f

        val paint = Paint().apply {
            this.color = color
            this.alpha = alpha
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val rect = RectF(left + margin, top + margin, right - margin, bottom - margin)
        canvas.drawRoundRect(rect, cellSize * 0.1f, cellSize * 0.1f, paint)

        // Highlight (top-left shine)
        val shinePaint = Paint().apply {
            this.color = Color.WHITE
            this.alpha = (alpha * 0.35f).toInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val shineRect = RectF(left + margin, top + margin, right - margin - cellSize * 0.3f, top + margin + cellSize * 0.25f)
        canvas.drawRoundRect(shineRect, cellSize * 0.1f, cellSize * 0.1f, shinePaint)
    }

    private fun drawGrid(canvas: Canvas) {
        for (r in 0..engine.rows) {
            val y = boardTop + r * cellSize
            canvas.drawLine(boardLeft, y, boardLeft + boardWidth, y, gridPaint)
        }
        for (c in 0..engine.cols) {
            val x = boardLeft + c * cellSize
            canvas.drawLine(x, boardTop, x, boardTop + boardHeight, gridPaint)
        }
        // Board border
        val borderPaint = Paint().apply {
            color = Color.parseColor("#3333AA")
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRect(boardLeft, boardTop, boardLeft + boardWidth, boardTop + boardHeight, borderPaint)
    }

    private fun drawPanel(canvas: Canvas) {
        val panelW = width - panelLeft - 10f

        // NEXT label
        labelPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("NEXT", panelLeft, boardTop + 32f, labelPaint)

        // Next piece preview box
        val previewSize = cellSize * 4
        val previewTop = boardTop + 40f
        val previewRect = RectF(panelLeft, previewTop, panelLeft + previewSize, previewTop + previewSize)
        canvas.drawRect(previewRect, Paint().apply { color = Color.parseColor("#11112A") })
        canvas.drawRect(previewRect, Paint().apply {
            color = Color.parseColor("#3333AA")
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        })
        drawNextPiece(canvas, previewTop, previewSize)

        // Stats
        var statY = previewTop + previewSize + cellSize * 1.2f
        val statSpacing = cellSize * 1.5f

        fun drawStat(label: String, value: String) {
            canvas.drawText(label, panelLeft, statY, labelPaint)
            statY += 32f
            textPaint.textSize = 36f
            textPaint.textAlign = Paint.Align.LEFT
            canvas.drawText(value, panelLeft, statY, textPaint)
            statY += statSpacing
        }

        drawStat("SCORE", engine.score.toString())
        drawStat("LINES", engine.lines.toString())
        drawStat("LEVEL", engine.level.toString())

        // Controls hint
        statY += cellSize * 0.5f
        labelPaint.textSize = 22f
        val hints = listOf("← →  Move", "↑  Rotate", "↓  Soft drop", "⚡  Hard drop", "⏸  Pause")
        for (hint in hints) {
            canvas.drawText(hint, panelLeft, statY, labelPaint)
            statY += 28f
        }
        labelPaint.textSize = 28f
    }

    private fun drawNextPiece(canvas: Canvas, previewTop: Float, previewSize: Float) {
        val next = engine.next
        val cells = next.cells()
        val minR = cells.minOf { it.first }
        val maxR = cells.maxOf { it.first }
        val minC = cells.minOf { it.second }
        val maxC = cells.maxOf { it.second }
        val pieceH = (maxR - minR + 1) * cellSize
        val pieceW = (maxC - minC + 1) * cellSize
        val offsetR = (previewSize - pieceH) / 2f / cellSize
        val offsetC = (previewSize - pieceW) / 2f / cellSize

        val paint = Paint().apply {
            color = next.color()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        for ((r, c) in cells) {
            val adjR = r - minR + offsetR
            val adjC = c - minC + offsetC
            val left = panelLeft + adjC * cellSize
            val top = previewTop + adjR * cellSize
            val margin = cellSize * 0.05f
            canvas.drawRoundRect(
                RectF(left + margin, top + margin, left + cellSize - margin, top + cellSize - margin),
                cellSize * 0.1f, cellSize * 0.1f, paint
            )
        }
    }

    private fun drawPauseOverlay(canvas: Canvas) {
        canvas.drawRect(boardLeft, boardTop, boardLeft + boardWidth, boardTop + boardHeight,
            Paint().apply { color = Color.parseColor("#BB000020") })
        overlayTextPaint.color = Color.parseColor("#FFD700")
        canvas.drawText("PAUSA", boardLeft + boardWidth / 2, boardTop + boardHeight / 2 - 20f, overlayTextPaint)
        canvas.drawText("Toca para continuar", boardLeft + boardWidth / 2, boardTop + boardHeight / 2 + 40f, subTextPaint)
    }

    private fun drawGameOverOverlay(canvas: Canvas) {
        canvas.drawRect(boardLeft, boardTop, boardLeft + boardWidth, boardTop + boardHeight,
            Paint().apply { color = Color.parseColor("#CC000000") })
        overlayTextPaint.color = Color.parseColor("#FF4444")
        canvas.drawText("GAME OVER", boardLeft + boardWidth / 2, boardTop + boardHeight / 2 - 60f, overlayTextPaint)
        subTextPaint.textSize = 32f
        canvas.drawText("Puntuación: ${engine.score}", boardLeft + boardWidth / 2, boardTop + boardHeight / 2, subTextPaint)
        subTextPaint.color = Color.parseColor("#FFD700")
        canvas.drawText("Toca para reiniciar", boardLeft + boardWidth / 2, boardTop + boardHeight / 2 + 60f, subTextPaint)
        subTextPaint.color = Color.parseColor("#AAAACC")
        subTextPaint.textSize = 36f
    }

    // ─── Public controls ─────────────────────────────────────────────────────

    fun onTap() {
        if (engine.isGameOver) {
            engine.reset()
        } else {
            engine.togglePause()
        }
    }

    fun restartGame() {
        engine.reset()
    }
}
