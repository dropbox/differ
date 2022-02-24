package com.dropbox.differ.cli

import okio.FileSystem

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual val FileSystem.Companion.SYSTEM: FileSystem
  get() = okio.FileSystem.Companion.SYSTEM
