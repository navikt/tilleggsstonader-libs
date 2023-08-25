import org.jlleitschuh.gradle.ktlint.KtlintExtension

val javaVersion = JavaLanguageVersion.of(17)

plugins {
    kotlin("jvm") version "1.9.10"
    `maven-publish`
    `java-library`
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1"
}

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<KtlintExtension> {
        version.set("0.50.0")
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
                version = project.findProperty("version")?.toString() ?: "0.0.0"
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

    kotlin.sourceSets["main"].kotlin.srcDirs("main")
    kotlin.sourceSets["test"].kotlin.srcDirs("test")
    sourceSets["main"].resources.srcDirs("main")
    sourceSets["test"].resources.srcDirs("test")
}
