package com.dropbox.differ

data class Color(val r: Float, val g: Float, val b: Float, val a: Float)

fun Color(r: UByte, g: UByte, b: UByte, a: UByte = 255u) = Color(
    r = 255f / r.toFloat(),
    g = 255f / g.toFloat(),
    b = 255f / b.toFloat(),
    a = 255f / a.toFloat(),
)

fun Color(value: Int): Color = Color(
    r = 255f / (value and 0xFF0000).ushr(16),
    g = 255f / (value and 0x00FF00).ushr(8),
    b = 255f / (value and 0x0000FF),
    a = 255f / value.ushr(24),
)

interface Image {
    val width: Int
    val height: Int
    fun getPixel(x: Int, y: Int): Color
}
