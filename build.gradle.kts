plugins {
    id("com.github.johnrengelman.shadow") version("8.1.1")
    id("java")
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation(project(":rpgregions", "shadow"))
    implementation(project(":modern", "shadow"))
}

allprojects {
    group = "net.islandearth.rpgregions"
    version = "1.4.6"

    //tasks.withType(JavaCompile).configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    //it.options.encoding = "UTF-8"

    // Force warnings
    //options.compilerArgs << '-Xlint:all'
    //options.deprecation = true
    //}

    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "java")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        disableAutoTargetJvm()
    }

    repositories {
        mavenCentral()
        mavenLocal()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://jitpack.io")
        maven("https://repo.convallyria.com/releases")
        maven("https://repo.convallyria.com/snapshots")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.aikar.co/content/groups/aikar/")

        // worldguard
        maven("https://maven.enginehub.org/repo/")

        // PAPI
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        // MythicMobs
        maven("https://mvn.lumine.io/repository/maven-public/")

        // Dynmap
        maven("https://repo.mikeprimm.com")

        // GriefDefender
        maven("https://repo.glaremasters.me/repository/bloodshot")

        // Crunch
        maven("https://redempt.dev")

        flatDir { dir("../libraries") }
    }

    dependencies {
        implementation("com.convallyria.languagy:api:3.0.3-SNAPSHOT")
        implementation("com.google.code.gson:gson:2.10.1")

        compileOnly("net.kyori:adventure-api:4.14.0")
        compileOnly("net.kyori:adventure-platform-bukkit:4.3.2") // IMPLEMENTED VIA LIBRARIES
        compileOnly("net.kyori:adventure-text-minimessage:4.14.0") // IMPLEMENTED VIA LIBRARIES
    }

    tasks {
        test {
            useJUnitPlatform()

            testLogging {
                events("passed", "skipped", "failed")
            }
        }

        shadowJar {
            archiveClassifier.set("")

            relocate("com.google.gson", "net.islandearth.rpgregions.libs.gson")
            relocate("cloud.commandframework", "net.islandearth.rpgregions.libs.commandframework")
            relocate("io.leangen.geantyref", "net.islandearth.rpgregions.libs.typetoken")
            relocate("com.convallyria.languagy", "net.islandearth.rpgregions.libs.languagy")
            relocate("io.papermc.lib", "net.islandearth.rpgregions.libs.paperlib")
            relocate("redempt.crunch", "net.islandearth.rpgregions.libs.crunch")
			relocate("co.aikar.idb", "net.islandearth.rpgregions.libs.idb")
			relocate("com.github.stefvanschie.inventoryframework", "net.islandearth.rpgregions.libs.inventoryframework")
			relocate("org.bstats", "net.islandearth.rpgregions.libs.bstats")
			relocate("me.lucko.helper", "net.islandearth.rpgregions.libs.helper")
			relocate("net.wesjd", "net.islandearth.rpgregions.libs.anvilgui")
        }

        build {
            dependsOn(shadowJar)
        }

        compileJava {
            options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

            // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
            // See https://openjdk.java.net/jeps/247 for more information.
            options.release.set(16)
        }

        processResources {
            filesMatching("plugin.yml") {
                expand("version" to project.version)
            }
        }
    }
}