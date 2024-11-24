plugins {
    kotlin("plugin.spring") version "2.0.21"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    api("io.getunleash:unleash-client-java:9.2.5")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
