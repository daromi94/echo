plugins {
    alias(libs.plugins.kotlin.jvm)

    application
}

group = "com.daromi"

repositories {
    mavenCentral()
}

dependencies {}

kotlin {
    jvmToolchain(24)
}

application {
    mainClass = "$group.${rootProject.name}.MainKt"
}
