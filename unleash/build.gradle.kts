plugins {
    kotlin("plugin.spring") version "2.1.21"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    api("io.getunleash:unleash-client-java:10.2.2")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
