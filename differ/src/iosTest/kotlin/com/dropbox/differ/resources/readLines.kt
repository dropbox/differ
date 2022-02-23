package com.dropbox.differ.resources

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import okio.Path.Companion.toPath
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

actual fun readResource(path: String): ByteArray {
  val (filename, ext) = path.toPath().name.split(".")
  val resourcePath = NSBundle.mainBundle.pathForResource(
    "resources/$filename",
    ext
  )

  val data = NSData.dataWithContentsOfFile(resourcePath!!)
  return data!!.toByteArray()
}

internal fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).apply {
  usePinned {
    memcpy(it.addressOf(0), bytes, length)
  }
}
