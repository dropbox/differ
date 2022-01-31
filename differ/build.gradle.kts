plugins {
    kotlin("multiplatform")
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
                implementation(kotlin("test"))
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
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
