package com.kokomi.origin.user

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
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
internal fun UserContentView(context: Context, vm: UserViewModel) {
    UserView(context = context, vm = vm)
}

@Composable
private fun UserView(context: Context, vm: UserViewModel) {
    val isLogged by vm.isLogged.collectAsState()
    val painter =
        BitmapPainter(
            context.getBlurBitmap(
                context.getBitmap(R.drawable.ic_app), 25f
            ).asImageBitmap()
        )
    Box {
        Image(
            painter = painter,
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
        Column(
            modifier = Modifier.padding(top = 200.dp)
        ) {
            Text(
                text = if (isLogged) "欢迎你" else "登录",
                color = SoftWhite,
                fontSize = 52.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth()
            )
            if (isLogged) LoggedView(vm = vm)
            else NotLoggedView(vm = vm)
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = if (isLogged) {
                    { vm.logout(context) }
                } else {
                    { vm.login(context) }
                },
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
                    text = if (isLogged) "退出登录" else "登录",
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
    }
}

@Composable
private fun NotLoggedView(vm: UserViewModel) {
    BigText(text = "用户名")
    TextFieldBox(hint = "用户名", stateFlow = vm.userName, hide = false)
    BigText(text = "密码")
    TextFieldBox(hint = "密码", stateFlow = vm.password, hide = true)
}

@Composable
private fun LoggedView(vm: UserViewModel) {
    BigText(text = vm.user.value.nickName)
}

@Composable
private fun TextFieldBox(hint: String, stateFlow: MutableStateFlow<String>, hide: Boolean) {
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
                text = hint,
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
        visualTransformation = if (!hide)
            VisualTransformation.None
        else PasswordVisualTransformation('*'),
        singleLine = true,
        modifier = Modifier
            .padding(horizontal = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
    )
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