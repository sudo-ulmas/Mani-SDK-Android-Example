pluginManagement {
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
        maven(url = "https://storage.googleapis.com/download.flutter.io")
        maven(url = "/Users/abdullah/flutterProjects/mani_auth/build/host/outputs/repo")
        maven ( url = "https://artifactory.aigroup.uz:443/artifactory/myid" )
    }
}

rootProject.name = "AddToAppAndroid"
include(":app")
