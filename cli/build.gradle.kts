import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
  macosX64()
  macosArm64()
  linuxX64()
  mingwX64()

  sourceSets {
    val commonMain by getting {
      dependencies {}
    }
    val nativeMain by creating {
      dependsOn(commonMain)
      dependencies {
        implementation(libs.kotlinx.cli)
        implementation(libs.kotlinx.io)
        implementation(project(":differ"))
      }
    }

    get("linuxX64Main").dependsOn(nativeMain)
    get("macosX64Main").dependsOn(nativeMain)
    get("macosArm64Main").dependsOn(nativeMain)
    get("mingwX64Main").dependsOn(nativeMain)
  }

  targets.withType<KotlinNativeTarget> {
    binaries {
      executable {
        baseName = "differ"
        entryPoint = "com.dropbox.differ.cli.main"
      }
    }

    compilations["main"].cinterops {
      val stbImage by creating {
        includeDirs(defFile.parentFile.resolve("include"))
      }
    }
  }
}
