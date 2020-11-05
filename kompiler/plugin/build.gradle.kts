import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    maven
}

group = "me.archinamon.tcp.server"
version = "latest"

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    val install by getting
    val build by getting {
        finalizedBy(install)
    }
}

dependencies {
    implementation("me.archinamon.tcp.server:graph-jvm:latest")

    val kotlinVersion = System.getProperties()["kotlin.version"] as String
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("com.squareup:kotlinpoet:1.6.0")
    implementation("de.jensklingenberg:mpapt-runtime:0.8.6")

    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
}
