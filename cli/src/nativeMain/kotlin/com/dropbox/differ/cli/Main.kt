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
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.exit

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
expect val FileSystem.Companion.SYSTEM: FileSystem

fun main(args: Array<String>) {
  val parser = ArgParser("differ")
  val verbose by parser
    .option(ArgType.Boolean, fullName = "verbose", description = "Print verbose comparison output.")
    .default(false)
  val threshold by parser
    .option(ArgType.Double, fullName = "threshold", shortName = "t", description = "Percent of pixels that can differ while still resulting in a success.")
    .default(0.001)
  val delta by parser
    .option(ArgType.Double, fullName = "delta", shortName = "d", description = "Max distance colors can be in 4 dimensional space without triggering a difference.")
    .default(0.08)
  val hShift by parser
    .option(ArgType.Int, fullName = "h-shift", description = "Acceptable horizontal shift of pixel.")
    .default(0)
  val vShift by parser
    .option(ArgType.Int, fullName = "v-shift", description = "Acceptable vertical shift of pixel.")
    .default(0)
  val outputFile by parser.option(ArgType.String, "output", "Path at which the output file should be written.")
  val leftImageString by parser.argument(ArgType.String, "First Image path", "Path to the first image for comparison")
  val rightImageString by parser.argument(ArgType.String, "Second Image path", "Path to the second image for comparison")

  parser.parse(args)

  val leftPath = leftImageString.toPath().let {
    if (it.isRelative) {
      FileSystem.SYSTEM.canonicalize(".".toPath()).resolve(it)
    } else {
      it
    }
  }
  val rightPath = rightImageString.toPath().let {
    if (it.isRelative) {
      FileSystem.SYSTEM.canonicalize(".".toPath()).resolve(it)
    } else {
      it
    }
  }

  // Ensure the files exist
  if (!FileSystem.SYSTEM.exists(leftPath)) {
    println("first image path doesn't exist: $leftPath")
    exit(1)
  }
  if (!FileSystem.SYSTEM.exists(rightPath)) {
    println("second image path doesn't exist: $rightPath")
    exit(1)
  }

  val startTime = getTimeMicros()
  val comparator = SimpleImageComparator(
    maxDistance = delta.toFloat(),
    hShift = hShift,
    vShift = vShift,
  )

  // Check the file metadata to see if we need to process files or directories
  val leftPathMeta = FileSystem.SYSTEM.metadata(leftPath)
  val rightPathMeta = FileSystem.SYSTEM.metadata(rightPath)

  data class Result(
    val success: Boolean,
    val result: ImageComparator.ComparisonResult? = null,
    val mask: Mask? = null,
  )

  fun compareImages(left: Path, right: Path, output: Path?): Result {
    if (verbose) {
      print("Comparing \'${left.name}\' and \'${right.name}\'...")
    }

    return withImage(left.toString()) { leftImage ->
      withImage(right.toString()) { rightImage ->
        val mask = output?.let { Mask(leftImage.width, leftImage.height) }
        val result = comparator.compare(leftImage, rightImage, mask)
        val diff = result.pixelDifferences.toFloat() / result.pixelCount
        val pass = diff <= threshold

        if (verbose) {
          if (pass) {
            println("pass")
          } else {
            println("fail (${result.pixelDifferences} / ${result.pixelCount} pixels differ, ${(result.pixelDifferences / result.pixelCount.toFloat()) * 100} %)")
          }
        }

        Result(
          success = pass,
          result = result,
          mask = mask,
        )
      }
    }
  }

  val results = if (leftPathMeta.isDirectory && rightPathMeta.isDirectory) {
    FileSystem.SYSTEM.list(leftPath).map { leftImagePath ->
      val rightImagePath = rightPath.resolve(leftImagePath.name)
      if (!FileSystem.SYSTEM.exists(rightImagePath)) {
        return@map Result(success = false)
      }

      val outputImagePath = outputFile?.toPath()?.resolve(leftImagePath.name)

      compareImages(leftImagePath, rightImagePath, outputImagePath)
    }
  } else {
    listOf(compareImages(leftPath, rightPath, outputFile?.toPath()))
  }

  if (verbose) {
    println("Complete with ${results.filter { it.success }.size} / ${results.size} successes")
    println("Time: ${(getTimeMicros() - startTime) / 1000f}ms")
    println(if (results.all { it.success }) "PASS" else "FAIL")
  }

  exitProcess(if (results.all { it.success }) 0 else 1)
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
