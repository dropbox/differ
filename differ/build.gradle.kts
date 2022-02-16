import kotlinx.kover.api.KoverTaskExtension
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

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
      }
    }
    get("iosX64Main").dependsOn(commonMain)
    get("iosArm64Main").dependsOn(commonMain)
    get("iosSimulatorArm64Main").dependsOn(commonMain)
    get("linuxX64Main").dependsOn(commonMain)
    get("macosX64Main").dependsOn(commonMain)
    get("macosArm64Main").dependsOn(commonMain)
    get("mingwX64Main").dependsOn(commonMain)
  }
}

tasks.withType<KotlinJvmTest>().configureEach {
  extensions.configure(KoverTaskExtension::class) {
    isDisabled = false
  }
}
