package com.dropbox.differ

import com.dropbox.differ.resources.TestImage
import com.dropbox.differ.resources.mutate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExactEqualityImageTest {
  @Test fun `skips the per-pixel comparison when the images are provably identical`() {
    val delegate = TestImage(width = 8, height = 8)
    val left = CountingExactEqualityImage(delegate, ExactEqualityResult.Equal)
    val right = TestImage(width = 8, height = 8)

    val result = SimpleImageComparator().compare(left, right)

    assertEquals(0, result.pixelDifferences)
    assertEquals(8 * 8, result.pixelCount)
    assertEquals(8, result.width)
    assertEquals(8, result.height)
    assertEquals(0, left.getPixelCalls)
  }

  @Test fun `falls back to the full comparison when equality is unknown`() {
    val delegate = TestImage(width = 8, height = 8)
    val left = CountingExactEqualityImage(delegate, ExactEqualityResult.Unknown)
    val right = TestImage(width = 8, height = 8).mutate { FloatArray(it.size) { 0f } }

    val result = SimpleImageComparator().compare(left, right)

    assertEquals(8 * 8, result.pixelDifferences)
    assertTrue(left.getPixelCalls > 0)
  }

  @Test fun `reports the same result as the full comparison for identical images`() {
    val delegate = TestImage(width = 8, height = 8)
    val left = CountingExactEqualityImage(delegate, ExactEqualityResult.Equal)
    val right = TestImage(width = 8, height = 8)

    val shortCircuited = SimpleImageComparator().compare(left, right)
    val compared = SimpleImageComparator().compare(delegate, right)

    assertEquals(compared, shortCircuited)
  }

  @Test fun `runs the full comparison when a diff mask is supplied`() {
    val delegate = TestImage(width = 8, height = 8)
    val left = CountingExactEqualityImage(delegate, ExactEqualityResult.Equal)
    val right = TestImage(width = 8, height = 8)
    val mask = Mask(8, 8)

    val result = SimpleImageComparator().compare(left, right, mask)

    assertEquals(0, result.pixelDifferences)
    assertTrue(left.getPixelCalls > 0)
  }

  @Test fun `runs the full comparison when dimensions differ`() {
    // Equal from a misbehaving implementation must not hide a size mismatch.
    val delegate = TestImage(width = 8, height = 8)
    val left = CountingExactEqualityImage(delegate, ExactEqualityResult.Equal)
    val right = TestImage(width = 8, height = 4)

    val result = SimpleImageComparator().compare(left, right)

    assertEquals(8 * 4, result.pixelDifferences)
  }

  private class CountingExactEqualityImage(
    private val delegate: TestImage,
    private val result: ExactEqualityResult,
  ) : ExactEqualityImage {
    var getPixelCalls = 0
    override val width: Int get() = delegate.width
    override val height: Int get() = delegate.height

    override fun getPixel(x: Int, y: Int): Color {
      getPixelCalls += 1
      return delegate.getPixel(x, y)
    }

    override fun checkExactEquality(other: Image): ExactEqualityResult = result
  }
}
