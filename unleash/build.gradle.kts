plugins {
    kotlin("plugin.spring") version "1.9.22"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    api("io.getunleash:unleash-client-java:9.2.0")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
