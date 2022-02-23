package com.dropbox.differ.resources

import okio.FileSystem
import okio.Path.Companion.toPath

actual fun readResource(path: String): ByteArray {
  return FileSystem.SYSTEM.read(testResourcePath / path.toPath()) {
    readByteArray()
  }
}
