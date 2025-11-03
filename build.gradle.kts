// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Android Gradle Plugin (AGP)
        classpath(libs.gradle)
        // Hilt Gradle Plugin (optional)
        classpath(libs.hilt.android.gradle.plugin)
    }
}

plugins {
    // Use plugin IDs in module-level build files
    kotlin("jvm") version "2.2.21" apply false
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("com.google.devtools.ksp") version "2.2.21-2.0.4" apply false
}