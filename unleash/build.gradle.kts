plugins {
    kotlin("plugin.spring") version "2.4.10"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    api("io.getunleash:unleash-client-java:12.2.3")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
