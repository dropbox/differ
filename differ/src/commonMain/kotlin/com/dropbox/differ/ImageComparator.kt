package com.dropbox.differ

import kotlin.math.abs

interface ImageComparator {

  data class ComparisonResult(
    val pixelDifferences: Int,
    val pixelCount: Int,
    val width: Int,
    val height: Int,
  )

  /**
   * Compares two images, returning a Double that indicates the percentage of
   * the images that differ.
   *
   * If a [diff] mask is supplied, it will be filled with the pixels that differ.
   */
  fun compare(left: Image, right: Image, diff: Mask? = null): ComparisonResult
}

class SimpleImageComparator(
  val maxDistance: Float = 0.1f,
  val hShift: Int = 0,
  val vShift: Int = 0,
) : ImageComparator {
  override fun compare(left: Image, right: Image, diff: Mask?): ImageComparator.ComparisonResult {
    val width = maxOf(left.width, right.width)
    val height = maxOf(left.height, right.height)

    fun compareWindow(x: Int, y: Int, color: Color): Boolean {
      if (hShift == 0 && vShift == 0) return false

      val l = maxOf(x - hShift, 0)
      val t = minOf(y + vShift, height - 1)
      val r = minOf(x + hShift, width - 1)
      val b = maxOf(y - vShift, 0)

      (l..r).forEach { offsetX ->
        (b..t).forEach { offsetY ->
          if (offsetX != x || offsetY != y) {
            val c1 = left.getPixel(offsetX, offsetY)
            val localDeltaThreshold = color.distance(c1)

            val c2 = right.getPixel(offsetX, offsetY)
            val localDelta = color.distance(c2)

            if (abs(localDelta - localDeltaThreshold) < maxDistance && localDeltaThreshold > maxDistance) {
              return true
            }
          }
        }
      }
      return false
    }

    var misses = 0
    (0 until width).forEach { x ->
      (0 until height).forEach { y ->
        val leftColor = left.getPixel(x, y)
        val rightColor = right.getPixel(x, y)

        val delta = leftColor.distance(rightColor)
        if (delta > maxDistance) {

          // If exact pixels don't match, check within the shift window
          if (!compareWindow(x, y, leftColor)) {
            misses++
          }
        }

        diff?.setValue(x, y, delta)
      }
    }

    return ImageComparator.ComparisonResult(
      pixelDifferences = misses,
      pixelCount = width * height,
      width = width,
      height = height,
    )
  }

}
