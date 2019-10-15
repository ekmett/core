plugins {
  antlr
  application
  java
  maven
}

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  annotationProcessor("org.graalvm.truffle:truffle-api:19.2.0.1")
  annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:19.2.0.1")
  antlr("org.antlr:antlr4:4.7.2")
  implementation("org.graalvm.truffle:truffle-api:19.2.0.1")
  implementation("org.graalvm.truffle:truffle-tck:19.2.0.1")
  implementation("org.graalvm.sdk:graal-sdk:19.2.0.1")
  implementation("org.graalvm.sdk:launcher-common:19.2.0.1")
  implementation("org.antlr:antlr4-runtime:4.7.2")
  // implementation("org.yuvalshavit:antlr-denter:1.1")
  testImplementation("org.testng:testng:6.14.3")
}

application {
  // mainClassName = "cadenza.CoreLauncher"
  mainClassName = "cadenza.Main"
  applicationDefaultJvmArgs = listOf("-XX:+UnlockExperimentalVMOptions","-XX:+EnableJVMCI","-Dtruffle.class.path.append=build/libs/cadenza.jar")
}

val jar by tasks.getting(Jar::class) {
  manifest {
    attributes["Bundle-Name"] = "Cadenza"
    attributes["Bundle-Symbolic-Name"] = "cadenza"
    attributes["Bundle-Version"] = "0.0"
    attributes["Bundle-RequireCapability"] = "org.graalvm;filter:=\"(&(graalvm_version=19.2.0)(os_arch=amd64))\""
    attributes["x-GraalVM-Polyglot-Part"] = "True"
    attributes["Main-Class"] = "cadenza.Main"
  }
}

val test by tasks.getting(Test::class) {
  useTestNG()
}

