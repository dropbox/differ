package com.dropbox.differ.cli

import com.dropbox.differ.Color
import com.dropbox.differ.Image
import kotlinx.cinterop.*
import kotlinx.io.core.Closeable
import libpng.*
import platform.posix.fopen
import platform.posix.fread

fun <R> withPNGImage(filePath: String, block: (image: PNGImage) -> R): R {
  val fp = fopen(filePath, "rb")
    ?: error("Failed to open file pointer at $filePath")

  memScoped {
    val len = 8UL
    val buf = allocArray<UByteVar>(len.toInt())
    if (fread(buf, 1, len, fp) != len) {
      error("Failed to read $len bytes of file $filePath")
    }

    val isPng = png_sig_cmp(buf, 0, len) == 0
    if (!isPng) {
      error("Not a png $filePath")
    }

    val png_struct = png_create_read_struct(PNG_LIBPNG_VER_STRING, null, null, null)
      ?: error("Couldn't create png read struct")

    defer {
      memScoped {
        val png_structp = alloc<CPointerVarOf<CPointer<png_struct>>> {
          value = png_struct
        }
        png_destroy_read_struct(png_structp.ptr, null, null)
      }
    }

    "foo".cstr
    val info_ptr = png_create_info_struct(png_struct)
    if (info_ptr == null) {
      png_destroy_read_struct(, null, null)
    }
  }

  var image: PNGImage? = null
  try {
    image = PNGImage()
    return block(image)
  } finally {
    image?.close()
  }
}

class PNGImage : Image, Closeable {
  override val width: Int
    get() = TODO("Not yet implemented")
  override val height: Int
    get() = TODO("Not yet implemented")

  override fun getPixel(x: Int, y: Int): Color {
    TODO("Not yet implemented")
  }

  override fun close() {
    TODO("Not yet implemented")
  }
}
