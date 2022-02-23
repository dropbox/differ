package com.dropbox.differ

import com.dropbox.differ.resources.TestImage
import com.dropbox.differ.resources.mutate
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleImageComparatorTest {
  @Test fun `returns no pixel differences for identical images`() {
    val first = TestImage()
    val second = TestImage()

    val comparator = SimpleImageComparator()
    val result = comparator.compare(first, second)

    assertEquals(0, result.pixelDifferences)
    assertEquals(first.width * first.height, result.pixelCount)
    assertEquals(first.width, result.width)
    assertEquals(first.height, result.height)
  }

  @Test fun `returns DIFFERENT for completely different images`() {
    val first = TestImage()
    val second = first.mutate { FloatArray(it.size) { 0f } }

    val comparator = SimpleImageComparator()
    val result = comparator.compare(first, second)

    assertEquals(first.width * first.height, result.pixelDifferences)
  }

  @Test fun `mask contains differences`() {
    val first = TestImage(width = 1080, height = 1920)
    val second = TestImage(width = 1080, height = 1920)
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
