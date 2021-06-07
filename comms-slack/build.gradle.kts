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
    implementation(project(":domain-eviction"))
    implementation("com.slack.api:slack-api-client:1.8.1")
}
