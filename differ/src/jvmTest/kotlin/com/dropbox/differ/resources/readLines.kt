package com.dropbox.differ.resources

actual fun readResource(path: String): ByteArray {
  return ClassLoader.getSystemResourceAsStream(path)!!.readBytes()
}
