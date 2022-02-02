package com.dropbox.differ

interface ImageComparator {
  /**
   * Compares two images, returning a Double that indicates the percentage of
   * the images that differ.
   *
   * If a mask is supplied, it will be filled with the pixels that differ.
   */
  fun compare(left: Image, right: Image, mask: Mask? = null): Double
}

class SimpleImageComparator(
  val maxDistance: Float = 20f
) : ImageComparator {
  override fun compare(left: Image, right: Image, mask: Mask?): Double {
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

        mask?.setValue(x, y, delta)
      }
    }

    return misses.toDouble() / (width * height)
  }
}
