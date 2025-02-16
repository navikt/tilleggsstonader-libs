plugins {
    kotlin("plugin.spring") version "2.1.10"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    api("io.getunleash:unleash-client-java:10.0.1")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
