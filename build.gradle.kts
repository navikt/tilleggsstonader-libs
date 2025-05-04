val javaVersion = JavaLanguageVersion.of(21)

plugins {
    kotlin("jvm") version "2.1.20"
    `maven-publish`
    `java-library`
    id("com.diffplug.spotless") version "7.0.3"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.18"
    id("org.cyclonedx.bom") version "2.3.0"
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
            ktlint("1.5.0")
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
        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.5"))

        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.assertj:assertj-core")
        testImplementation("io.mockk:mockk:1.14.2")

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

tasks.cyclonedxBom {
    setIncludeConfigs(listOf("runtimeClasspath", "compileClasspath"))
}
