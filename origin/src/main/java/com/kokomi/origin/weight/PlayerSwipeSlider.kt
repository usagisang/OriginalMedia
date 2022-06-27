package com.kokomi.origin.weight

import android.content.Context
import android.util.AttributeSet
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kokomi.origin.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.listener.EventListenerAdapter

internal class PlayerSwipeSlider
@JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
): AbstractComposeView(context, attr, defStyle) {

    private var player by mutableStateOf<Player?>(null)

    private var dragListener: OnDragSliderListener? = null

    private var dragging by mutableStateOf(false)

    private var progressFlow = createProgressFlow()

    fun setOnDragSliderListener(listener: OnDragSliderListener?) {
        dragListener = listener
    }

    fun bindPlayer(player: Player?) {
        this.player = player
        progressFlow = createProgressFlow()
    }

    @Composable
    override fun Content() {
        var value by remember { mutableStateOf(0F) }
        val state by producePlayerState()

        val isLoading = (state == Player.STATE_LOADING || state == Player.STATE_BUFFERING)

        val alphaAnim = remember { Animatable(1F) }

        val strokeWidth by animateDpAsState(targetValue = if (dragging) 6.dp else 2.dp)

        LaunchedEffect(isLoading) {
            if (isLoading) {
                alphaAnim.animateTo(
                    targetValue = 0.3F,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 1500,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            } else if(alphaAnim.value < 1F) {
                alphaAnim.snapTo(1F)
            }
        }

        LaunchedEffect(player) {
            progressFlow.collectLatest {
                value = it
            }
        }

        SwipeSlider(
            modifier = Modifier.alpha(alphaAnim.value),
            value = value,
            onValueChange = {
                dragging = true
                value = it
                dragListener?.onDrag(false, it, player?.durationMs)
            },
            onValueChangeFinished = {
                player?.run {
                    seekTo((value * durationMs).toLong())
                }
                dragListener?.onDrag(true, value, player?.durationMs)
                dragging = false
            },
            color = colorResource(id = R.color.soft_white),
            strokeWidth = strokeWidth
        )
    }


    @Composable
    private fun producePlayerState(): State<Int> {
        return produceState(initialValue = player?.playerState ?: Player.STATE_INIT, key1 = player) {
            val realPlayer = player ?: return@produceState

            val eventListener = object : EventListenerAdapter {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    value = playbackState
                }
            }
            realPlayer.addEventListener(eventListener)

            awaitDispose {
                realPlayer.removeEventListener(eventListener)
            }
        }
    }

    private fun createProgressFlow(): Flow<Float> {
        return flow {
            while (player != null) {
                player?.run {
                    // 只有在状态为正在播放、未拖动时才更新进度
                    if (isPlaying() && !dragging) {
                        emit(
                            if (durationMs != 0L) {
                                (currentPositionMs.toFloat()) / (durationMs.toFloat())
                            } else {
                                0F
                            }
                        )
                    }
                }
                delay(200L)
            }
            if (player == null) {
                emit(0F)
            }
        }.conflate()
    }
}

internal fun interface OnDragSliderListener {
    /**
     * 当拖拽事件发生后回调，[end]为true代表这是一次拖拽事件的末尾
     *
     * [durationMs]是与该控件绑定的[Player]的时长元数据
     *
     * [value]是进度条的值，目前在0F~1F之间
     */
    fun onDrag(end: Boolean, value: Float, durationMs: Long?)
}

@Composable
private fun SwipeSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
    color: Color = Color.Black,
    strokeWidth: Dp,
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
            modifier = Modifier
                .alpha(0F)
                .fillMaxWidth(),
        )
        RoundLinearProgressIndicator(
            progress = value,
            progressRange = valueRange,
            backgroundColor = Color.Transparent,
            color = color,
            modifier = Modifier.fillMaxWidth(),
            height = strokeWidth
        )
    }
}

@Composable
private fun RoundLinearProgressIndicator(
    progress: Float,
    progressRange: ClosedFloatingPointRange<Float> = 0f..1f,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = Color.Transparent,
    height: Dp,
) {
    Canvas(
        modifier
            .progressSemantics(progress, progressRange)
            .size(240.dp, height)
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