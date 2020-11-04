package me.archinamon.server.tcp.bind

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

expect abstract class BinderService {
    companion object Impl {
        fun <T : BinderService> provideService(klass: KClass<out T>): T
        inline fun <reified T : BinderService> provideService(): T
        inline fun <reified T : BinderService> singleton(): ReadOnlyProperty<Any?, T>
    }
}