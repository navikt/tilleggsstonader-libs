rootProject.name = "tilleggsstonader-libs"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

include("http-client")
include("log")
include("kafka")
include("sikkerhet")
include("util")
include("unleash")
include("spring")
include("test-util")
