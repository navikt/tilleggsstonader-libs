plugins {
    kotlin("plugin.spring") version "2.3.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    api("io.getunleash:unleash-client-java:12.0.1")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
