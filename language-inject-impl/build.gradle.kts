val parentProject = project

allprojects {
    dependencies {
        implementation(rootProject)
    }
}

subprojects {
    val minecraftVersion = name.substring(1)

    repositories {
        mavenLocal()
    }

    dependencies {
        implementation(parentProject)
        implementation("org.spigotmc:spigot:$minecraftVersion-R0.1-SNAPSHOT:remapped-mojang")
    }

    tasks {
        remap {
            version.set(minecraftVersion)
        }

        jar {
            finalizedBy(remap)
        }
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("commons-io:commons-io:2.13.0")
}