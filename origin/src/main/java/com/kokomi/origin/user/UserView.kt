package com.kokomi.origin.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kokomi.origin.R
import kotlinx.coroutines.flow.MutableStateFlow

private val SoftBlack = Color(0xFF212121)
private val SoftWhite = Color(0xFFF5F5F5)
private val MidGray = Color(0xFF787878)
private val Transparent = Color(0x00000000)
private val FieldBackground = Color(0xFFBDBDBD)
private val FieldTextColor = Color(0xCC3C3C3C)
private val HintTextColor = Color(0x993C3C3C)

@Composable
internal fun UserContentView(vm: UserViewModel) {
    val user by vm.user.collectAsState()
    if (user.userId > 0) UserView(vm = vm)
    else LoginView(vm = vm)
}

@Composable
private fun UserView(vm: UserViewModel) {
    Box {
        TopBackgroundImage()
        Column(
            modifier = Modifier.padding(top = 200.dp)
        ) {
            Text(
                text = "欢迎你",
                color = SoftWhite,
                fontSize = 52.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth()
            )
            BigText(text = vm.user.value.nickName)
            Spacer(modifier = Modifier.weight(1f))
            BottomButton(text = "退出登录") { vm.logout() }
        }
    }
}

@Composable
private fun LoginView(vm: UserViewModel) {
    Box {
        TopBackgroundImage()
        Column(
            modifier = Modifier
                .padding(top = 200.dp)
        ) {
            Text(
                text = "登录",
                color = SoftWhite,
                fontSize = 52.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth()
            )
            BigText(text = "用户名")
            UserNameScanBox(stateFlow = vm.userName)
            BigText(text = "密码")
            PasswordScanBox(stateFlow = vm.password)
            Spacer(modifier = Modifier.weight(1f))
            BottomButton(text = "登录") { vm.login() }
        }
    }
}

@Composable
private fun TopBackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.ic_app),
        modifier = Modifier
            .blur(25.dp)
            .fillMaxWidth()
            .height(400.dp)
            .width(400.dp)
            .offset(y = (-80).dp),
        alignment = Alignment.TopCenter,
        contentScale = ContentScale.FillBounds,
        contentDescription = null
    )
}

@Composable
private fun UserNameScanBox(stateFlow: MutableStateFlow<String>) {
    val value by stateFlow.collectAsState()
    TextField(
        value = value,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = FieldBackground,
            cursorColor = MidGray,
            focusedIndicatorColor = Transparent,
            unfocusedIndicatorColor = Transparent,
            textColor = FieldTextColor
        ),
        textStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        ),
        placeholder = {
            Text(
                text = "用户名",
                color = HintTextColor,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        },
        onValueChange = { text ->
            stateFlow.value = text
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        visualTransformation = VisualTransformation.None,
        singleLine = true,
        modifier = Modifier
            .padding(horizontal = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
    )
}

@Composable
private fun PasswordScanBox(stateFlow: MutableStateFlow<String>) {
    val value by stateFlow.collectAsState()
    TextField(
        value = value,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = FieldBackground,
            cursorColor = MidGray,
            focusedIndicatorColor = Transparent,
            unfocusedIndicatorColor = Transparent,
            textColor = FieldTextColor
        ),
        textStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        ),
        placeholder = {
            Text(
                text = "密码",
                color = HintTextColor,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        },
        onValueChange = { text ->
            stateFlow.value = text
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation('*'),
        singleLine = true,
        modifier = Modifier
            .padding(horizontal = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
    )
}

@Composable
private fun BottomButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(8.dp)),
        colors = buttonColors(
            backgroundColor = SoftWhite,
            contentColor = SoftWhite
        )
    ) {
        Text(
            text = text,
            color = SoftBlack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BigText(text: String) {
    Text(
        text = text,
        color = SoftWhite,
        textAlign = TextAlign.End,
        modifier = Modifier
            .padding(horizontal = 48.dp)
            .padding(top = 24.dp, bottom = 8.dp)
            .fillMaxWidth(),
        fontSize = 28.sp,
        fontWeight = FontWeight.Medium
    )
}