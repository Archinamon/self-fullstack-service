package me.archinamon.server.tcp

enum class TcpProtoMethod(val value: String) {
    Command("CMD"), // may have inputs, no response data, only success true/false
    Retrieve("RET"), // no inputs, always has response data
    Store("STR"); // always should have inputs, may not response datas, but always success true/false

    companion object {
        fun parse(value: String) = when (value.toUpperCase()) {
            "CMD" -> Command
            "RET" -> Retrieve
            "STR" -> Store
            else -> throw IllegalArgumentException("Unknown http method: $value")
        }
    }
}