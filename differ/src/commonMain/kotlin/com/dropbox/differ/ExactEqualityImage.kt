package com.dropbox.differ

/**
 * The outcome of an exact-equality check between two images.
 *
 * There is deliberately no "not equal" value: raw pixel data being different does not mean a
 * comparison would fail, since a comparator may apply tolerance or shift. The only actionable
 * answers are "provably identical" and "cannot tell".
 */
sealed interface ExactEqualityResult {
  /** Both images have identical dimensions and every pixel is identical. */
  data object Equal : ExactEqualityResult

  /** Equality could not be established; the full comparison must run. */
  data object Unknown : ExactEqualityResult
}

/**
 * An [Image] that can cheaply check whether it is pixel-for-pixel identical to another image,
 * for example by comparing backing pixel storage directly instead of reading pixels one by one.
 *
 * Contract:
 * - Return [ExactEqualityResult.Equal] only when both images have identical dimensions and
 *   [Image.getPixel] would return an equal [Color] at every coordinate.
 * - Return [ExactEqualityResult.Unknown] whenever that cannot be established, including when
 *   [other] is of a type the implementation does not understand.
 *
 * Comparators may use this to skip the per-pixel comparison: two provably identical images
 * cannot produce a difference under any tolerance or shift.
 */
interface ExactEqualityImage : Image {
  fun checkExactEquality(other: Image): ExactEqualityResult
}
