package com.dropbox.differ

class Mask(
  val width: Int,
  val height: Int,
  private val data: FloatArray = FloatArray(width * height) { 0f }
) {
  /** The total number of pixels in the mask. */
  val size: Int get() = data.size

  /** The number of pixels that have been masked. */
  val count: Int get() = data.count { it > 0f }

  /** The percent of pixels that are masked. */
  val difference: Double get() = count.toDouble() / size

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
