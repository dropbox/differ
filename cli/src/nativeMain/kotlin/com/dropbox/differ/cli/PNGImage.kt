package com.dropbox.differ.cli

import com.dropbox.differ.Color
import com.dropbox.differ.Image
import kotlinx.cinterop.*
//import kotlinx.io.core.Closeable
import libpng.*
import platform.posix.*

fun <R> withPNGImage2(filePath: String, block: (image: Image) -> R): R {
  val image = PNGImage2(filePath)
  return try {
    block(image)
  } finally {
    image.close()
  }
}

@OptIn(ExperimentalUnsignedTypes::class)
fun <R> withPNGImage(filePath: String, block: (image: Image) -> R): R {
  var width = 0
  var height = 0
  var bytes = memScoped {
    val realPath = memScoped {
      val tmp = allocArray<ByteVar>(PATH_MAX)
      realpath(filePath, tmp)
      tmp.toKString()
    }

    val fp = fopen(realPath, "rb")
      ?: error("Failed to open file pointer at $realPath")

    val len = 8UL
    val buf = allocArray<UByteVar>(len.toInt())
    if (fread(buf, 1, len, fp) != len) {
      error("Failed to read $len bytes of file $realPath")
    }

    val isPng = png_sig_cmp(buf, 0, len) == 0
    if (!isPng) {
      error("Not a png $realPath")
    }

    val pngPtr = png_create_read_struct(PNG_LIBPNG_VER_STRING, null, null, null)
      ?: error("Couldn't create png read struct")

    defer {
      memScoped {
        val pngPtrPtr = alloc<CPointerVarOf<CPointer<png_struct>>> {
          value = pngPtr
        }
        png_destroy_read_struct(pngPtrPtr.ptr, null, null)
      }
    }

    val infoPtr = png_create_info_struct(pngPtr)

    png_init_io(pngPtr, fp.reinterpret())
    png_set_sig_bytes(pngPtr, len.toInt())

    png_read_info(pngPtr, infoPtr)
    width = png_get_image_width(pngPtr, infoPtr).toInt()
    height = png_get_image_height(pngPtr, infoPtr).toInt()
    val colorType = png_get_color_type(pngPtr, infoPtr).toInt()
    val bitDepth = png_get_bit_depth(pngPtr, infoPtr).toInt()

    if (bitDepth == 16) {
      png_set_strip_16(pngPtr)
    }

    if (colorType == PNG_COLOR_TYPE_PALETTE) {
      png_set_palette_to_rgb(pngPtr)
    }

    if (colorType == PNG_COLOR_TYPE_GRAY && bitDepth < 8) {
      png_set_expand_gray_1_2_4_to_8(pngPtr)
    }

    if (png_get_valid(pngPtr, infoPtr, PNG_INFO_tRNS) != 0U) {
      png_set_tRNS_to_alpha(pngPtr)
    }

    if (colorType in setOf(PNG_COLOR_TYPE_RGB, PNG_COLOR_TYPE_GRAY, PNG_COLOR_TYPE_PALETTE)) {
      png_set_filler(pngPtr, 0xFF, PNG_FILLER_AFTER)
    }

    if (colorType == PNG_COLOR_TYPE_GRAY || colorType == PNG_COLOR_TYPE_GRAY_ALPHA) {
      png_set_gray_to_rgb(pngPtr)
    }

    png_read_update_info(pngPtr, infoPtr)

    val rowBytes = png_get_rowbytes(pngPtr, infoPtr).toInt()
    val data = allocArray<png_bytepVar>(height) {
      value = allocArray(rowBytes)
    }
    png_read_image(pngPtr, data)

    fclose(fp)

    data.readBytes(height * rowBytes).toUByteArray()
  }

  var image: PNGImage? = null
  try {
    image = PNGImage(bytes, width, height)
    return block(image)
  } finally {
    image?.close()
  }
}

class PNGImage(
  val bytes: UByteArray,
  override val width: Int,
  override val height: Int,
) : Image {
  private fun getChannel(x: Int, y: Int, channel: Int): UByte {
    val pixelOffset = (x + y * width) * 4
    return bytes[pixelOffset + channel]
  }

  override fun getPixel(x: Int, y: Int): Color = Color(
    r = getChannel(x, y, 0),
    g = getChannel(x, y, 0),
    b = getChannel(x, y, 0),
    a = getChannel(x, y, 0),
  )

  /* override */ fun close() = Unit
}

class PNGImage2(
  val filePath: String,
) : Image {
  private val arena = Arena()
  private val data: CPointer<UByteVar>

  override val width: Int
  override val height: Int

  init {
    val loadArena = Arena()
    try {
      val realPath = memScoped {
        val tmp = allocArray<ByteVar>(PATH_MAX)
        realpath(filePath, tmp)
        tmp.toKString()
      }

      val fp = fopen(realPath, "rb")
        ?: error("Failed to open file pointer at $realPath")

      val len = 8UL
      val buf = loadArena.allocArray<UByteVar>(len.toInt())
      if (fread(buf, 1, len, fp) != len) {
        error("Failed to read $len bytes of file $realPath")
      }

      val isPng = png_sig_cmp(buf, 0, len) == 0
      if (!isPng) {
        error("Not a png $realPath")
      }

      val pngPtr = png_create_read_struct(PNG_LIBPNG_VER_STRING, null, null, null)
        ?: error("Couldn't create png read struct")

      loadArena.defer {
        memScoped {
          val pngPtrPtr = alloc<CPointerVarOf<CPointer<png_struct>>> {
            value = pngPtr
          }
          png_destroy_read_struct(pngPtrPtr.ptr, null, null)
        }
      }

      val infoPtr = png_create_info_struct(pngPtr)

      png_init_io(pngPtr, fp.reinterpret())
      png_set_sig_bytes(pngPtr, len.toInt())

      png_read_info(pngPtr, infoPtr)
      width = png_get_image_width(pngPtr, infoPtr).toInt()
      height = png_get_image_height(pngPtr, infoPtr).toInt()
      val colorType = png_get_color_type(pngPtr, infoPtr).toInt()
      val bitDepth = png_get_bit_depth(pngPtr, infoPtr).toInt()

      if (bitDepth == 16) {
        png_set_strip_16(pngPtr)
      }

      if (colorType == PNG_COLOR_TYPE_PALETTE) {
        png_set_palette_to_rgb(pngPtr)
      }

      if (colorType == PNG_COLOR_TYPE_GRAY && bitDepth < 8) {
        png_set_expand_gray_1_2_4_to_8(pngPtr)
      }

      if (png_get_valid(pngPtr, infoPtr, PNG_INFO_tRNS) != 0U) {
        png_set_tRNS_to_alpha(pngPtr)
      }

      if (colorType in setOf(PNG_COLOR_TYPE_RGB, PNG_COLOR_TYPE_GRAY, PNG_COLOR_TYPE_PALETTE)) {
        png_set_filler(pngPtr, 0xFF, PNG_FILLER_AFTER)
      }

      if (colorType == PNG_COLOR_TYPE_GRAY || colorType == PNG_COLOR_TYPE_GRAY_ALPHA) {
        png_set_gray_to_rgb(pngPtr)
      }

      png_read_update_info(pngPtr, infoPtr)

      val rowBytes = png_get_rowbytes(pngPtr, infoPtr).toInt()
      val pointer = arena.allocArray<png_bytepVar>(height) {
        value = arena.allocArray(rowBytes)
      }
      png_read_image(pngPtr, pointer)

      data = pointer.reinterpret()

      fclose(fp)
    } finally {
      loadArena.clear()
    }
  }

  private fun getChannel(x: Int, y: Int, channel: Int): UByte {
    val pixelOffset = (x + y * width) * 4
    return data[pixelOffset + channel]
  }

  override fun getPixel(x: Int, y: Int): Color = Color(
    r = getChannel(x, y, 0),
    g = getChannel(x, y, 0),
    b = getChannel(x, y, 0),
    a = getChannel(x, y, 0),
  )

  fun close() {
    arena.clear()
  }
}
