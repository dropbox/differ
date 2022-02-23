package com.dropbox.differ.resources

import okio.Path.Companion.toPath

val projectPath = ".".toPath()
val testResourcePath = projectPath / "src" / "commonTest" / "resources"

// TODO Once Kotlin 1.6.20 is released and Okio is updated we can
//  implement this only here, instead of once for each platform.
//actual fun readResource(path: String): ByteArray {
//  return FileSystem.SYSTEM.read(testResourcePath / path.toPath()) {
//    readByteArray()
//  }
//}
