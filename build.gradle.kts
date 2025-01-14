import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.10"
    id("fabric-loom") version "1.9.2"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String
val minecraft_version = project.property("minecraft_version") as String
val yarn_mappings = project.property("yarn_mappings") as String
val loader_version = project.property("loader_version") as String
val kotlin_loader_version = project.property("kotlin_loader_version") as String
val fabric_version = project.property("fabric_version") as String
val geckolib_version = project.property("geckolib_version") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("world-of-labyrinth") {
            sourceSet("main")
        }
    }
}

repositories {
    maven {
        name = "GeckoLib"
        url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
        content {
            includeGroup("software.bernie.geckolib")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${kotlin_loader_version}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
    modImplementation("software.bernie.geckolib:geckolib-fabric-${minecraft_version}:${geckolib_version}")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", minecraft_version)
    inputs.property("loader_version", loader_version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to minecraft_version,
            "loader_version" to loader_version,
            "kotlin_loader_version" to kotlin_loader_version,
            "geckolib_version" to geckolib_version
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    repositories {
    }
}
