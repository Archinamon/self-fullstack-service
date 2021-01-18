import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import shadow.org.apache.tools.zip.ZipOutputStream

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

        val transformer = FileRemoveTransformer(
            "META-INF/versions/.*".toRegex(),
            "META-INF/com.android.tools/.*".toRegex(),
            "META-INF/maven/.*".toRegex()
        )
        transform(transformer)

        dependencyFilter.exclude {
            it.moduleGroup == "org.jetbrains.kotlin"
                    || it.moduleGroup == "org.jetbrains"
                    || it.moduleGroup == "org.intellij"
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
    implementation(project(":kompiler:graph"))
    shadow("me.archinamon.tcp.server:graph-jvm:latest")

    implementation("com.squareup:kotlinpoet:1.7.2")
    implementation("de.jensklingenberg:mpapt-runtime:0.8.7")

    compileOnly("org.jetbrains.kotlin:kotlin-compiler")
}

class FileRemoveTransformer(private vararg val patterns: Regex) : Transformer {
    override fun canTransformResource(element: FileTreeElement): Boolean {
        return patterns.any { pattern ->
            pattern in element.relativePath.pathString
        }
    }

    override fun transform(context: TransformerContext) {}
    override fun hasTransformedResource(): Boolean = true
    override fun modifyOutputStream(jos: ZipOutputStream, preserveFileTimestamps: Boolean) {}
}
