package com.dropbox.differ.cli

import com.dropbox.differ.ImageComparator
import com.dropbox.differ.SimpleImageComparator
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

    val leftImage = FileImage(leftPath)
    val rightImage = FileImage(rightPath)

    val comparator = SimpleImageComparator()
    val mask = comparator.compare(leftImage, rightImage)

    val difference = mask.count.toDouble() / mask.size
    if (difference > threshold) {
        println("Image comparison failed with difference: $difference")
        exitProcess(1)
    } else {
        println("Image comparison succeeded.")
        exitProcess(0)
    }
}