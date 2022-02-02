package com.dropbox.differ

import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleImageComparatorTest {
  @Test fun `returns diff of 0 for identical images`() {
    val first = testImage()
    val second = testImage()

    val comparator = SimpleImageComparator()
    val diff = comparator.compare(first, second)

    assertEquals(0.0, diff)
  }

  @Test fun `returns diff of 1 for completely different images`() {
    val first = testImage()
    val second = first.mutate { FloatArray(it.size) { 0f } }

    val comparator = SimpleImageComparator()
    val diff = comparator.compare(first, second)

    assertEquals(0.0, diff)
  }

  @Test fun `mask contains differences`() {
    val first = testImage(width = 1080, height = 1920)
    val second = testImage(width = 1080, height = 1920)
    // Add a 20x20 pixel black square to the center of the image
    (950..970).forEach { y ->
      (530..550).forEach { x ->
        second.setPixel(x, y, Color(0f, 0f, 0f, 1f))
      }
    }

    val comparator = SimpleImageComparator(maxDistance = 1.0f)
    val mask = Mask(1080, 1920)
    comparator.compare(first, second, mask)

    val expectedDistance = Color(1f, 1f, 1f, 1f).distance(Color(0f, 0f, 0f, 1f))
    (0 until mask.height).forEach { y ->
      (0 until mask.width).forEach { x ->
        val actual = mask.getValue(x, y)
        val expected = if (y in (950..970) && x in (530..550)) expectedDistance else 0.0f
        assertEquals(expected, actual, "Mask pixel at $x, $y")
      }
    }
  }
}
