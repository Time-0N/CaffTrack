pluginManagement {
    plugins {
        id("com.android.application") version "8.9.3" apply false
        id("org.jetbrains.kotlin.android") version "2.0.0" apply false
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
        id("com.google.dagger.hilt.android") version "2.51.1" apply false
        id("org.jetbrains.kotlin.kapt") version "2.0.0" apply false
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CaffTrack"
include(":app")
 