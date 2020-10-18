package me.archinamon.server.tcp.bind

enum class HttpMethod(val value: String) {
    Get("GET"),
    Post("POST"),
    Put("PUT"),
    Patch("PATCH"),
    Delete("DELETE"),
    Head("HEAD"),
    Options("OPTIONS");

    companion object {
        fun parse(value: String) = when (value.toUpperCase()) {
            "GET" -> Get
            "POST" -> Post
            "PUT" -> Put
            "PATCH" -> Patch
            "DELETE" -> Delete
            "HEAD" -> Head
            "OPTIONS" -> Options
            else -> throw IllegalArgumentException("Unknown http method: $value")
        }
    }
}