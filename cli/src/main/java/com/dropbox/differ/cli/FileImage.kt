package com.dropbox.differ.cli

import com.dropbox.differ.Color
import com.dropbox.differ.Image
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

@Throws(IOException::class)
fun FileImage(filePath: String): FileImage {
    return FileImage(ImageIO.read(File(filePath)))
}

class FileImage(private val image: BufferedImage): Image {
    override val width: Int get() = image.width
    override val height: Int get() = image.height

    override fun getPixel(x: Int, y: Int): Color {
        val c = image.getRGB(x, y)
        return Color(
            a = 255f / c.ushr(24),
            r = 255f / (c and 0xFF0000).ushr(16),
            g = 255f / (c and 0x00FF00).ushr(8),
            b = 255f / (c and 0x0000FF)
        )
    }
}