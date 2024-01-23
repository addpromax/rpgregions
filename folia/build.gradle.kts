java {
    disableAutoTargetJvm()
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly(project(":api"))

    compileOnly("dev.folia:folia-api:${properties["folia_version"]}")
}
