package com.dropbox.differ

data class Color(val a: Float, val r: Float, val g: Float, val b: Float)

interface Image {
    val width: Int
    val height: Int
    fun getPixel(x: Int, y: Int): Color
}
