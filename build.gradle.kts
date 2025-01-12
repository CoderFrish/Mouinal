plugins {
    java
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.13"
}

paperweight {
    upstreams.register("folia") {
        repo.set(github("PaperMC", "Folia"))
        ref.set(providers.gradleProperty("folia"))

        patchFile {
            path = "folia-server/build.gradle.kts"
            outputFile = file("mouinal-server/build.gradle.kts")
            patchFile = file("mouinal-server/build.gradle.kts.patch")
        }

        patchFile {
            path = "folia-api/build.gradle.kts"
            outputFile = file("mouinal-api/build.gradle.kts")
            patchFile = file("mouinal-api/build.gradle.kts.patch")
        }

        patchRepo("paperApi") {
            upstreamPath = "paper-api"
            patchesDir = file("mouinal-api/paper-patches")
            outputDir = file("paper-api")
        }

        patchRepo("paperApiGenerator") {
            upstreamPath = "paper-api-generator"
            patchesDir = file("mouinal-api-generator/paper-patches")
            outputDir = file("paper-api-generator")
        }

        patchDir("foliaApi") {
            upstreamPath = "folia-api"
            excludes = listOf("build.gradle.kts", "build.gradle.kts.patch", "paper-patches")
            patchesDir = file("mouinal-api/folia-patches")
            outputDir = file("folia-api")
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

subprojects {
    apply(plugin = "java-library")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
        options.isFork = true
    }

    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
}
