plugins {
    kotlin("plugin.spring") version "2.0.20"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework:spring-web")
    implementation("org.slf4j:slf4j-api")
    api("jakarta.servlet:jakarta.servlet-api")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
