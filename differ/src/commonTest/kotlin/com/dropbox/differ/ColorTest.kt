package com.dropbox.differ

import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {
  @Test fun equality() {
    assertEquals(Color(1f, 1f, 1f, 1f), Color(1f, 1f, 1f, 1f))
  }

  @Test fun `ubyte constructor`() {
    assertEquals(Color(0f, 0f, 0f, 0f), Color(0u, 0u, 0u, 0u))
    assertEquals(Color(0f, 0f, 0f, 1f), Color(0u, 0u, 0u, 255u))
    assertEquals(Color(0.49803922f, 0f, 0f, 1f), Color(127u, 0u, 0u, 255u))
    assertEquals(Color(0f, 0.26666668f, 0f, 1f), Color(0u, 68u, 0u, 255u))
    assertEquals(Color(0f, 0f, 0.9529412f, 1f), Color(0u, 0u, 243u, 255u))
    assertEquals(Color(0f, 0f, 0f, 1f), Color(0u, 0u, 0u))
  }

  @Test fun `many int constructor`() {
    assertEquals(Color(0f, 0.33333334f, 0.6666667f, 1f), Color(0, 85, 170, 255))
    assertEquals(Color(1f, 0.6666667f,0.33333334f, 0f), Color(255, 170, 85, 0))
    assertEquals(Color(0f, 0f, 0f, 1f), Color(0, 0, 0))
  }

  @Test fun `int constructor`() {
    assertEquals(Color(0f, 0f, 0f, 0f), Color(0x00000000))
    assertEquals(Color(0f, 0f, 0f, 1f), Color(0xff000000.toInt()))
    assertEquals(Color(1f, 0f, 0f, 1f), Color(0xffff0000.toInt()))
    assertEquals(Color(0f, 0.6666667f, 0f, 1f), Color(0xff00aa00.toInt()))
    assertEquals(Color(0f, 0f, 0.73333335f, 1f), Color(0xff0000bb.toInt()))
  }

  @Test fun distance() {
    val c1 = Color(0f, 0f, 0f, 0f)
    val c2 = Color(1f, 1f, 1f, 1f)
    assertEquals(2f, c1.distance(c2))
  }
}
