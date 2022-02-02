package com.dropbox.differ

import kotlin.math.pow
import kotlin.math.sqrt

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

fun Color.distance(other: Color): Float {
  val r = (this.r - other.r).pow(2)
  val g = (this.g - other.g).pow(2)
  val b = (this.b - other.b).pow(2)
  val a = (this.a - other.a).pow(2)
  return sqrt(r + g + b + a)
}
