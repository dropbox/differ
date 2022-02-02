package com.dropbox.differ

interface Image {
  val width: Int
  val height: Int
  fun getPixel(x: Int, y: Int): Color
}
