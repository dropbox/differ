package com.dropbox.differ.cli

import com.dropbox.differ.ImageComparator
import com.dropbox.differ.SimpleImageComparator
import com.dropbox.differ.Mask
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  val parser = ArgParser("differ")
  val threshold by parser.option(
    ArgType.Double,
    fullName = "threshold",
    shortName = "t",
    description = "Percent of pixels that can differ while still resulting in a success."
  ).default(0.001)
  val leftPath by parser.argument(ArgType.String, "First Image path", "Path to the first image for comparison")
  val rightPath by parser.argument(ArgType.String, "Second Image path", "Path to the second image for comparison")

  parser.parse(args)

  val comparator = SimpleImageComparator()
  val mask = withImage(leftPath) { leftImage ->
    withImage(rightPath) { rightImage ->
      val mask = Mask(leftImage.width, leftImage.height)
      comparator.compare(leftImage, rightImage, mask)
      mask
    }
  }

  if (mask.difference > threshold) {
    println("Image comparison failed with difference: ${mask.difference}")
    exitProcess(1)
  } else {
    println("Image comparison succeeded.")
    exitProcess(0)
  }
}