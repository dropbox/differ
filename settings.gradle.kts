pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }

  val kotlin_version: String by settings
  plugins {
    kotlin("multiplatform") version kotlin_version
  }
}

rootProject.name = "differ-parent"

include(":differ")
include(":cli")
