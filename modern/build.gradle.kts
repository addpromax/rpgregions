val ultraRegionsSupport = (properties.getValue("ultraRegionsSupport") as String).toBoolean()

tasks.compileJava {
    if (!ultraRegionsSupport) {
        sourceSets.main.get().java.exclude("**/net/islandearth/rpgregions/api/integrations/ultraregions/UltraRegionsIntegration.java")
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":rpgregions"))

    testImplementation("junit:junit:4.13.2")

    compileOnly("org.spigotmc:spigot-api:${properties["spigot_version"]}")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:${properties["worldguard_version"]}") {
        exclude("com.destroystokyo.paper")
        exclude("org.spigotmc")
    }
    compileOnly(":Residence4.9.2.2") // residence
    compileOnly(":GriefPrevention") // griefprevention
    compileOnly("com.github.angeschossen:LandsAPI:6.37.0") // lands
    compileOnly("com.griefdefender:api:2.1.0-SNAPSHOT") // GriefDefender
    if (ultraRegionsSupport) compileOnly(":UltraRegions") // ultraregions
}