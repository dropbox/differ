package com.dropbox.differ

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class MaskTest {
  @Test fun `starts with an empty map`() {
    val mask = Mask(100, 100)
    assertEquals(0.0, mask.difference)
  }

  @Test fun `starts with no pixels masked`() {
    val mask = Mask(100, 100)
    assertEquals(0, mask.count)
  }

  @Test fun `returns size`() {
    val mask =  Mask(10, 10)
    assertEquals(100, mask.size)
  }

  @Test fun `ensures data fits in size`() {
    assertFails("Data arary should not be larger than size.") {
      Mask(10, 10, FloatArray(105))
    }
    assertFails("Data array should not be smaller than size.") {
      Mask(10, 10, FloatArray(10))
    }
  }

  @Test fun `count returns number of non-zero pixels`() {
    val mask = Mask(
      10,
      10,
      FloatArray(100) { if (it < 20) 1f else 0f }
    )
    assertEquals(20, mask.count)
  }

  @Test fun `difference returns the percent of non-zero pixels`() {
    val mask = Mask(
      10,
      10,
      FloatArray(100) { if (it < 20) 1f else 0f }
    )
    assertEquals(0.2, mask.difference)
  }

  @Test fun `getValue returns the value at the pixel location`() {
    val mask = Mask(5, 5, FloatArray(25) { it.toFloat() })
    assertEquals(12f, mask.getValue(2, 2))
  }

  @Test fun `getValue fails if request is out of range`() {
    val mask = Mask(5, 5)
    assertFails { mask.getValue(-1, 0) }
    assertFails { mask.getValue(0, -1) }
    assertFails { mask.getValue(6, 0) }
    assertFails { mask.getValue(0, 6) }
  }

  @Test fun `setValue sets the value at the given location`() {
    val mask = Mask(5, 5)
    assertEquals(0f, mask.getValue(2, 2))

    mask.setValue(2, 2, 1.4f)
    assertEquals(1.4f, mask.getValue(2, 2))
  }

  @Test fun `setValue fails if request is out of range`() {
    val mask = Mask(5, 5)
    assertFails { mask.setValue(-1, 0, 1f) }
    assertFails { mask.setValue(0, -1, 1f) }
    assertFails { mask.setValue(6, 0, 1f) }
    assertFails { mask.setValue(0, 6, 1f) }
  }
}
