dependencies {
    testImplementation(project(":util"))
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
