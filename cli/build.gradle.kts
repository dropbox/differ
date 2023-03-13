import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  id("application")
}

kotlin {
  jvm()
//  macosX64()
  macosArm64()
//  linuxX64()
//  mingwX64()


  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.kotlinx.cli)
        implementation(libs.kotlinx.io)
        implementation(project(":differ"))
      }
    }
    val nativeMain by creating {
      dependsOn(commonMain)
      dependencies {}
    }

//    get("linuxX64Main").dependsOn(nativeMain)
//    get("macosX64Main").dependsOn(nativeMain)
    get("macosArm64Main").dependsOn(nativeMain)
//    get("mingwX64Main").dependsOn(nativeMain)
  }

  targets.withType<KotlinNativeTarget> {
    binaries {
      executable {
        baseName = "differ"
        entryPoint = "com.dropbox.differ.cli.main"
      }
    }

    compilations["main"].cinterops {
      val libpng by creating {}
      val stbImage by creating {
        includeDirs(defFile.parentFile.resolve("include"))
      }
    }
  }
}

application {
  mainClass.set("com.dropbox.differ.cli.jvm.JvmMainKt")
}
