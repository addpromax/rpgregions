![rpgregions](https://fortitude.islandearth.net/rpgregions-main-banner.png)
# RPGRegions 2

RPGRegions is the all-in-one region exploration plugin that will finally make it exciting for your players to explore. Discover high mountain ranges and the deepest dungeons. Create complex, dungeon-like areas with various dependencies, requirements, and reward them for their efforts.

It is comparable to that of Skyrim discoverable places, and is highly recommended for RPG servers.

# Links
- [Spigot](https://www.spigotmc.org/resources/rpgregions-1-12-1-15.74479/)
- [Wiki](https://fortitude.islandearth.net)
- [Discord](https://discord.gg/fh62mxU)

## About RPGRegions
RPGRegions lets you create configurable regions that your players can discover. Aiming to promote exploration, you set titles, sounds, deliver unlimited rewards, and execute commands to do whatever you want.

## What makes it different to v1?
RPGRegions 2 is the successor to RPGRegions 1. The entire plugin has been recoded and has an entirely new design. With performance in mind, it is the only discovery plugin that is up-to-date, per-region configs, and supporting SQL.

# Compiling
RPGRegions uses gradle and builds on JDK 16. Clone the repository and run `./gradlew build` in the directory.

## Compilation errors
You may get an error where UltraRegions cannot be resolved. To fix this, go to `gradle.properties` and set `ultraRegionsSupport` to `false`.
Note that this will disable support for UltraRegions.

To enable UltraRegions support, place the `UltraRegions.jar` file into the `libraries` folder.

