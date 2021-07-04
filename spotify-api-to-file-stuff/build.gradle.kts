import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"
    application
}

group = "dev.niggelgame"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://schlaubi.jfrog.io/artifactory/envconf/")
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:1.6.1"))

    // WebServer for Redirect
    implementation("io.ktor", "ktor-server-core")
    implementation("io.ktor", "ktor-server-netty")
//    implementation("io.ktor", "ktor-client-core")
//    implementation("io.ktor", "ktor-client-okhttp")

    // Spotify API Stuff
    implementation("com.adamratzman", "spotify-api-kotlin-core", "3.7.0")
    implementation("com.soywiz.korlibs.krypto", "krypto", "2.0.7")
    implementation("com.soywiz.korlibs.korim", "korim", "2.0.9")

    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.2.0")
    implementation("org.jetbrains.kotlinx", "kotlinx-datetime", "0.2.1")

    // Env Config
    implementation("dev.schlaubi", "envconf", "1.0")

}

application {
    mainClass.set("dev.niggelgame.spotify.ApplicationKt")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "16"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}