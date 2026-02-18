plugins {
    alias(libs.plugins.kotlin.jvm)

    application
}

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
