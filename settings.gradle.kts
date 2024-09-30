pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

var osn = System.getProperty("os.name").lowercase()
var osa = System.getProperty("os.arch").lowercase()

rootProject.name = "libfvad-jni-${osn}-${osa}"
