package com.dropbox.differ.cli

import com.dropbox.differ.Image
import com.dropbox.differ.ImageComparator
import com.dropbox.differ.Mask
import com.dropbox.differ.SimpleImageComparator
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.system.exitProcess
import kotlin.system.getTimeMicros
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

fun main(args: Array<String>) {
  val parser = ArgParser("differ")
  val verbose by parser
    .option(
      ArgType.Boolean,
      fullName = "verbose",
      description = "Print verbose comparison output."
    )
    .default(false)
  val threshold by parser
    .option(
      ArgType.Double,
      fullName = "threshold",
      shortName = "t",
      description = "Percent of pixels that can differ while still resulting in a success."
    )
    .default(0.001)
  val delta by parser
    .option(
      ArgType.Double,
      fullName = "delta",
      shortName = "d",
      description = "Max distance colors can be in 4 dimensional space without triggering a difference."
    )
    .default(0.08)
  val hShift by parser
    .option(
      ArgType.Int,
      fullName = "h-shift",
      description = "Acceptable horizontal shift of pixel.",
    )
    .default(0)
  val vShift by parser
    .option(
      ArgType.Int,
      fullName = "v-shift",
      description = "Acceptable vertical shift of pixel.",
    )
    .default(0)
  val outputFile by parser
    .option(
      ArgType.String,
      fullName = "output",
      shortName = "o",
      description = "Path at which the output file should be written."
    )

  val leftPath by parser.argument(ArgType.String, "First Image path", "Path to the first image for comparison")
  val rightPath by parser.argument(ArgType.String, "Second Image path", "Path to the second image for comparison")

  parser.parse(args)

  val startTime = getTimeMicros()
  val comparator = SimpleImageComparator(
    maxDistance = delta.toFloat(),
    hShift = hShift,
    vShift = vShift,
  )
  var mask: Mask

  val result = withImage(leftPath) { leftImage ->
    outputFile?.let {
      writePPMFile(it, leftImage)
    }
    withImage(rightPath) { rightImage ->
      mask = Mask(leftImage.width, leftImage.height)
      comparator.compare(leftImage, rightImage, mask)
    }
  }

  val diff = result.pixelDifferences.toFloat() / result.pixelCount
  val pass = diff <= threshold

  if (verbose) {
    val status = when {
      result.pixelDifferences == 0 -> "identical"
      pass -> "similar"
      else -> "different"
    }
    println("Images are $status")

    if (result.pixelDifferences > 0) {
      println("${result.pixelDifferences} pixels are different")
    }

    println("Time: ${(getTimeMicros() - startTime) / 1000f}ms")

    val percentDiff = result.pixelDifferences.toFloat() / result.pixelCount * 100
    println("Differences: ${result.pixelDifferences} (${percentDiff.toString(2)}%)")
    println(if (pass) "PASS" else "FAIL")
  }

  exitProcess(if (pass) 0 else 1)
}

fun Float.toString(decimals: Int): String {
  return "${toInt()}.${((this - toInt()) * 10f.pow(decimals)).roundToInt()}"
}

fun writePPMFile(path: String, image: Image) {
  val w = image.width
  val h = image.height
  println("P3")
  println("$w $h")
  println("255")
  (0 until h).reversed().forEach { y ->
    (0 until w).forEach { x ->
      val c = image.getPixel(x, y)
      println("${(c.r * 255).roundToInt()} ${(c.g * 255).roundToInt()} ${(c.b * 255).roundToInt()}")
    }
  }
}
