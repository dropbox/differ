package com.dropbox.differ

import kotlin.math.pow
import kotlin.math.sqrt

class Mask(
  val width: Int,
  val height: Int,
  private val data: FloatArray = FloatArray(width * height) { 0f }
) {
  val size: Int get() = data.size
  val count: Int get() = data.count { it > 0f }

  fun getValue(x: Int, y: Int): Float {
    return data[getIndex(x, y)]
  }

  fun setValue(x: Int, y: Int, value: Float) {
    data[getIndex(x, y)] = value
  }

  private fun getIndex(x: Int, y: Int): Int {
    return width * y + x
  }
}

interface ImageComparator {
  /**
   * Comparison function that compares two images, and
   */
  fun compare(left: Image, right: Image, output: Mask = Mask(left.width, left.height)): Mask
}

class SimpleImageComparator(
  val maxDistance: Float = 20f
) : ImageComparator {
  override fun compare(left: Image, right: Image, output: Mask): Mask {
    val width = maxOf(left.width, right.width)
    val height = maxOf(left.height, right.height)

    var misses = 0
    (0 until width).forEach { x ->
      (0 until height).forEach { y ->
        val leftColor = left.getPixel(x, y)
        val rightColor = right.getPixel(x, y)

        val delta = leftColor.distance(rightColor)
        if (delta > maxDistance) {
          misses++
        }

        output.setValue(x, y, delta)
      }
    }
    return output
  }

  private fun Color.distance(other: Color): Float {
    val r = (this.r - other.r).pow(2)
    val g = (this.g - other.g).pow(2)
    val b = (this.b - other.b).pow(2)
    val a = (this.a - other.a).pow(2)
    return sqrt(r + g + b + a)
  }
}
