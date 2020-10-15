import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform") version "1.4.10"
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

                val serverPort = if (extra.has("cgi.server.runningPort"))
                    extra["cgi.server.runningPort"]
                else 8080

                runTask?.args(serverPort, true)
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
//                implementation("me.archinamon:file-io:1.0")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
                implementation("org.jetbrains:kotlin-react:16.13.1-pre.110-kotlin-1.4.10")
                implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.110-kotlin-1.4.10")
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.110-kotlin-1.4.10")
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
