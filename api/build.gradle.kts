plugins {
    id("maven-publish")
}

dependencies {
    implementation("com.github.Redempt:Crunch:1.1.2") // used to evaluating mathematical expressions

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.9.0")
    testImplementation("org.reflections:reflections:0.10.2")

    compileOnly("org.spigotmc:spigot-api:${properties["spigot_version"]}")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8") { // IMPLEMENTED VIA LIBRARIES
        exclude("org.checkerframework")
    }
    compileOnly("io.papermc:paperlib:1.0.7") // we include paperlib and relocate elsewhere
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") // vault
    compileOnly("me.clip:placeholderapi:2.11.3") // PAPI
    compileOnly("com.github.shynixn.headdatabase:hdb-api:1.0") // head database
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:${properties["worldguard_version"]}") {
        exclude("com.destroystokyo.paper")
        exclude("org.spigotmc")
        exclude("com.google")
    }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:${properties["worldedit_version"]}") {
        exclude("com.google")
        exclude("org.bukkit")
        exclude("org.spigotmc")
    }
    compileOnly("io.lumine:Mythic-Dist:5.3.5") {
        exclude("org.apache.commons")
        exclude("com.google")
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        // See Gradle docs for how to provide credentials to PasswordCredentials
        // https://docs.gradle.org/current/samples/sample_publishing_credentials.html
        maven {
            name = "convallyriaSnapshots"
            url = uri("https://repo.convallyria.com/snapshots/")
            credentials(PasswordCredentials::class)
        }
        maven {
            name = "convallyriaReleases"
            url = uri("https://repo.convallyria.com/releases/")
            credentials(PasswordCredentials::class)
        }
    }
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}