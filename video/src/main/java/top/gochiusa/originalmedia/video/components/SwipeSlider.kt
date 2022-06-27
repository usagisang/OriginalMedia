package top.gochiusa.originalmedia.video.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


@Composable
internal fun SwipeSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
    color: Color = Color.Black
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            onValueChangeFinished = onValueChangeFinished,
            modifier = Modifier.alpha(0F).fillMaxWidth()
        )
        RoundLinearProgressIndicator(
            progress = value,
            progressRange = valueRange,
            backgroundColor = Color.Transparent,
            color = color,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RoundLinearProgressIndicator(
    /*@FloatRange(from = 0.0, to = 1.0)*/
    progress: Float,
    progressRange: ClosedFloatingPointRange<Float> = 0f..1f,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = Color.Transparent
) {
    Canvas(
        modifier.progressSemantics(progress, progressRange).size(240.dp, 4.dp)
    ) {
        val strokeWidth = size.height
        val endFraction = if (progressRange.endInclusive == 0F) 0F else
            progress / progressRange.endInclusive
        drawLinearIndicatorBackground(backgroundColor, strokeWidth)
        drawLinearIndicator(0f, endFraction, color, strokeWidth)
    }
}

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float
) {
    val cap = StrokeCap.Round
    val width = size.width
    val height = size.height
    // Start drawing from the vertical center of the stroke
    val yOffset = height / 2

    val roundedCapOffset = strokeWidth / 2
    val isLtr = layoutDirection == LayoutDirection.Ltr

    val ratio = if (isLtr) 1 else -1

    val barStart = (if (isLtr) startFraction else 1f - endFraction) * width// + roundedCapOffset * ratio

    val barEnd = (if (isLtr) endFraction else 1f - startFraction) * width - roundedCapOffset * ratio

    // Progress line
    drawLine(color, Offset(barStart, yOffset), Offset(barEnd, yOffset), strokeWidth, cap)
}

private fun DrawScope.drawLinearIndicatorBackground(
    color: Color,
    strokeWidth: Float
) = drawLinearIndicator(0f, 1f, color, strokeWidth)