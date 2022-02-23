import com.dropbox.differ.Color
import com.dropbox.differ.resources.TestImage

enum class TestImageType {
  SMALL_1, SMALL_2, SMALL_3,
  MEDIUM_1, MEDIUM_2,
  SLIM_1, SLIM_2
}

fun generateImage(type: TestImageType) = when (type) {
  TestImageType.SMALL_1 -> TestImage(2, 2).apply {
    setPixel(0, 0, Color(r = 10, g = 20, b = 30, a = 40))
    setPixel(0, 1, Color(r = 50, g = 60, b = 70, a = 80))
    setPixel(1, 0, Color(r = 90, g = 100, b = 110, a = 120))
    setPixel(1, 1, Color(r = 130, g = 140, b = 150, a = 160))
  }
  TestImageType.SMALL_2 -> TestImage(2, 2).apply {
    setPixel(0, 0, Color(r = 210, g = 220, b = 230, a = 240))
    setPixel(0, 1, Color(r = 10, g = 20, b = 30, a = 40))
    setPixel(1, 0, Color(r = 50, g = 60, b = 70, a = 80))
    setPixel(1, 1, Color(r = 15, g = 25, b = 35, a = 45))
  }
  TestImageType.SMALL_3 -> TestImage(2, 2)
  TestImageType.MEDIUM_1 -> TestImage(3, 3).apply {
    setPixel(0, 0, Color(r = 130, g = 140, b = 150, a = 160))
    setPixel(0, 1, Color(r = 170, g = 180, b = 190, a = 200))
    setPixel(0, 2, Color(r = 210, g = 220, b = 230, a = 240))
    setPixel(1, 0, Color(r = 15, g = 25, b = 35, a = 45))
    setPixel(1, 1, Color(r = 55, g = 65, b = 75, a = 85))
    setPixel(1, 2, Color(r = 95, g = 105, b = 115, a = 125))
    setPixel(2, 0, Color(r = 10, g = 20, b = 30, a = 40))
    setPixel(2, 1, Color(r = 50, g = 60, b = 70, a = 80))
    setPixel(2, 2, Color(r = 90, g = 100, b = 110, a = 120))
  }
  TestImageType.MEDIUM_2 -> TestImage(3, 3).apply {
    setPixel(0, 0, Color(r = 95, g = 15, b = 165, a = 26))
    setPixel(0, 1, Color(r = 15, g = 225, b = 135, a = 144))
    setPixel(0, 2, Color(r = 170, g = 80, b = 210, a = 2))
    setPixel(1, 0, Color(r = 50, g = 66, b = 23, a = 188))
    setPixel(1, 1, Color(r = 110, g = 120, b = 63, a = 147))
    setPixel(1, 2, Color(r = 30, g = 110, b = 10, a = 61))
    setPixel(2, 0, Color(r = 190, g = 130, b = 180, a = 29))
    setPixel(2, 1, Color(r = 10, g = 120, b = 31, a = 143))
    setPixel(2, 2, Color(r = 155, g = 165, b = 15, a = 185))
  }
  TestImageType.SLIM_1 -> TestImage(1, 3).apply {
    setPixel(0, 0, Color(r = 15, g = 225, b = 135, a = 144))
    setPixel(0, 1, Color(r = 170, g = 80, b = 210, a = 2))
    setPixel(0, 2, Color(r = 50, g = 66, b = 23, a = 188))
  }
  TestImageType.SLIM_2 -> TestImage(3, 3).apply {
    setPixel(0, 0, Color(r = 15, g = 225, b = 135, a = 144))
    setPixel(1, 0, Color(r = 170, g = 80, b = 210, a = 2))
    setPixel(2, 0, Color(r = 50, g = 66, b = 23, a = 188))
  }
}
