package com.kokomi.carver.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun SettingContentView(vm: SettingViewModel) {
    val impl by vm.selectedImpl.collectAsState()
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        SelectBox(title = "相机模式", items = listOf("普通模式", "高级模式"), stateFlow = vm.selectedImpl)
        if (impl == 0) {
            SelectBox(title = "分辨率", items = vm.supportedQuality, stateFlow = vm.selectedQuality)
        } else {
            ScanBox(item = "视频帧率(帧/秒)", stateFlow = vm.videoFrameRate)
            ScanBox(item = "视频码率(比特/秒)", stateFlow = vm.bitRate)
            ScanBox(item = "I帧间隔(秒)", stateFlow = vm.iFrameInterval)
            ScanBox(item = "音频采样率(赫兹)", stateFlow = vm.audioSampleRate)
            ScanBox(item = "音频比特率(比特/秒)", stateFlow = vm.audioBitRate)
            ScanBox(item = "音频通道数", stateFlow = vm.audioChannelCount)
        }
    }
}

@Composable
fun SelectBox(title: String, items: List<String>, stateFlow: MutableStateFlow<Int>) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 20.dp),
            color = MidGray,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Column(
            modifier = Modifier
                .clip(RoundRectShape)
                .background(LightGray)
        ) {
            SelectItemBox(item = items, stateFlow = stateFlow)
        }
    }
}

@Composable
private fun SelectItemBox(item: List<String>, stateFlow: MutableStateFlow<Int>) {
    val selected by stateFlow.collectAsState()
    for (i in item.indices) {
        Text(
            text = item[i],
            modifier = Modifier
                .fillMaxWidth()
                .background(if (i == selected) MidGray else Transparent)
                .clickable { stateFlow.value = i }
                .padding(horizontal = 24.dp, vertical = 12.dp),
            color = SoftWhite,
            fontSize = 18.sp
        )
    }
}

@Composable
fun ScanBox(item: String, stateFlow: MutableStateFlow<Int>) {
    val value by stateFlow.collectAsState()
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(RoundRectShape)
            .background(LightGray)
    ) {
        TextField(
            value = if (value < 0) "" else value.toString(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Transparent,
                cursorColor = MidGray,
                focusedIndicatorColor = Transparent,
                unfocusedIndicatorColor = Transparent,
                textColor = SoftWhite
            ),
            textStyle = TextStyle(
                textAlign = TextAlign.End,
                fontSize = 20.sp
            ),
            leadingIcon = {
                Text(
                    text = item,
                    color = MidGray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 18.sp
                )
            },
            placeholder = {
                Text(
                    text = "默认值",
                    color = SoftWhite,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontSize = 18.sp
                )
            },
            onValueChange = { text ->
                if (text.isBlank()) {
                    stateFlow.value = -1
                    return@TextField
                }
                try {
                    stateFlow.value = text.toInt()
                } catch (e: NumberFormatException) {
                    stateFlow.value = -1
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = VisualTransformation.None,
            singleLine = true,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )
    }
}
