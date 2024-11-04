plugins {
    kotlin("jvm") version "2.0.20"
}

group = "page.j5155"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.brott.dev/")
}

dependencies {
    implementation("com.acmerobotics.roadrunner:actions:1.0.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}