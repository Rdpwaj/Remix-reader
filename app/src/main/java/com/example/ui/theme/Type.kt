package com.example.ui.theme

import android.content.Context
import android.graphics.Typeface
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.R
import java.util.concurrent.ConcurrentHashMap

val CairoFontFamily = FontFamily(Font(R.font.cairo))
val TajawalFontFamily = FontFamily(Font(R.font.tajawal))
val AmiriFontFamily = FontFamily(Font(R.font.amiri))
val NotoNaskhArabicFontFamily = FontFamily(Font(R.font.noto_naskh_arabic))
val NotoKufiArabicFontFamily = FontFamily(Font(R.font.noto_kufi_arabic))
val AlmaraiFontFamily = FontFamily(Font(R.font.almarai))
val ScheherazadeFontFamily = FontFamily(Font(R.font.scheherazade_new))
val InterFontFamily = FontFamily(Font(R.font.inter))

private val baseTypefaceCache = ConcurrentHashMap<String, Typeface>()

fun resolveReaderTypeface(context: Context, fontKey: String, isBold: Boolean): Typeface {
    val style = if (isBold) Typeface.BOLD else Typeface.NORMAL
    val base = baseTypefaceCache.getOrPut(fontKey) {
        val resId = when (fontKey) {
            "Cairo" -> R.font.cairo
            "Tajawal" -> R.font.tajawal
            "Amiri" -> R.font.amiri
            "Noto Naskh Arabic" -> R.font.noto_naskh_arabic
            "Noto Kufi" -> R.font.noto_kufi_arabic
            "Almarai" -> R.font.almarai
            "Scheherazade" -> R.font.scheherazade_new
            "Inter" -> R.font.inter
            else -> 0
        }
        if (resId != 0) {
            runCatching { ResourcesCompat.getFont(context, resId) }.getOrNull() ?: Typeface.DEFAULT
        } else {
            when (fontKey) {
                "Serif" -> Typeface.SERIF
                "Roboto" -> Typeface.SANS_SERIF
                else -> Typeface.DEFAULT
            }
        }
    }
    return Typeface.create(base, style)
}

fun resolveReaderFontFamily(fontKey: String): FontFamily {
    return when (fontKey) {
        "Cairo" -> CairoFontFamily
        "Tajawal" -> TajawalFontFamily
        "Amiri" -> AmiriFontFamily
        "Noto Naskh Arabic" -> NotoNaskhArabicFontFamily
        "Noto Kufi" -> NotoKufiArabicFontFamily
        "Almarai" -> AlmaraiFontFamily
        "Scheherazade" -> ScheherazadeFontFamily
        "Inter" -> InterFontFamily
        "Roboto" -> FontFamily.SansSerif
        "Serif" -> FontFamily.Serif
        else -> FontFamily.Default
    }
}

val AvailableReaderFonts = listOf(
    "System" to "خط النظام السريع (افتراضي)",
    "Cairo" to "خط القاهرة (Cairo)",
    "Tajawal" to "خط تجوال (Tajawal)",
    "Amiri" to "خط أميري الروائي (Amiri)",
    "Noto Naskh Arabic" to "نوتو نسخ (Noto Naskh)",
    "Noto Kufi" to "نوتو كوفي (Noto Kufi)",
    "Almarai" to "خط المراعي (Almarai)",
    "Scheherazade" to "شهرزاد التراثي (Scheherazade)",
    "Inter" to "إنتر (Inter)",
    "Roboto" to "روبوتو (Roboto)",
    "Serif" to "سيريف (Serif)"
)

// Use hardware-cached system font (FontFamily.Default) for app UI chrome so buttons, cards, and lists
// render with zero TTF shaping overhead on low-spec devices like Galaxy M02s.
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)
