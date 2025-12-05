val javaVersion = JavaLanguageVersion.of(21)

plugins {
    kotlin("jvm") version "2.2.21"
    `maven-publish`
    `java-library`
    id("com.diffplug.spotless") version "8.1.0"
    id("com.github.ben-manes.versions") version "0.53.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.19"
}

allprojects {
    repositories {
        mavenCentral()

        maven {
            url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
        }
    }

    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.patrikerdes.use-latest-versions")
    spotless {
        kotlin {
            ktlint("1.7.1")
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
        implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.0"))

        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        testImplementation("org.assertj:assertj-core")
        testImplementation("io.mockk:mockk:1.14.6")

        testImplementation("ch.qos.logback:logback-core")
        testImplementation("ch.qos.logback:logback-classic")
    }

    tasks.jar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    tasks.test {
        useJUnitPlatform()
        failOnNoDiscoveredTests = false
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
