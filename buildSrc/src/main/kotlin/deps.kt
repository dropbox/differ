import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object versions {
  const val kotlin = "1.6.10"
  const val kover = "0.5.0"
}

// Plugins
fun PluginDependenciesSpec.kover() = id("org.jetbrains.kotlinx.kover").version(versions.kover)

object deps {
  object kotlin {
    const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"
  }
}
