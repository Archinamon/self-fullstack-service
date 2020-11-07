package me.archinamon.server.tcp.bind

import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@AssociatedObjectKey
actual annotation class NativeObjectBinder(actual val value: KClass<out BinderService>)

actual fun <R : Any?> syncLocally(lock: Any, block: () -> R): R = synchronized(lock, block)

actual inline fun <reified T : Annotation> KClass<*>.findObject(): Any = this.findAssociatedObject<T>()
    .takeIf { it != null }
    ?: throw NoSuchElementException(INSTANCE_ERROR)
