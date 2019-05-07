import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.30"
    id("org.jetbrains.dokka") version "0.9.17"
}

group = "rspsUtil"
version = "1.14"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xuse-experimental=kotlin.contracts.ExperimentalContracts")
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
    outputFormat = "html"
    outputDirectory = "documentation\\html"
}