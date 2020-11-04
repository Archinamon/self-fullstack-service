package me.archinamon.server.tcp.bind

internal typealias CallHandler<reified T, reified R> = suspend CallRouter.(input: T) -> R

internal typealias CommonCallHandler = CallHandler<BindingRequest, BindingResponse<*>>