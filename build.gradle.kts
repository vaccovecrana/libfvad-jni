plugins { id("io.vacco.oss.gitflow") version "1.0.1" }

apply(plugin = "io.vacco.oss.gitflow")
group = "io.vacco.fvad"
version = "0.1.0-532ab66" // in sync with https://github.com/dpirch/libfvad

configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  addJ8Spec()
  addClasspathHell()
  sharedLibrary(true, false)
}

val copyJni = tasks.register<Copy>("copyFfJni") {
  from("./src/main/c/libfvad.so")
  into("./build/resources/main/io/vacco/fvad")
}

tasks.processResources {
  dependsOn(copyJni)
}
