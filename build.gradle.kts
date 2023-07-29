plugins {
    java
    id("io.github.patrick.remapper") version "1.4.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

group = "io.github.patrick-choe"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.github.patrick.remapper")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
}

tasks {
    create<Jar>("bundleJar") {
        archiveClassifier.set("bundle")

        allprojects.forEach { project ->
            val task = project.tasks["jar"] as Jar
            from(zipTree(task.archiveFile))
            dependsOn(task)
        }
    }
}