plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    kotlin("android") version "1.8.10" apply false
    kotlin("kapt") version "1.8.10" apply false
}

task("clean") {
    delete(rootProject.buildDir)
}