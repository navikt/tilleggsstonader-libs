plugins {
    kotlin("plugin.spring") version "2.2.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    api("io.getunleash:unleash-client-java:11.0.2")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
