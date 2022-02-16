import kotlinx.kover.api.CoverageEngine.JACOCO
import kotlinx.kover.tasks.KoverMergedHtmlReportTask
import kotlinx.kover.tasks.KoverMergedXmlReportTask

plugins {
  alias(libs.plugins.kotlin.multiplatform).apply(false)
  alias(libs.plugins.kover)
}

repositories {
  mavenCentral()
  gradlePluginPortal()
  google()
}

kover {
  coverageEngine.set(JACOCO)
}

tasks.withType<KoverMergedHtmlReportTask>().configureEach {
  isEnabled = true
}
tasks.withType<KoverMergedXmlReportTask>().configureEach {
  isEnabled = true
}

allprojects {
  group = project.property("GROUP") as String
  version = project.property("VERSION_NAME") as String
}

subprojects {
  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
  }

  repositories {
    mavenCentral()
  }
}
