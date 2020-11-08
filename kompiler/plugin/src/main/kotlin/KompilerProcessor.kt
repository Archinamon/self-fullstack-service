package me.archinamon.tcp.server.plugin

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import de.jensklingenberg.mpapt.common.methods
import de.jensklingenberg.mpapt.model.AbstractProcessor
import de.jensklingenberg.mpapt.model.Element
import de.jensklingenberg.mpapt.model.RoundEnvironment
import de.jensklingenberg.mpapt.utils.KotlinPlatformValues
import me.archinamon.tcp.server.graph.TcpService
import org.jetbrains.kotlin.backend.common.descriptors.isSuspend
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import java.io.File

class KompilerProcessor(private val generatedRootDir: File) : AbstractProcessor() {

    private companion object {
        const val PACKAGE = "me.archinamon.server.tcp.service"

        val binderServiceType = ClassName.bestGuess("me.archinamon.server.tcp.bind.BinderService")
        val routeCmdType = ClassName.bestGuess("me.archinamon.server.tcp.bind.RouteCommand")
        val socketBinderType = ClassName.bestGuess("me.archinamon.server.tcp.bind.TcpSocketBinder")
    }

    private val serviceClass = TcpService::class.qualifiedName!!

    override fun onProcessingStarted() {
        log("Kompiler: starting processor...")
    }

    override fun process(roundEnvironment: RoundEnvironment) {
        roundEnvironment.getElementsAnnotatedWith(serviceClass)
            .filterIsInstance<Element.ClassElement>()
            .forEach { classElem -> constructService(classElem.classDescriptor) }

        log("Kompiler: RoundTrip end")
    }

    override fun isTargetPlatformSupported(platform: TargetPlatform): Boolean {
        return when (val targetName = platform.first().platformName) {
            KotlinPlatformValues.JS -> false
            KotlinPlatformValues.JVM -> false
            KotlinPlatformValues.NATIVE -> true
            else -> {
                log(targetName)
                false
            }
        }
    }

    override fun getSupportedAnnotationTypes() = setOf(serviceClass)

    private fun constructService(descriptor: ClassDescriptor) {
        val classFqn = descriptor.fqNameSafe.asString()

        val classType = ClassName.bestGuess(classFqn)
        val serviceName = ClassName.bestGuess(classFqn + "Impl")
        val serviceBinder = ClassName.bestGuess(classType.canonicalName + "Binder")

        val defaultImports = listOf(
            binderServiceType, routeCmdType, socketBinderType, classType
        )

        descriptor.renderCommonBindingInfo(defaultImports, classType, serviceName, serviceBinder)
        descriptor.renderFrontendBindingInfo(defaultImports, classType, serviceName, serviceBinder)
    }

    private fun ClassDescriptor.renderCommonBindingInfo(
        defaultImports: List<ClassName>,
        classType: ClassName,
        serviceName: ClassName,
        serviceBinder: ClassName
    ) {
        val expectation = TypeSpec.objectBuilder(serviceName)
            .addModifiers(KModifier.EXPECT)
            .addSuperinterface(binderServiceType)
            .addSuperinterface(classType)
            .build()

        val binder = TypeSpec.objectBuilder(serviceBinder)
            .superclass(socketBinderType.parameterizedBy(serviceName))
            .addSuperclassConstructorParameter("%T::class", serviceName)
            .addInitializerBlock(CodeBlock.builder()
                .apply {
                    methods().forEach { method ->
                        val methodName = method.name
                        addStatement("bind(%T(%S), %T::%L)", routeCmdType, methodName, classType, methodName)
                    }
                }
                .build()
            )
            .build()

        File(generatedRootDir, "common")
            .writeFile(classType.simpleName, defaultImports, expectation, binder)
    }

    private fun ClassDescriptor.renderFrontendBindingInfo(
        defaultImports: List<ClassName>,
        classType: ClassName,
        serviceName: ClassName,
        serviceBinder: ClassName
    ) {
        val jqueryImports = arrayOf(
            ClassName.bestGuess("pl.treksoft.jquery.JQueryXHR"),
            ClassName.bestGuess("pl.treksoft.jquery.JQueryAjaxSettings")
        )

        val binderObjectKey = ClassName.bestGuess("me.archinamon.server.tcp.bind.NativeObjectBinder")
        val binderObjectKeyAnnotation = AnnotationSpec.builder(binderObjectKey)
            .addMember("%T::class", serviceName)
            .build()

        val beforeSendSpec = ClassName.bestGuess("me.archinamon.web.json.serialize.BeforeExecCall")
        val callAgent = ClassName.bestGuess("me.archinamon.server.tcp.bind.TcpCallAgent")
            .parameterizedBy(serviceName)

        val callAgentField = PropertySpec.builder("agent", callAgent)
            .initializer("%T(%T)", callAgent, serviceBinder)
            .build()

        fun CallableMemberDescriptor.renderServiceMethods(): FunSpec {
            val builder = FunSpec.builder(name.identifier)
                .addModifiers(KModifier.OVERRIDE)

            if (isSuspend)
                builder.addModifiers(KModifier.SUSPEND)

            if (valueParameters.isEmpty()) {
                builder.addStatement("return %N.call(%T::%L)", callAgentField, classType, name)
            } else {
                valueParameters.singleOrNull()
                    ?.let { param ->
                        val spec = ParameterSpec.builder(
                            param.name.identifier,
                            param.type.getJetTypeFqName(true)
                                .let(ClassName::bestGuess)
                        )

                        if (param.isVararg) return@let null

                        return@let spec.build()
                    }
                    ?.also { param ->
                        builder.addStatement("return %N.call(%T::%L, %N)", callAgentField, classType, name, param)
                    }
                    ?.let(builder::addParameter)
                    ?: builder.addStatement(
                        "throw %T(%S)",
                        IllegalArgumentException::class,
                        "Multiple arguments not supported! Use data class as single parameter."
                    )
            }

            if (extensionReceiverParameter != null) {
                builder.addStatement(
                    "throw %T(%S)",
                    IllegalArgumentException::class,
                    "Implicit receiver not supported in binding expressions!"
                )
            }

            return builder.build()
        }

        val callbackParam = ParameterSpec("callback", beforeSendSpec)
        val beforeSendCallback = FunSpec.builder("onBeforeCallExecutes")
            .addParameter(callbackParam)
            .addStatement("return %N.onBeforeCallExecutes(%N)", callAgentField, callbackParam)
            .build()

        val actualization = TypeSpec.objectBuilder(serviceName)
            .addModifiers(KModifier.ACTUAL)
            .addAnnotation(binderObjectKeyAnnotation)
            .superclass(binderServiceType)
            .addSuperinterface(classType)
            .addProperty(callAgentField)
            .addFunction(beforeSendCallback)
            .addFunctions(methods().map(CallableMemberDescriptor::renderServiceMethods))
            .build()

        File(generatedRootDir, "js")
            .writeFile(classType.simpleName, defaultImports + jqueryImports, actualization)
    }

    private fun File.writeFile(
        fileName: String,
        fqnImports: List<ClassName>,
        vararg types: TypeSpec
    ) = FileSpec.builder(PACKAGE, fileName)
        .addComment("Generated by kompiler/plugin module!\n\nDo not edit it manually!")
        .indent("    ")
        .apply {
            fqnImports.forEach { className ->
                addImport(className.packageName, className.simpleName)
            }

            types.forEach(this@apply::addType)
        }
        .build()
        .writeTo(this)
}