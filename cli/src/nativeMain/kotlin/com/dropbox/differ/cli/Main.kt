package com.dropbox.differ.cli

import com.dropbox.differ.ImageComparator
import com.dropbox.differ.Mask
import com.dropbox.differ.SimpleImageComparator
import kotlin.system.exitProcess
import kotlin.system.getTimeMicros
import kotlin.system.getTimeMillis
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

  inline fun vlog(msg: String) {
    if (verbose) println(msg)
  }

  vlog("")
  vlog("Image loader: STB")
  vlog("Starting to load first image: $leftPath")
  var start = getTimeMillis()
  var result = withImage(leftPath) { leftImage ->
    vlog("Loaded $leftPath in ${getTimeMillis() - start}ms")
    vlog("Starting to load second image: $rightPath")
    start = getTimeMillis()
    withImage(rightPath) { rightImage ->
      vlog("Loaded $rightPath in ${getTimeMillis() - start}ms")
      mask = Mask(leftImage.width, leftImage.height)

      vlog("Starting comparison")
      start = getTimeMillis()
      val result = comparator.compare(leftImage, rightImage, mask)
      vlog("Finished comparison in ${getTimeMillis() - start}ms")
      result
    }
  }

  vlog("")
  vlog("Image loader: libpng")
  vlog("Starting to load first image: $leftPath")
  start = getTimeMillis()
  result = withPNGImage2(leftPath) { leftImage ->
    vlog("Loaded $leftPath in ${getTimeMillis() - start}ms")
    vlog("Starting to load second image: $rightPath")
    start = getTimeMillis()
    withPNGImage2(rightPath) { rightImage ->
      vlog("Loaded $rightPath in ${getTimeMillis() - start}ms")
      mask = Mask(leftImage.width, leftImage.height)

      vlog("Starting comparison")
      start = getTimeMillis()
      val result = comparator.compare(leftImage, rightImage, mask)
      vlog("Finished comparison in ${getTimeMillis() - start}ms")
      result
    }
  }

  vlog("")
  vlog("Image loader: libpng (copy byte to Kotlin)")
  vlog("Starting to load first image: $leftPath")
  start = getTimeMillis()
  result = withPNGImage(leftPath) { leftImage ->
    vlog("Loaded $leftPath in ${getTimeMillis() - start}ms")
    vlog("Starting to load second image: $rightPath")
    start = getTimeMillis()
    withPNGImage(rightPath) { rightImage ->
      vlog("Loaded $rightPath in ${getTimeMillis() - start}ms")
      mask = Mask(leftImage.width, leftImage.height)

      vlog("Starting comparison")
      start = getTimeMillis()
      val result = comparator.compare(leftImage, rightImage, mask)
      vlog("Finished comparison in ${getTimeMillis() - start}ms")
      result
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
    println("Differences: ${result.pixelDifferences} (${percentDiff}%)")
    println(if (pass) "PASS" else "FAIL")
  }

  exitProcess(if (pass) 0 else 1)
}
