import net.ltgt.gradle.errorprone.*;

apply(plugin = "antlr")
apply(plugin = "java-library")
apply(plugin = "com.palantir.baseline-error-prone")

val antlrRuntime by configurations.creating

dependencies {
  compile("com.palantir.safe-logging:preconditions:1.11.0")
  testCompile("com.palantir.safe-logging:preconditions-assertj:1.11.0")
  annotationProcessor("org.graalvm.truffle:truffle-api:19.2.0.1")
  annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:19.2.0.1")
  "antlr"("org.antlr:antlr4:4.7.2")
  "antlrRuntime"("org.antlr:antlr4-runtime:4.7.2")
  implementation("org.graalvm.truffle:truffle-api:19.2.0.1")
  implementation("org.graalvm.sdk:graal-sdk:19.2.0.1")
  implementation("org.antlr:antlr4-runtime:4.7.2")
  testImplementation("org.testng:testng:6.14.3")
  implementation("com.palantir.safe-logging:safe-logging")
  implementation("com.palantir.safe-logging:preconditions")
}

tasks.getByName<Jar>("jar") {
  baseName = "cadenza-language"
}

tasks.withType<JavaCompile> {
  options.errorprone {
    check("SwitchStatementDefaultCase", CheckSeverity.OFF)
  }
}

tasks.withType<AntlrTask> {
  arguments.addAll(listOf("-package", "cadenza.syntax", "-no-listener", "-visitor"))
}
