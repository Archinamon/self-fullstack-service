package me.archinamon.server.tcp.bind

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.native.concurrent.ThreadLocal
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@AssociatedObjectKey
annotation class NativeObjectBinder(val value: KClass<out BinderService>)

val INSTANCE_ERROR = "Service should have no-arg constructor annotated with ${NativeObjectBinder::class.simpleName} inheritor"
private const val NEW_INSTANCE_ERROR = "Cannot set new instance — use available instance!"

actual abstract class BinderService {

    @ThreadLocal
    actual companion object Impl {
        protected var serviceInstances = mutableMapOf<String, BinderService?>()
        private val lock = SynchronizedObject()

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

            synchronized(lock) {
                if (unsafeGet(serviceName) != null) {
                    return unsafeGet(serviceName) as T
                }

                val service = serviceClass.findAssociatedObject<NativeObjectBinder>() as? T
                    ?: throw NoSuchElementException(INSTANCE_ERROR)

                return (service as T).also {
                    unsafeSet(serviceName, service)
                }
            }
        }

        actual fun <T : BinderService> provideService(klass: KClass<out T>) = instance(klass)

        actual inline fun <reified T : BinderService> provideService() = instance(T::class)

        actual inline fun <reified T : BinderService> singleton() = ReadOnlyProperty<Any?, T> { _, _ ->
            instance(T::class)
        }
    }
}