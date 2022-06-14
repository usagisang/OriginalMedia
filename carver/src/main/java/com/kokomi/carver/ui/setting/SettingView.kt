package com.kokomi.carver.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun SettingContentView(vm: SettingViewModel) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        SettingBox(title = "分辨率", items = vm.supportedQuality, stateFlow = vm.selectedResolvingPower)
    }
}

@Composable
fun SettingBox(title: String, items: List<String>, stateFlow: MutableStateFlow<Int>) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 20.dp),
            color = MidGray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Column(
            modifier = Modifier
                .clip(RoundRectShape)
                .background(LightGray)
        ) {
            ItemBox(item = items, stateFlow = stateFlow)
        }
    }
}

@Composable
fun ItemBox(item: List<String>, stateFlow: MutableStateFlow<Int>) {
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
