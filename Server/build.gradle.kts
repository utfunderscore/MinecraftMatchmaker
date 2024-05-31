import org.codehaus.plexus.util.Os

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.5.10" // Check for new versions at https://plugins.gradle.org/plugin/io.papermc.paperweight.userdev
    id("xyz.jpenilla.run-paper") version "2.2.3" // Adds runServer and runMojangMappedServer tasks for testing
    id("maven-publish")
}

group = "com.readutf.mcmatchmaker"
version = "1.0-SNAPSHOT"
description = "Matchmaking instance"


repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")


    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        compileOnly("com.readutf.inari:development:1.0")
        implementation("com.readutf.matchmaker:client:1.3.1")
        implementation("com.readutf.matchmaker:shared:1.3.1")
    } else {
        compileOnly("com.github.utfunderscore.MinigameFramework:development:31028f3ac6:dev")
        compileOnly("com.github.utfunderscore.MinigameFramework:core:31028f3ac6:dev")

        implementation("com.github.utfunderscore.MatchMaker:client:master-SNAPSHOT")
        implementation("com.github.utfunderscore.MatchMaker:shared:master-SNAPSHOT")
    }


    implementation("com.github.docker-java:docker-java:3.2.13")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.readutf.mcmatchmaker"
            artifactId = "Server"
            version = "1.1"

            from(components["java"])
        }
    }
}



tasks.test {
    useJUnitPlatform()
}

tasks {
    // Configure reobfJar to run when invoking the build task

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything



        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
                "name" to project.name,
                "version" to project.version,
                "description" to project.description,
                "apiVersion" to "1.20"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.20.2")

    }

    /*
    reobfJar {
      // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
      // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
      outputJar.set(layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar"))
    }
     */
}