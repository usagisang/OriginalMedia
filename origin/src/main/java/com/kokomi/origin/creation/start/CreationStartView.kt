package com.kokomi.origin.creation.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kokomi.origin.R
import com.kokomi.origin.explore.tabBarHeight
import com.kokomi.origin.util.pxToDp

private val SoftBlack = Color(0xFF212121)
private val SoftWhite = Color(0xFFF5F5F5)
private val SoftGray = Color(0xFFBDBDBD)

@Composable
internal fun CreationStartContentView(
    statusBarHeight: Int,
    clickableStart: () -> Unit,
    clickableEnd: () -> Unit
) {
    val tabBarHeight by tabBarHeight.collectAsState()
    Column {
        Box(
            modifier = Modifier
                .height(tabBarHeight.pxToDp)
                .background(SoftGray)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 32.dp))
                    .background(SoftBlack)
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = clickableStart)
        ) {
            ImageCreation()
        }
        Box(modifier = Modifier.height((tabBarHeight + statusBarHeight).pxToDp)) {
            Row {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SoftGray)
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 32.dp))
                            .background(SoftBlack)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(SoftWhite)
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomEnd = 32.dp))
                            .background(SoftBlack)
                    )
                }
            }
            Text(
                text = "选择创作类型",
                color = SoftWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = clickableEnd)
        ) {
            VideoCreation()
        }
        Box(
            modifier = Modifier
                .height(tabBarHeight.pxToDp)
                .background(SoftWhite)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topEnd = 32.dp))
                    .background(SoftBlack)
            )
        }
    }
}

@Composable
private fun RowScope.ImageCreation() {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(topEnd = 36.dp, bottomEnd = 32.dp))
            .background(SoftGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.weight(2f))
            Image(
                painter = painterResource(id = R.drawable.ic_image_creation),
                alignment = Alignment.Center,
                modifier = Modifier
                    .weight(2.5f)
                    .fillMaxHeight(),
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_font_creation),
                alignment = Alignment.Center,
                modifier = Modifier
                    .weight(2.5f)
                    .fillMaxHeight()
                    .padding(start = 5.dp),
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(2f))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = "图文创作",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                color = SoftBlack,
                textAlign = TextAlign.Center,
                fontSize = 48.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
    val tabBarHeight by tabBarHeight.collectAsState()
    Spacer(modifier = Modifier.width(tabBarHeight.pxToDp))
}

@Composable
private fun RowScope.VideoCreation() {
    val tabBarHeight by tabBarHeight.collectAsState()
    Spacer(modifier = Modifier.width(tabBarHeight.pxToDp))
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp))
            .background(SoftWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.weight(2f))
            Image(
                painter = painterResource(id = R.drawable.ic_recording_creation),
                alignment = Alignment.Center,
                modifier = Modifier
                    .weight(2.5f)
                    .fillMaxHeight(),
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_video_creation),
                alignment = Alignment.Center,
                modifier = Modifier
                    .weight(2.5f)
                    .fillMaxHeight(),
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(2f))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = "视频创作",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                color = SoftBlack,
                textAlign = TextAlign.Center,
                fontSize = 48.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}