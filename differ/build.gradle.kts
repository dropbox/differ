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

    get("jvmTest").dependsOn(commonTest)

    val nativeTest by creating {
      dependsOn(commonTest)
    }

    get("iosX64Test").dependsOn(nativeTest)
    get("iosArm64Test").dependsOn(nativeTest)
    get("iosSimulatorArm64Test").dependsOn(nativeTest)
    get("linuxX64Test").dependsOn(nativeTest)
    get("macosX64Test").dependsOn(nativeTest)
    get("macosArm64Test").dependsOn(nativeTest)
    get("mingwX64Test").dependsOn(nativeTest)
  }
}

tasks.withType<KotlinJvmTest>().configureEach {
  extensions.configure(KoverTaskExtension::class) {
    isDisabled = false
  }
}
