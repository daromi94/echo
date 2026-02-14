plugins {
    alias(libs.plugins.kotlin.jvm)

    application
}

group = "com.daromi"

kotlin {
    jvmToolchain(24)
}

repositories {
    mavenCentral()
}

dependencies {}

application {
    mainClass = "com.daromi.echo.MainKt"
}
