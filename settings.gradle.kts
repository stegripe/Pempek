import java.util.Locale

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

if (!file(".git").exists()) {
    val errorText = """
        
        =====================[ ERROR ]=====================
         The Pempek project directory is not a properly cloned Git repository.
         
         In order to build Pempek from source you must clone
         the Pempek repository using Git, not download a code
         zip from GitHub.
         
         Built Pempek jars are available for download at
         https://pempek.stegripe.org/downloads
         
         See https://github.com/Stegripe/Pempek/blob/HEAD/CONTRIBUTING.md
         for further information on building and modifying Pempek.
        ===================================================
    """.trimIndent()
    error(errorText)
}

rootProject.name = "pempek"
for (name in listOf("pempek-api", "pempek-server")) {
    val projName = name.lowercase(Locale.ENGLISH)
    include(projName)
    findProject(":$projName")!!.projectDir = file(name)
}

optionalInclude("test-plugin")

fun optionalInclude(name: String, op: (ProjectDescriptor.() -> Unit)? = null) {
    val settingsFile = file("$name.settings.gradle.kts")
    if (settingsFile.exists()) {
        apply(from = settingsFile)
        findProject(":$name")?.let { op?.invoke(it) }
    } else {
        settingsFile.writeText(
            """
            // Uncomment to enable the '$name' project
            // include(":$name")

            """.trimIndent()
        )
    }
}

gradle.lifecycle.beforeProject {
    val mcVersion = providers.gradleProperty("mcVersion").get().trim()
    val pempekChannel = providers.gradleProperty("channel").get().trim()
    val pempekBuildNumber = providers.environmentVariable("BUILD_NUMBER").orNull?.trim()?.toInt()
    val versionString = if (pempekBuildNumber == null) {
        "$mcVersion.local-SNAPSHOT"
    } else {
        "$mcVersion.build.$pempekBuildNumber-${pempekChannel.lowercase()}"
    }
    version = versionString
}
