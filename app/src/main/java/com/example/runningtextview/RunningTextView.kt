package com.example.runningtextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

internal const val FADING_EDGE_LENGTH = 30

/*
* This class is used to make the "running" text run always (because marquee will have a little pause and then repeat)
* It will only work when have a width (not unspecified)
* */
class RunningTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var repeatScrollX = 0
    private var posX = 0f
    private var posY = 0f
    private var currWidth = 0

    var runSpeed: Int = getDimension(R.dimen.run_speed)
    var desiredTextSpacing = getDimension(R.dimen.text_spacing)

    private var pausing = true
    private var needToReCalculate = true
    private var realText = ""
    private var resultText = ""
    private val stringBuilder = StringBuilder()

    private var textPaint = Paint().apply {
        flags       = Paint.ANTI_ALIAS_FLAG
        style       = Paint.Style.FILL
        strokeCap   = Paint.Cap.ROUND
        color       = getColorWithoutTheme(R.color.text_color)
        textSize    = getDimensionInFloat(R.dimen.text_size)
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        isHorizontalFadingEdgeEnabled = true
        setFadingEdgeLength(FADING_EDGE_LENGTH)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val h = textPaint.fontMetrics.bottom - textPaint.fontMetrics.top
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), h.toInt())

        // Prevent calculate too much by comparing oldW with new width
        // Don't use onSizeChanged(), as there is an unknown bug that even though the size has changed already
        //  but that method is not triggered (LinearLayout ?)
        if (!needToReCalculate && measuredWidth != currWidth) {
            needToReCalculate = true
        }
        currWidth = measuredWidth
    }

    private fun calculateRunningTextParams() {
        needToReCalculate = false

        val textWidth = textPaint.measureText(realText).toInt()
        val spacingString = computeSpacingString()

        // Trick text will be "x    x" where x = N of `realText    realText`
        // MUST ENSURE that there will always be a next realText (case realText too short, we may see 3 realText, for example)
        var additionalWidth = 0

        stringBuilder.setLength(0)
        // First, append the first text
        stringBuilder.append(realText)
        // Then, run a loop to create our RUNNING string, for case realText is long, this loop will
        //  also appends 1 time to create a string like "realText    realText"
        while (additionalWidth < measuredWidth) {
            stringBuilder.append(spacingString)
            stringBuilder.append(realText)
            additionalWidth += desiredTextSpacing + textWidth
        }

        resultText = stringBuilder.toString()

        // When reached the beginning of the next realText, repeat
        repeatScrollX = textWidth + desiredTextSpacing

        posX = FADING_EDGE_LENGTH.toFloat() // This will be our initial posX
        posY = (measuredHeight - (textPaint.fontMetrics.bottom + textPaint.fontMetrics.top)) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (needToReCalculate) {
            calculateRunningTextParams()
            needToReCalculate = false
        }

        canvas.drawText(resultText, posX, posY, textPaint)

        if (!pausing) {
            (posX - runSpeed).let {
                // Reset to the start of first realText so that have loop effect
                posX = if (it < -repeatScrollX) 0f else it
                postInvalidateOnAnimation()
            }
        }
    }

    override fun getLeftFadingEdgeStrength(): Float {
        // For more detail, pls read the source code. Return 1f to always have fading edge
        return 1f
    }

    override fun getRightFadingEdgeStrength(): Float {
        // For more detail, pls read the source code. Return 1f to always have fading edge
        return 1f
    }

    fun setTextPaint(paint: Paint) {
        textPaint = paint
    }

    /**
     * We compute a string "    " that has the width >= [desiredTextSpacing] and then change the textSpacing
     *  base on the comupted width
     *
     * @return a string contains only whitespace
     * */
    private fun computeSpacingString(): String {
        val widthOfSingleSpacing = textPaint.measureText(" ")
        var totalWidth = 0f
        var numberOfSpacing = 0
        while (totalWidth < desiredTextSpacing) {
            ++numberOfSpacing
            totalWidth += widthOfSingleSpacing
        }
        // Adjust the initial textSpacing, to avoid cases like:
        // WidthOfSingleSpacing = 5, textSpacing = 23 --> The repeatX pos will be wrong, and we will
        //  see the text not "running"
        desiredTextSpacing = totalWidth.toInt()

        var spacingString = ""
        for (i in 0 until numberOfSpacing) {
            spacingString += " "
        }
        return spacingString
    }

    /**
     * Must call this function to make the text "running"
     * @param text
     * */
    fun setRunningText(text: String) {
        if (text.isEmpty() || text.contentEquals(realText))
            return

        realText = text
        calculateRunningTextParams()
        invalidate()
    }

    fun pause() {
        pausing = true
    }

    fun resume() {
        pausing = false
        invalidate()
    }
}