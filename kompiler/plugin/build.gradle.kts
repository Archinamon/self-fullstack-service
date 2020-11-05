import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    maven
}

group = "me.archinamon.tcp.server"
version = "latest"

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        kotlinOptions.includeRuntime = false
    }

    val fatJar by creating(Jar::class) {
        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Main-Class" to "me.archinamon.tcp.server.plugin.KompilerComponentRegistrar"
            )
        }

        configurations.compile.get().forEach {
            if (it.isDirectory) from(it)
            else from(zipTree(it))
        }
    }

    shadowJar {
        dependsOn(fatJar)
        archiveClassifier.set("")

        minimize {
            exclude("org.jetbrains.kotlin:.*:.*")
        }
    }

    val install by getting {
        dependsOn(shadowJar)
    }

    val build by getting {
        dependsOn(shadowJar)
        finalizedBy(install)
    }
}

dependencies {
    implementation("me.archinamon.tcp.server:graph-jvm:latest")

    implementation("com.squareup:kotlinpoet:1.6.0")
    implementation("de.jensklingenberg:mpapt-runtime:0.8.7")

    compileOnly("org.jetbrains.kotlin:kotlin-compiler")
}
