import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import kotlinx.kover.api.CoverageEngine.JACOCO
import kotlinx.kover.tasks.KoverMergedHtmlReportTask
import kotlinx.kover.tasks.KoverMergedXmlReportTask

plugins {
  alias(libs.plugins.kotlin.multiplatform).apply(false)
  alias(libs.plugins.kover)
  alias(libs.plugins.mavenPublish)
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

  plugins.withId("com.vanniktech.maven.publish.base") {
    configure<MavenPublishBaseExtension> {
      publishToMavenCentral(SonatypeHost.DEFAULT)
      signAllPublications()
      pom {
        description.set("A simple, lightweight, multiplatform image diffing library.")
        name.set(project.name)
        url.set("https://github.com/dropbox/differ/")
        licenses {
          license {
            name.set("The Apache Software License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            distribution.set("repo")
          }
        }
        scm {
          url.set("https://github.com/dropbox/differ/")
          connection.set("scm:git:git://github.com/dropbox/differ.git")
          developerConnection.set("scm:git:ssh://git@github.com/dropbox/differ.git")
        }
        developers {
          developer {
            id.set("dropbox")
            name.set("Dropbox, Inc.")
          }
        }
      }
    }
  }
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

tasks.register("printVersionName") {
  doLast {
    val VERSION_NAME: String by project
    println(VERSION_NAME)
  }
}
