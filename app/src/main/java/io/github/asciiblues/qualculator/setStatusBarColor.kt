package io.github.asciiblues.qualculator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.asciiblues.qualculator.ui.theme.statusBarColor

fun isColorLight(color: Color): Boolean {
    return color.luminance() > 0.5
}

val darkIcons = isColorLight(statusBarColor)

@Composable
fun setStatusBarColor() {
    val systemUiController = rememberSystemUiController()

    // Set the status bar color
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }
}