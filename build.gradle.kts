plugins {
    id("io.freefair.lombok") version "8.7.1"
    id("io.github.goooler.shadow") version "8.1.7"
    id("java")
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")
version = "1.0." + (buildNumber?.let { "-$it" } ?: "")

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.11.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    shadowJar {
        archiveBaseName.set(project.name)
        archiveClassifier.set(null as String?)
        archiveVersion.set(null as String?)

        val libNamespace = "${project.group}.${project.name}.libs"
        val relocations = mapOf(
            "org.jetbrains.annotations" to "$libNamespace.jetbrains.annotations",
            "org.intellij.lang.annotations" to "$libNamespace.intellij.lang.annotations"
        )

        relocations.forEach { (original, relocated) ->
            relocate(original, relocated)
        }

        mergeServiceFiles()
    }

    processResources {
        inputs.properties("name" to rootProject.name)
        inputs.properties("version" to project.version)
        inputs.properties("group" to project.group)
        inputs.properties("description" to project.properties["description"])
        inputs.properties("apiVersion" to project.properties["apiVersion"])
        inputs.properties("authors" to project.properties["authors"])
        inputs.properties("website" to project.properties["website"])

        filesMatching("plugin.yml") {
            expand(inputs.properties)
        }
    }

    build {
        dependsOn(shadowJar)
    }
}