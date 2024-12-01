import java.io.ByteArrayOutputStream

val snapshotsUsername: String = System.getenv("SNAPSHOTS_USERNAME")
val snapshotsPassword: String = System.getenv("SNAPSHOTS_PASSWORD")

val releasesUsername: String = System.getenv("RELEASES_USERNAME")
val releasesPassword: String = System.getenv("RELEASES_PASSWORD")

/* Gets the version name from the latest Git tag */
fun getVersionName(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags", "--always")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}
plugins {
    kotlin("jvm") version "2.0.20"
    `maven-publish`
}

apply(plugin = "org.jetbrains.dokka")

group = "page.j5155"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://maven.brott.dev/")
}

dependencies {
    implementation("com.acmerobotics.roadrunner:actions:1.0.0")
    implementation("com.acmerobotics.dashboard:dashboard:0.4.16") {
        exclude(group = "org.firstinspires.ftc")
    }

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}


publishing {
    repositories {
        maven {
            name = "dairyReleases"
            url = uri("https://repo.dairy.foundation/releases")
            credentials {
                username = releasesUsername
                password = releasesPassword
            }
        }
        maven {
            name = "dairySnapshots"
            url = uri("https://repo.dairy.foundation/snapshots")
            credentials {
                username = snapshotsUsername
                password = snapshotsPassword
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "page.j5155.roadrunner.expressway"
            artifactId = "core"
            version = getVersionName()
            from(components["java"])
        }
    }
}