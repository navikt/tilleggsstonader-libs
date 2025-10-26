val tilleggsstønaderKontrakterVersion = "2025.10.17-12.11.c565284fc91f"

dependencies {
    implementation("org.assertj:assertj-core")
    implementation("org.springframework:spring-web")
    implementation(project(":http-client"))

    implementation("no.nav.tilleggsstonader.kontrakter:kontrakter-felles:$tilleggsstønaderKontrakterVersion")

    testImplementation(project(":util"))
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}
