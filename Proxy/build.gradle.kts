plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    id("xyz.jpenilla.run-velocity") version "2.2.2"
}

group = "com.readutf.mcmatchmaker"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    } // This lets gradle find the BungeeCord files online
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.github.utfunderscore.MatchMaker:api-wrapper:a17105dd5f")

    implementation("co.aikar:acf-velocity:0.5.1-SNAPSHOT")

    //add velocity
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    //add bungee
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

}

tasks {
    test {
        useJUnitPlatform()
    }
    javadoc {
        options.encoding = "UTF-8"
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
    runVelocity {
        velocityVersion("3.3.0-SNAPSHOT")
    }
}

tasks.test {
    useJUnitPlatform()
}