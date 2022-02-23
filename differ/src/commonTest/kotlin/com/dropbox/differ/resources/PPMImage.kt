package com.dropbox.differ.resources

import com.dropbox.differ.Color

fun PPMImage(name: String): TestImage {
  val data = readResource(name)
  val content = data.decodeToString()

  // Parse the PPM image
  val iterator = content.iterator()
  if (iterator.nextLine() != "P3") {
    throw Exception("Invalid PPM file. Missing `P3` header.")
  }

  val width = iterator.nextInt()
  val height = iterator.nextInt()
  val maxValue = iterator.nextInt().toFloat()

  val result = TestImage(width, height, 3)
  (0 until height).reversed().forEach { y ->
    (0 until width).reversed().forEach { x ->
      val c = Color(
        r = iterator.nextInt() / maxValue,
        g = iterator.nextInt() / maxValue,
        b = iterator.nextInt() / maxValue
      )
      result.setPixel(x, y, c)
    }
  }
  return result
}

private fun CharIterator.nextLine(): String = buildString {
  while (hasNext()) {
    val c = nextChar()
    if (c == '\n') return@buildString
    append(c)
  }
}

private fun CharIterator.nextWord(): String = buildString {
  while (hasNext()) {
    val c = nextChar()

    // Skip comment lines
    if (c == '#') {
      nextLine()
      continue
    }

    if (c.isWhitespace()) {
      if (isNotBlank()) {
        return@buildString
      } else {
        continue
      }
    }

    append(c)
  }
}

private fun CharIterator.nextInt(): Int = nextWord().toInt()
