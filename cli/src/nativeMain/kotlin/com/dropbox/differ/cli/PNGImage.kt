package com.dropbox.differ.cli

//import com.dropbox.differ.Color
//import com.dropbox.differ.Image
//import kotlinx.io.core.Closeable


//
//fun <R> withPNGImage(filePath: String, block: (image: PNGImage) -> R): R {
//  var image: PNGImage? = null
//  try {
//    image = PNGImage()
//    return block(image)
//  } finally {
//    image?.close()
//  }
//}
//
//class PNGImage : Image, Closeable {
//  override val width: Int
//    get() = TODO("Not yet implemented")
//  override val height: Int
//    get() = TODO("Not yet implemented")
//
//  override fun getPixel(x: Int, y: Int): Color {
//    TODO("Not yet implemented")
//  }
//
//  override fun close() {
//    TODO("Not yet implemented")
//  }
//}
