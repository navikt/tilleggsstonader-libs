val tilleggsstønaderKontrakterVersion = "2026.01.30-09.17.cce4015d2343-dev"

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

dependencies {
    implementation("no.nav.tilleggsstonader.kontrakter:kontrakter-felles:${tilleggsstønaderKontrakterVersion}")
}