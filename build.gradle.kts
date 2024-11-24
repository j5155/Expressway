plugins {
    kotlin("jvm") version "2.0.20"
    id("com.android.library") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
}


repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}


kotlin {
    jvmToolchain(18)
}
