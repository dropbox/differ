import org.gradle.kotlin.dsl.PluginDependenciesSpecScope

object versions {
  const val kotlin = "1.6.10"
  const val kover = "0.5.0"
}

// Plugins
fun PluginDependenciesSpecScope.kover() = id("org.jetbrains.kotlinx.kover").version("0.5.0")

object deps {
  object kotlin {
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"
  }
}
