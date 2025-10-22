package com.eggsa.enois.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.eggsa.enois.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

private val EggSafeColorScheme = lightColorScheme(
    primary = Color(0xFF4ACFAC),
    secondary = Color(0xFFFFD93D),
    tertiary = Color(0xFFC88752),
    background = Color(0xFFFFF9E6),
    surface = Color(0xFFFFF9E6),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val EggSafeTypography = androidx.compose.material3.Typography(
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    )
    // Add more styles as needed
)

@Composable
fun EggSafeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EggSafeColorScheme,
        typography = EggSafeTypography,
        content = content
    )
}