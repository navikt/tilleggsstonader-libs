val tilleggsstønaderKontrakterVersion = "2025.06.10-12.56.ea582af44a70"

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
