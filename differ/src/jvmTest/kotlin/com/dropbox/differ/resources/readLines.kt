package com.dropbox.differ.resources

import okio.FileSystem
import okio.Path.Companion.toPath

actual fun readResource(path: String): ByteArray {
  return FileSystem.RESOURCES.read(path.toPath()) {
    readByteArray()
  }
}
