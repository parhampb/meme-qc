val invoker by configurations.creating

plugins {
    kotlin("jvm")
}

group = "com.siliconatom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.cloud.functions:functions-framework-api:1.0.1")

    implementation(project(":domain-eviction"))
    implementation(project(":repository-slack"))
    implementation(project(":comms-slack"))

    invoker("com.google.cloud.functions.invoker:java-function-invoker:1.0.0-alpha-2-rc5")
}

task<JavaExec>("runFunction") {
    main = "com.google.cloud.functions.invoker.runner.Invoker"
    classpath(invoker)
    inputs.files(configurations.runtimeClasspath, sourceSets["main"].output)
    args(
        "--target", project.findProperty("runFunction.target") ?: "",
        "--port", project.findProperty("runFunction.port") ?: 8080
    )
    doFirst {
        args("--classpath", files(configurations.runtimeClasspath, sourceSets["main"].output).asPath)
    }
}
