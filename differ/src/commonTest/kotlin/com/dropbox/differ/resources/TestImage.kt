package com.dropbox.differ.resources

import com.dropbox.differ.Color
import com.dropbox.differ.Image

fun TestImage(
  width: Int = 1080,
  height: Int = 1920,
  components: Int = 4,
  data: FloatArray? = null,
): TestImage =
  TestImage(
    data = data ?: FloatArray(width * height * components) { 1f },
    components,
    width,
    height
  )

class TestImage(
  val data: FloatArray,
  val componentCount: Int,
  override val width: Int,
  override val height: Int
) : Image {

  private val stride = width * componentCount

  fun getIndex(x: Int, y: Int): Int = x * componentCount + y * stride

  fun getPosition(index: Int): Pair<Int, Int> {
    val x = (index % stride) / componentCount
    val y = index / stride
    return x to y
  }

  override fun getPixel(x: Int, y: Int): Color {
    val i = getIndex(x, y)
    return Color(
      r = data[i],
      g = if (componentCount > 1) data[i + 1] else 0f,
      b = if (componentCount > 2) data[i + 2] else 0f,
      a = if (componentCount > 3) data[i + 3] else 1f,
    )
  }

  fun setPixel(x: Int, y: Int, color: Color) {
    val i = getIndex(x, y)
    data[i] = color.r
    if (componentCount > 1) {
      data[i + 1] = color.g
    }
    if (componentCount > 2) {
      data[i + 2] = color.b
    }
    if (componentCount > 3) {
      data[i + 3] = color.a
    }
  }
}

fun TestImage.mutate(func: (data: FloatArray) -> FloatArray) =
  TestImage(func(data), componentCount, width, height)
