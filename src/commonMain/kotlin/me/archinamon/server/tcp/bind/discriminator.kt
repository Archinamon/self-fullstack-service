package me.archinamon.server.tcp.bind

import me.archinamon.tcp.server.graph.BindersDiscriminator

@BindersDiscriminator
expect fun boundServicesProvider(): Set<TcpSocketBinder<out BinderService>>