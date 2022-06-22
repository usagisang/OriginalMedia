package com.kokomi.origin.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kokomi.origin.R
import kotlinx.coroutines.flow.MutableStateFlow

private val SoftBlack = Color(0xFF212121)
private val SoftWhite = Color(0xFFF5F5F5)
private val LightGray = Color(0xFF323232)
private val MidGray = Color(0xFF787878)
private val Transparent = Color(0x00000000)

@Composable
fun UserContentView(vm: UserViewModel) {
    Column {
        Image(
            painter = painterResource(id = R.drawable.ic_app),
            modifier = Modifier
                .blur(25.dp)
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )
        Spacer(modifier = Modifier.padding(48.dp))
        ScanBox(item = "用户名", stateFlow = vm.userName)
        Spacer(modifier = Modifier.padding(32.dp))
        ScanBox(item = "密码", stateFlow = vm.password)
        Spacer(modifier = Modifier.padding(48.dp))
        Button(onClick = { vm.login() }) {
            Text(text = "登录")
        }
    }
}

@Composable
fun ScanBox(item: String, stateFlow: MutableStateFlow<String>) {
    val value by stateFlow.collectAsState()
    TextField(
        value = value,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = SoftWhite,
            cursorColor = MidGray,
            focusedIndicatorColor = Transparent,
            unfocusedIndicatorColor = Transparent,
            textColor = SoftBlack
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.End,
            fontSize = 20.sp
        ),
        placeholder = {
            Text(
                text = item,
                color = SoftBlack,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                fontSize = 18.sp
            )
        },
        onValueChange = { text ->
            stateFlow.value = text
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = VisualTransformation.None,
        singleLine = true,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
    )
}