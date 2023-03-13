package com.dropbox.differ.cli

actual fun exitProcess(status: Int): Nothing = kotlin.system.exitProcess(status)
actual fun getTimeMillis(): Long = kotlin.system.getTimeMillis()

actual fun imageFunctions(): List<Pair<String, LoadImageFunc>> = listOf(
  "STB" to ::withImage,
  "libpng" to ::withPNGImage2,
  "libpng (copy pixels to Kotlin)" to ::withPNGImage,
)
