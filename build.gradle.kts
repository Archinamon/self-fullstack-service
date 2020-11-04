import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

evaluationDependsOn(":kompiler")

buildscript {
    val kotlinVersion = System.getProperty("kotlin.version")

    repositories {
        jcenter()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
    }

    dependencies {
        classpath(kotlin("serialization", kotlinVersion))
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

plugins {
    val kotlinVersion = System.getProperty("kotlin.version")

    kotlin("multiplatform") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("me.archinamon.tcp.build-plugin") apply true
}

group = "me.archinamon"
version = "1.0-SNAPSHOT"

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
        val kotlinVersion = System.getProperty("kotlin.version")

        val commonMain by getting {
            kotlin.srcDir("build/generated-src/kompiler-plugin/common")

            dependencies {
                implementation(project(":kompiler:graph"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0-M1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
            }
        }

        val jsMain by getting {
            kotlin.srcDir("build/generated-src/kompiler-plugin/js")

            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
                implementation("org.jetbrains:kotlin-react:16.13.1-pre.110-kotlin-$kotlinVersion")
                implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.110-kotlin-$kotlinVersion")
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.110-kotlin-$kotlinVersion")
            }
        }

        val linuxX64Main by getting {
            kotlin.srcDir("build/generated-src/kompiler-plugin/native")

            dependencies {
                implementation("me.archinamon:file-io-linuxx64:1.0")
            }
        }

        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }

    targets.flatMap(KotlinTarget::compilations).forEach { compilation ->
        compilation.kotlinOptions {
            freeCompilerArgs = listOf(
                "-XXLanguage:+InlineClasses",
                "-Xuse-experimental=kotlin.Experimental",
                "-Xopt-in=kotlin.reflect.ExperimentalAssociatedObjects"
            )
        }
    }
}

tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack") {
    outputFileName = "output.js"
}
