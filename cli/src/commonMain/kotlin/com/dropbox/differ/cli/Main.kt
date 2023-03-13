package com.dropbox.differ.cli

import com.dropbox.differ.Image
import com.dropbox.differ.ImageComparator
import com.dropbox.differ.Mask
import com.dropbox.differ.SimpleImageComparator
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

expect fun exitProcess(status: Int): Nothing
expect fun getTimeMillis(): Long

var verboseLogging = false
inline fun vlog(msg: String) {
  if (verboseLogging) println(msg)
}

typealias LoadImageFunc = (filepath: String, block: (Image) -> Unit) -> Unit

expect fun imageFunctions(): List<Pair<String, LoadImageFunc>>

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

  verboseLogging = verbose

  val startTime = getTimeMillis()
  val comparator = SimpleImageComparator(
    maxDistance = delta.toFloat(),
    hShift = hShift,
    vShift = vShift,
  )
  var mask: Mask

  for ((name, func) in imageFunctions()) {
    vlog("")
    vlog("Image Loader: $name")
    vlog("Starting to load first image: $leftPath")
    var start = getTimeMillis()
    var comparisonResult: ImageComparator.ComparisonResult? = null
    func(leftPath) { leftImage ->
      vlog("Loaded $leftPath in ${getTimeMillis() - start}ms")
      vlog("Starting to load second image: $rightPath")
      start = getTimeMillis()
      func(rightPath) { rightImage ->
        vlog("Loaded $rightPath in ${getTimeMillis() - start}ms")
        mask = Mask(leftImage.width, leftImage.height)

        vlog("Starting comparison")
        start = getTimeMillis()
        comparisonResult = comparator.compare(leftImage, rightImage, mask)
        vlog("Finished comparison in ${getTimeMillis() - start}ms")
      }
    }

    val result = checkNotNull(comparisonResult)
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

      println("Time: ${getTimeMillis() - startTime}ms")

      val percentDiff = result.pixelDifferences.toFloat() / result.pixelCount * 100
      println("Differences: ${result.pixelDifferences} (${percentDiff}%)")
      println(if (pass) "PASS" else "FAIL")
    }
  }

  exitProcess(0)
}
