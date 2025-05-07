pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-dev/ksp")
    }
    plugins {
        id("com.android.application") version "8.9.2" apply false
        id("org.jetbrains.kotlin.android") version "2.0.0" apply false
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
        id("com.google.dagger.hilt.android") version "2.56.1" apply false
        id("com.google.devtools.ksp") version "2.0.0-1.0.23" apply false
        id("org.jetbrains.kotlin.kapt") version "2.0.0" apply false
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
 