package top.gochiusa.originalmedia.video.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.gochiusa.originalmedia.video.R
import top.gochiusa.originalmedia.video.entity.Video
import top.gochiusa.originalmedia.video.entity.VideoState
import top.gochiusa.originalmedia.video.theme.HalfAlphaWhite
import top.gochiusa.originalmedia.video.theme.SemiWhite

internal fun Modifier.clickableNoIndication(
    onClick: () -> Unit = {}
): Modifier {
    return this.clickable(
        onClick = onClick,
        indication = null,
        interactionSource = MutableInteractionSource()
    )
}


@Composable
internal fun ProgressText(
    modifier: Modifier = Modifier,
    progress: String,
    duration: String
) {
    Text(
        text = getProgressText(progress, duration),
        letterSpacing = 1.sp,
        modifier = modifier
    )
}

private fun getProgressText(progress: String, duration: String): AnnotatedString {
    return AnnotatedString.Builder().run {
        pushStyle(
            SpanStyle(
                fontSize = 28.sp,
                color = SemiWhite,
                fontWeight = FontWeight.Bold
            )
        )
        append(progress)
        pop()
        pushStyle(
            SpanStyle(
                fontSize = 28.sp,
                color = SemiWhite,
                fontWeight = FontWeight.Bold
            )
        )
        append("   /   $duration")
        toAnnotatedString()
    }
}

@Composable
internal fun VideoTitle(
    modifier: Modifier,
    video: Video?
) {
    Text(
        text = video?.title ?: "",
        modifier = modifier,
        fontSize = 24.sp,
        color = SemiWhite,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
internal fun VideoStateComponents(
    modifier: Modifier,
    videoState: VideoState,
    onRetryClick: () -> Unit
) {
    when(videoState) {
        is VideoState.Loading -> {
            CircularProgressIndicator(
                modifier = modifier,
                color = HalfAlphaWhite
            )
        }
        is VideoState.Pause -> {
            Icon(
                painter = painterResource(id = R.drawable.video_ic_horizontal_play),
                contentDescription = null,
                modifier = modifier.size(100.dp),
                tint = HalfAlphaWhite
            )
        }
        is VideoState.Error -> {
            ErrorComponents(
                modifier = modifier,
                onRetryClick = onRetryClick
            )
        }
        else -> {}
    }
}

/**
 * 因为某些原因导致视频加载/播放失败时显示重试链接文字
 */
@Composable
internal fun ErrorComponents(
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit,
) {
    val message = stringResource(id = R.string.video_video_reload_tip)
    val underlineStartIndex = 8
    val retryTag = "retry"
    val annotatedString = AnnotatedString.Builder().run {
        append(message)
        addStyle(
            style = SpanStyle(
                color = HalfAlphaWhite
            ),
            start = 0,
            end = underlineStartIndex
        )
        addStyle(
            style = SpanStyle(
                color = Color.White,
                textDecoration = TextDecoration.Underline,
            ),
            start = underlineStartIndex,
            end = message.length
        )
        addStringAnnotation(
            tag = retryTag,
            annotation = "",
            start = underlineStartIndex,
            end = message.length
        )
        toAnnotatedString()
    }
    ClickableText(
        text = annotatedString,
        onClick = {
            if (it >= underlineStartIndex) {
                onRetryClick()
            }
        },
        modifier = modifier
    )
}