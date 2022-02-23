package com.dropbox.differ

import kotlin.math.pow
import kotlin.math.sqrt

data class Color(val r: Float, val g: Float, val b: Float, val a: Float = 1.0f)

fun Color(r: UByte, g: UByte, b: UByte, a: UByte = 255u) = Color(
  r = r.toFloat() / 255f,
  g = g.toFloat() / 255f,
  b = b.toFloat() / 255f,
  a = a.toFloat() / 255f,
)

fun Color(r: Int, g: Int, b: Int, a: Int = 255) = Color(
  r = r.toFloat() / 255f,
  g = g.toFloat() / 255f,
  b = b.toFloat() / 255f,
  a = a.toFloat() / 255f,
)

fun Color(value: Int): Color = Color(
    r = (value and 0xFF0000).ushr(16) / 255f,
    g = (value and 0x00FF00).ushr(8) / 255f,
    b = (value and 0x0000FF) / 255f,
    a = value.ushr(24) / 255f,
)

fun Color.distance(other: Color): Float {
  val r = (this.r - other.r).pow(2)
  val g = (this.g - other.g).pow(2)
  val b = (this.b - other.b).pow(2)
  val a = (this.a - other.a).pow(2)
  return sqrt(r + g + b + a)
}
