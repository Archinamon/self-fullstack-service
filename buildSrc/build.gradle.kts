import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

evaluationDependsOn(":kompiler")

plugins {
    val kotlinVersion = System.getProperties()["kotlin.version"] as String

    kotlin("jvm") version(kotlinVersion)
    id("java-gradle-plugin")
}

buildscript {
    dependencies {
        val kotlinVersion = System.getProperties()["kotlin.version"] as String
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.github.jengelman.gradle.plugins:shadow:5.0.0")
    }
}

allprojects {
    repositories {
        mavenLocal()

        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/ktor")
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
        maven("https://dl.bintray.com/archinamon/maven")
    }
}

gradlePlugin {
    plugins {
        create("annotationProcessor") {
            id = "me.archinamon.tcp.build-plugin"
            implementationClass = "me.archinamon.tcp.gradle.plugin.KompilerPlugin"
        }
    }
}

kotlin.sourceSets["main"].kotlin.srcDirs("src/kotlin")

tasks {
    val installTasks = arrayOf(":kompiler:plugin:install", ":kompiler:graph:publishToMavenLocal")

    withType<KotlinCompile> {
        dependsOn(*installTasks)
    }
}

dependencies {
    val kotlinVersion = System.getProperties()["kotlin.version"] as String

    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation(kotlin("gradle-plugin-api", kotlinVersion))

    implementation("me.archinamon.tcp.server:plugin:latest")
}