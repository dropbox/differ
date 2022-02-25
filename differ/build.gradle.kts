import kotlinx.kover.api.KoverTaskExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithSimulatorTests
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeHostTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
  alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
  jvm {
    withJava()
  }
  iosX64()
  iosArm64()
  iosSimulatorArm64()
  linuxX64()
  macosX64()
  macosArm64()
  mingwX64()

  sourceSets {
    val commonMain by getting {
      dependencies {
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.okio)
      }
    }
    get("iosX64Main").dependsOn(commonMain)
    get("iosArm64Main").dependsOn(commonMain)
    get("iosSimulatorArm64Main").dependsOn(commonMain)
    get("linuxX64Main").dependsOn(commonMain)
    get("macosX64Main").dependsOn(commonMain)
    get("macosArm64Main").dependsOn(commonMain)
    get("mingwX64Main").dependsOn(commonMain)
    get("jvmMain").dependsOn(commonMain)

    get("jvmTest").dependsOn(commonTest)

    val nativeTest by creating {
      dependsOn(commonTest)
    }
    get("linuxX64Test").dependsOn(nativeTest)
    get("macosX64Test").dependsOn(nativeTest)
    get("macosArm64Test").dependsOn(nativeTest)
    get("mingwX64Test").dependsOn(nativeTest)

    val iosTest by creating {
      dependsOn(nativeTest)
    }

    get("iosX64Test").dependsOn(iosTest)
    get("iosArm64Test").dependsOn(iosTest)
    get("iosSimulatorArm64Test").dependsOn(iosTest)
  }

  targets.withType(KotlinNativeTargetWithSimulatorTests::class.java).configureEach {
    val defaultTestRun = testRuns["test"]
    tasks.findByName("${defaultTestRun.target.targetName}ProcessResources")?.let { processResources ->
      val copyTask = tasks.register<Copy>("${targetName}CopyTestResources") {
        from("src/commonTest/resources")
        into(File(defaultTestRun.executionSource.binary.outputDirectory, "resources"))
      }

      processResources.dependsOn(copyTask)
    }
  }
}

tasks.withType<KotlinJvmTest>().configureEach {
  extensions.configure(KoverTaskExtension::class) {
    isDisabled = false
  }
}
