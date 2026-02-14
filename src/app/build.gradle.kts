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

dependencies {
    implementation("io.netty:netty-all:4.2.10.Final")
}

application {
    mainClass = "com.daromi.echo.MainKt"
}
