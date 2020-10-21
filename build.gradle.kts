import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val kotlinVersion = System.getProperty("kotlin.version")

plugins {
    val kotlinVersion = System.getProperty("kotlin.version")

    kotlin("multiplatform") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

group = "me.archinamon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://dl.bintray.com/kotlin/kotlinx")
    maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
    maven("https://dl.bintray.com/archinamon/maven")
}

kotlin {
    linuxX64 {
        binaries {
            executable {
                entryPoint("${project.group}.server.main")
            }
        }
    }

    js {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("me.archinamon:file-io:1.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0-M1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
                implementation("org.jetbrains:kotlin-react:16.13.1-pre.110-kotlin-$kotlinVersion")
                implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.110-kotlin-$kotlinVersion")
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.110-kotlin-$kotlinVersion")
            }
        }

        val linuxX64Main by getting {
            dependencies {
                implementation("me.archinamon:file-io-linuxx64:1.0")
            }
        }
    }
}

tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack") {
    outputFileName = "output.js"
}
