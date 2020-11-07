package me.archinamon.server.tcp.bind

import kotlin.native.concurrent.ThreadLocal
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

expect annotation class NativeObjectBinder(val value: KClass<out BinderService>)

expect fun <R : Any?> syncLocally(lock: Any, block: () -> R): R
expect inline fun <reified T : Annotation> KClass<*>.findObject(): Any

val INSTANCE_ERROR = "Service should have no-arg constructor annotated with ${NativeObjectBinder::class.simpleName} inheritor"
internal const val NEW_INSTANCE_ERROR = "Cannot set new instance — use available instance!"

abstract class BinderService {

    @ThreadLocal
    companion object Impl {
        protected var serviceInstances = mutableMapOf<String, BinderService?>()

        private fun unsafeGet(serviceName: String) = serviceInstances[serviceName]

        private fun unsafeSet(name: String, s: BinderService) {
            if (serviceInstances[name] != null) {
                throw IllegalStateException(NEW_INSTANCE_ERROR)
            }

            serviceInstances[name] = s
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : BinderService> instance(serviceClass: KClass<T>): T {
            val serviceName = serviceClass.qualifiedName ?: serviceClass.toString()

            if (unsafeGet(serviceName) != null) {
                return unsafeGet(serviceName) as T
            }

            return syncLocally(serviceClass) {
                if (unsafeGet(serviceName) != null) {
                    return@syncLocally unsafeGet(serviceName) as T
                }

                val service = serviceClass.findObject<NativeObjectBinder>()

                return@syncLocally (service as T).also {
                    unsafeSet(serviceName, service)
                }
            }
        }

        fun <T : BinderService> provideService(klass: KClass<out T>) = instance(klass)

        inline fun <reified T : BinderService> provideService() = instance(T::class)

        inline fun <reified T : BinderService> singleton() = ReadOnlyProperty<Any?, T> { _, _ ->
            instance(T::class)
        }
    }
}