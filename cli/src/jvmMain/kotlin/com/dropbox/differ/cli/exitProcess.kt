package com.dropbox.differ.cli

import com.dropbox.differ.Color
import com.dropbox.differ.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

actual fun exitProcess(status: Int): Nothing = kotlin.system.exitProcess(status)
actual fun getTimeMillis(): Long = System.currentTimeMillis()
actual fun imageFunctions(): List<Pair<String, LoadImageFunc>> = listOf(
  "JVM ImageIO" to ::withImageIO
)

fun <R> withImageIO(filePath: String, block: (image: Image) -> R): R {
  val image = BufferedImageWrapper(ImageIO.read(File(filePath)))
  return try {
    block(image)
  } finally {}
}

private class BufferedImageWrapper(private val delegate: BufferedImage) : Image {
  override val width: Int = delegate.width
  override val height: Int = delegate.height
  override fun getPixel(x: Int, y: Int): Color = delegate.getRGB(x, y).let(::Color)
}
