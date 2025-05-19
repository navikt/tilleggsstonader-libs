plugins {
    kotlin("plugin.spring") version "2.1.21"
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework:spring-web")
    implementation("org.slf4j:slf4j-api")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
