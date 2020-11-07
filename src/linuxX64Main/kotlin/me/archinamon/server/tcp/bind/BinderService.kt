package me.archinamon.server.tcp.bind

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@AssociatedObjectKey
actual annotation class NativeObjectBinder(actual val value: KClass<out BinderService>)

actual fun <R : Any?> syncLocally(lock: Any, block: () -> R) = synchronized(NativeLock, block)

actual inline fun <reified T : Annotation> KClass<*>.findObject(): Any = this.findAssociatedObject<T>()
    .takeIf { it != null }
    ?: throw NoSuchElementException(INSTANCE_ERROR)

internal object NativeLock : SynchronizedObject()
