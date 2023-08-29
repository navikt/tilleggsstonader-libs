val javaVersion = JavaLanguageVersion.of(17)

plugins {
    kotlin("jvm") version "1.9.10"
    `maven-publish`
    `java-library`
    id("com.diffplug.spotless") version "6.21.0"
    id("com.github.ben-manes.versions") version "0.47.0"
}

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            ktlint("0.50.0")
        }
    }

    configurations.all {
        resolutionStrategy {
            failOnNonReproducibleResolution()
        }
    }
}

subprojects {
    group = "no.nav.tilleggsstonader-libs"

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")
    apply(plugin = "java-library")

    kotlin {
        jvmToolchain(javaVersion.asInt())
    }

    dependencies {
        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.1.3"))

        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.assertj:assertj-core")
        testImplementation("io.mockk:mockk:1.13.7")

        testImplementation("ch.qos.logback:logback-core")
        testImplementation("ch.qos.logback:logback-classic")
    }

    tasks.jar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    tasks.test {
        useJUnitPlatform()
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = project.name
                version = project.findProperty("version")?.toString() ?: "1.0-SNAPSHOT"
                from(components["java"])
            }
        }

        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/navikt/tilleggsstonader-libs")
                credentials {
                    username = "x-access-token"
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }

    if (project.hasProperty("skipLint")) {
        gradle.startParameter.excludedTaskNames += "spotlessKotlinCheck"
    }

    kotlin.sourceSets["main"].kotlin.srcDirs("main")
    kotlin.sourceSets["test"].kotlin.srcDirs("test")
    sourceSets["main"].resources.srcDirs("main")
    sourceSets["test"].resources.srcDirs("test")
}
