package me.archinamon.server.tcp.bind

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

actual abstract class BinderService {
    actual companion object Impl {
        actual fun <T : BinderService> provideService(klass: KClass<out T>): T {
            TODO("Not yet implemented")
        }

        actual inline fun <reified T : BinderService> provideService(): T {
            TODO("Not yet implemented")
        }

        actual inline fun <reified T : BinderService> singleton(): ReadOnlyProperty<Any?, T> {
            TODO("Not yet implemented")
        }
    }
}