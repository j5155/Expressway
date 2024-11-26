plugins {
    kotlin("jvm") version "2.0.20"
    `maven-publish`
}

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
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            name = "dairySnapshots"
            url = uri("https://repo.dairy.foundation/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "page.j5155.roadrunner"
            artifactId = "expressway"
            version = "0.1.0"
            from(components["java"])
        }
    }
}