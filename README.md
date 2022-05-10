# Differ

Differ is a multiplatform image diffing library that's designed to be lightweight and flexible.

Differ's default `ImageComparator`, `SimpleImageComparator`, does basic per-pixel
[Euclidean distance](https://en.wikipedia.org/wiki/Color_difference) comparisons, returning a result of
the number of pixels whose distance is greater than the provided `maxDistance`. An optional shift value
can be applied both horizontally and vertically, to allow the comparator to account for differences which
are the result of anti-aliasing errors.

## Usage

The simplest comparison simply takes two Images and returns a result that has the number of pixels that
are different between the two.

```kotlin
val differ = SimpleImageComparator()
val result = differ.compare(left, right)

if (result.pixelDifferences > 0) {
    println("Images are different.")
} else {
    println("Images are identical.")
}
```

`SimpleImageComparator` can be configured to adjust the sensitivity of it's comparison using the `maxDistance`
property, which defines the max distance that two colors can be from each other in 4 dimensional space without
triggering a difference.

```kotlin
// This comparator will be more lenient than the default.
val differ = SimpleImageComparator(maxDistance = 0.007)
val result = differ.compare(left, right)

if (result.pixelDifferences > 0) {
    println("Images are different.")
} else {
    println("Images are identical.")
}
```

### Anti-Aliasing Support

If bitmap images are resized, it's common for some pixels to have minor differences not because the underlying
image differs, but because of anti-aliasing errors, as pixels are approximated.

`SimpleImageComparator` allows you to compensate for this by allowing you to define a horizontal and vertical
shift, which allows the comparator to take neighboring pixels within the shift window into account when comparing
pixels.

```kotlin
// This comparator will use the neighboring 2 pixels, both horizontally and vertically,
//  to account for potential anti-alias issues.
val differ = SimpleImageComparator(hShift = 2, vShift = 2)
val result = differ.compare(left, right)

if (result.pixelDifferences > 0) {
    println("Images are different.")
} else {
    println("Images are identical.")
}
```

> Note that adding a shift window can potentially increase comparison times exponentially, since for every pixel that doesn't immediately match the comparator will compare another 2 pixels for every shift value.

## License

    Copyright (c) 2022 Dropbox, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


