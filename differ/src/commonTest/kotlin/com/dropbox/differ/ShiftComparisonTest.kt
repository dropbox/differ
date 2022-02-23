package com.dropbox.differ

import com.dropbox.differ.resources.PPMImage
import com.dropbox.differ.resources.TestImage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ShiftComparisonTest {

  private lateinit var image1: TestImage
  private lateinit var image2: TestImage

  @BeforeTest fun setup() {
    image1 = PPMImage("image1.ppm")
    image2 = PPMImage("image2.ppm")
  }

  @Test fun `returns fail with no shift configured`() {
    val comparator = SimpleImageComparator(maxDistance = 0.1f)

    val percent = comparator.compare(image1, image2)
    assertEquals(318, percent.pixelDifferences)
  }

  @Test fun `returns pass when within shift margin`() {
    val comparator = SimpleImageComparator(
      maxDistance = 0.1f,
      hShift = 10,
      vShift = 10
    )

    val percent = comparator.compare(image1, image2)
    assertEquals(0, percent.pixelDifferences)
  }
}
