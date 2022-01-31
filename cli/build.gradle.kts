import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val hostArch = System.getProperty("os.arch")
    val isMingwX64 = hostOs.startsWith("Windows")

    val hostTarget = when {
        hostOs == "Mac OS X" && hostArch == "x86_64" -> macosX64("differ")
        hostOs == "Mac OS X" && hostArch == "arm64" -> macosArm64("differ")
        hostOs == "Linux" -> linuxX64("differ")
        isMingwX64 -> mingwX64("differ")
        else -> throw GradleException("Host OS '$hostOs($hostArch)' is not supported by Kotlin/Native")
    }

    hostTarget.apply {
        binaries {
            executable {
                baseName = "differ"
                entryPoint = "com.dropbox.differ.cli.main"
            }
        }
    }

    sourceSets {
        val differMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
                implementation("org.jetbrains.kotlinx:kotlinx-io:0.1.16")
                implementation(project(":differ"))
            }
        }
    }

    targets.withType<KotlinNativeTarget> {
        compilations["main"].cinterops {
            val stbImage by creating {
                includeDirs(defFile.parentFile.resolve("include"))
            }
        }
    }
}