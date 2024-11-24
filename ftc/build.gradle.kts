plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.brott.dev/")
}

dependencies {
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("org.firstinspires.ftc:RobotCore:10.1.1")
    implementation("com.acmerobotics.dashboard:dashboard:0.4.16") {
        exclude(group="org.firstinspires.ftc")
    }
    implementation("com.acmerobotics.roadrunner:actions:1.0.0")
}


android {
    namespace = "page.j5155.roadrunner.actionadditions"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
