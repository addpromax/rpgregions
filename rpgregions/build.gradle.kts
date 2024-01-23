repositories {
    maven("https://betonquest.org/nexus/repository/betonquest/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":folia"))

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.9.0")
    testImplementation("org.reflections:reflections:0.10.2")

    implementation("cloud.commandframework:cloud-paper:${properties["cloud_version"]}") {
        exclude("org.checkerframework")
    }
    implementation("cloud.commandframework:cloud-annotations:${properties["cloud_version"]}") {
        exclude("org.checkerframework")
    }
    implementation("cloud.commandframework:cloud-minecraft-extras:${properties["cloud_version"]}") {
        exclude("org.checkerframework")
        exclude("net.kyori")
    }

    implementation("net.wesjd:anvilgui:1.9.0-SNAPSHOT") // anvilgui
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.12") // inventory framework
    implementation("co.aikar:idb-core:1.0.0-SNAPSHOT") // database
    implementation("org.bstats:bstats-bukkit:3.0.2") // plugin stats
    implementation("io.papermc:paperlib:1.0.7") // paperlib - async teleport on Paper

    compileOnly("org.spigotmc:spigot-api:${properties["spigot_version"]}")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8") { // IMPLEMENTED VIA LIBRARIES
        exclude("org.checkerframework")
    }
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
    compileOnly("org.flywaydb:flyway-core:9.16.2") // IMPLEMENTED VIA LIBRARIES - db migration
    compileOnly("org.flywaydb:flyway-mysql:9.16.3") // IMPLEMENTED VIA LIBRARIES
    //compileOnly 'com.zaxxer:HikariCP:2.4.1' // IMPLEMENTED VIA LIBRARIES - database
    compileOnly("me.clip:placeholderapi:2.11.3") // PAPI
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") { // vault
        exclude("org.bukkit")
    }
    compileOnly("com.alonsoaliaga.alonsolevels:api:2.2.4") // alonsolevels
    compileOnly("me.blackvein.quests:quests-api:4.8.2") // quests
    compileOnly("me.blackvein.quests:quests-core:4.8.2") // quests
    compileOnly("pl.betoncraft:betonquest:1.12.11-SNAPSHOT") {
        exclude("org.bstats")
        exclude("org.apache.commons")
    }
    compileOnly("net.Indyuce:MMOCore-API:1.11.0-SNAPSHOT")
    compileOnly("com.github.shynixn.headdatabase:hdb-api:1.0") // head database
    compileOnly("com.github.plan-player-analytics:Plan:5.5.2307") // plan
    compileOnly("io.lumine:Mythic-Dist:5.3.5") {
        exclude("org.apache.commons")
        exclude("com.google")
    }
    compileOnly(":Dynmap-3.5-beta-3-spigot") // Dynmap
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT") {
        exclude("net.kyori")
    }
    compileOnly("com.ryandw11:CustomStructures:1.8.2")
}

configurations.all {
    exclude("commons-io")
    exclude("commons-codec")
}

tasks {
    javadoc {
        exclude("net/islandearth/rpgregions/translation/**")
        exclude("net/islandearth/rpgregions/listener/**")
        exclude("net/islandearth/rpgregions/gson/**")
        exclude("net/islandearth/rpgregions/commands/**")
        exclude("net/islandearth/rpgregions/utils/**")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    shadowJar {
        minimize()
    }
}
