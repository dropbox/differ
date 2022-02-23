package com.dropbox.differ.resources

/** Reads a resource as a byte array. */
expect fun readResource(path: String): ByteArray
