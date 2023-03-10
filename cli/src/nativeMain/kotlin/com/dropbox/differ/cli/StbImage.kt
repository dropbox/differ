package com.dropbox.differ.cli

import com.dropbox.differ.Color
import com.dropbox.differ.Image
import kotlinx.cinterop.*
import stb.image.stbi_image_free
import stb.image.stbi_load

fun <R> withImage(filePath: String, block: (image: Image) -> R): R {
  var image: StbImage? = null
  try {
    image = StbImage(filePath)
    return block(image)
  } finally {
    image?.close()
  }
}

class StbImage internal constructor(val filePath: String) : Image {

  private val pointer: CPointer<UByteVar>
  private val channels: Int

  override val width: Int
  override val height: Int

  init {
    val arena = Arena()
    try {
      val x = arena.alloc<IntVar>()
      val y = arena.alloc<IntVar>()
      val c = arena.alloc<IntVar>()

      val data = stbi_load(filePath, x.ptr, y.ptr, c.ptr, 0)
      pointer = data?.reinterpret()
        ?: throw Exception("Failed to read image from file: $filePath")

      width = x.value
      height = y.value
      channels = c.value
    } finally {
      arena.clear()
    }
  }

  private fun getChannel(x: Int, y: Int, channel: Int): UByte {
    if (channels < 4 && channel == 3) {
      return 255u
    }

    val pixelOffset = (x + y * width) * channels
    return pointer[pixelOffset + channel]
  }

  override fun getPixel(x: Int, y: Int): Color =
    Color(
      r = getChannel(x, y, 0),
      g = getChannel(x, y, 1),
      b = getChannel(x, y, 2),
      a = getChannel(x, y, 3),
    )

  fun close() {
    stbi_image_free(pointer)
  }
}
