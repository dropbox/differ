import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
}

kotlin {
    macosX64()
    macosArm64()
    linuxX64()
    mingwX64()

    targets.withType<KotlinNativeTarget>() {
        binaries {
            executable {
                baseName = "differ"
                entryPoint = "com.dropbox.differ.cli.main"
            }
        }
    }

    sourceSets {
        val nativeMain by creating {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
                implementation("org.jetbrains.kotlinx:kotlinx-io:0.1.16")
                implementation(project(":differ"))
            }
        }

        get("linuxX64Main").dependsOn(nativeMain)
        get("macosX64Main").dependsOn(nativeMain)
        get("macosArm64Main").dependsOn(nativeMain)
        get("mingwX64Main").dependsOn(nativeMain)
    }

    targets.withType<KotlinNativeTarget> {
        compilations["main"].cinterops {
            val stbImage by creating {
                includeDirs(defFile.parentFile.resolve("include"))
            }
        }
    }
}