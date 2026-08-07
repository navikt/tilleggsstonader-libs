plugins {
    kotlin("plugin.spring") version "2.3.21"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
