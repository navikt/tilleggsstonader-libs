plugins {
    kotlin("plugin.spring") version "2.3.20"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    api("io.getunleash:unleash-client-java:12.2.0")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
