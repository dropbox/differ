package com.dropbox.differ

import kotlin.test.Test
import kotlin.test.assertEquals

class MaskTest {
  @Test fun `starts with an empty map`() {
    val mask = Mask(100, 100)
    assertEquals(0.0, mask.difference)
  }

  @Test fun `starts with no pixels masked`() {
    val mask = Mask(100, 100)
    assertEquals(0, mask.count)
  }
}
