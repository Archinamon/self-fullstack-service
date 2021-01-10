import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = "me.archinamon.tcp.server"
version = "latest"

kotlin {
    js { nodejs() }
    jvm()
    linuxX64()
    macosX64()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        kotlinOptions.includeRuntime = false
    }

    val publishToMavenLocal by getting
    val build by getting {
        finalizedBy(publishToMavenLocal)
    }
}
