package me.archinamon.web.json.serialize

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.js.Date
import kotlin.reflect.KClass

interface JsCallSerializer {
    companion object {
        val parser = Json {
            isLenient = true
        }
    }

    @Suppress("ComplexMethod", "TooGenericExceptionCaught", "NestedBlockDepth", "UNCHECKED_CAST")
    fun trySerialize(kClass: KClass<Any>, value: Any): String {
        return if (value is List<*>) {
            if (value.size > 0) {
                when {
                    value.first() is String ->
                        parser.encodeToString(ListSerializer(String.serializer()) as KSerializer<Any>, value)
                    value.first() is Date ->
                        parser.encodeToString(ListSerializer(JsonDateSerializer) as KSerializer<Any>, value)
                    value.first() is Int ->
                        parser.encodeToString(ListSerializer(Int.serializer()) as KSerializer<Any>, value)
                    value.first() is Long ->
                        parser.encodeToString(ListSerializer(Long.serializer()) as KSerializer<Any>, value)
                    value.first() is Boolean ->
                        parser.encodeToString(ListSerializer(Boolean.serializer()) as KSerializer<Any>, value)
                    value.first() is Float ->
                        parser.encodeToString(ListSerializer(Float.serializer()) as KSerializer<Any>, value)
                    value.first() is Double ->
                        parser.encodeToString(ListSerializer(Double.serializer()) as KSerializer<Any>, value)
                    value.first() is Char ->
                        parser.encodeToString(ListSerializer(Char.serializer()) as KSerializer<Any>, value)
                    value.first() is Short ->
                        parser.encodeToString(ListSerializer(Short.serializer()) as KSerializer<Any>, value)
                    value.first() is Byte ->
                        parser.encodeToString(ListSerializer(Byte.serializer()) as KSerializer<Any>, value)
                    value.first() is Enum<*> -> "[" + value.joinToString(",") { "\"$it\"" } + "]"
                    else -> try {
                        parser.encodeToString(ListSerializer(kClass.serializer()) as KSerializer<Any>, value)
                    } catch (e: Throwable) {
                        try {
                            parser.encodeToString(ListSerializer(value.first()!!::class.serializer()) as KSerializer<Any>, value)
                        } catch (e: Throwable) {
                            try {
                                parser.encodeToString(ListSerializer(String.serializer()) as KSerializer<Any>, value)
                            } catch (e: Throwable) {
                                value.toString()
                            }
                        }
                    }
                }
            } else {
                "[]"
            }
        } else {
            when (value) {
                is Enum<*> -> "\"$value\""
                is String -> value
                is Char -> "\"$value\""
                is Date -> "\"${value.toStringInternal()}\""
                else -> try {
                    @Suppress("UNCHECKED_CAST")
                    parser.encodeToString(kClass.serializer(), value)
                } catch (e: Throwable) {
                    value.toString()
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST", "ComplexMethod")
    fun <RET> deserialize(value: String, jsType: String): RET {
        return when (jsType) {
            "String" -> parser.decodeFromString(String.serializer(), value) as RET
            "Number" -> parser.decodeFromString(Double.serializer(), value) as RET
            "Long" -> parser.decodeFromString(Long.serializer(), value) as RET
            "Boolean" -> parser.decodeFromString(Boolean.serializer(), value) as RET
            "BoxedChar" -> parser.decodeFromString(Char.serializer(), value) as RET
            "Short" -> parser.decodeFromString(Short.serializer(), value) as RET
            "Date" -> parser.decodeFromString(JsonDateSerializer, value) as RET
            "Byte" -> parser.decodeFromString(Byte.serializer(), value) as RET
            else -> throw NotStandardTypeException(jsType)
        }
    }

    @Suppress("UNCHECKED_CAST", "ComplexMethod")
    fun <RET> deserializeList(value: String, jsType: String): List<RET> {
        return when (jsType) {
            "String" -> parser.decodeFromString(ListSerializer(String.serializer()), value) as List<RET>
            "Number" -> parser.decodeFromString(ListSerializer(Double.serializer()), value) as List<RET>
            "Long" -> parser.decodeFromString(ListSerializer(Long.serializer()), value) as List<RET>
            "Boolean" -> parser.decodeFromString(ListSerializer(Boolean.serializer()), value) as List<RET>
            "BoxedChar" -> parser.decodeFromString(ListSerializer(Char.serializer()), value) as List<RET>
            "Short" -> parser.decodeFromString(ListSerializer(Short.serializer()), value) as List<RET>
            "Date" -> parser.decodeFromString(ListSerializer(JsonDateSerializer), value) as List<RET>
            "Byte" -> parser.decodeFromString(ListSerializer(Byte.serializer()), value) as List<RET>
            else -> throw NotStandardTypeException(jsType)
        }
    }

    @Suppress("TooGenericExceptionCaught", "ThrowsCount")
    fun tryDeserializeEnum(kClass: KClass<Any>, value: String): Any {
        return try {
            if (kClass.asDynamic().jClass.`$metadata$`.interfaces.first().name == "Enum") {
                findEnumValue(kClass, parser.decodeFromString(String.serializer(), value)) ?: throw NotEnumTypeException()
            } else {
                throw NotEnumTypeException()
            }
        } catch (e: Throwable) {
            throw NotEnumTypeException()
        }
    }

    @Suppress("TooGenericExceptionCaught", "ThrowsCount")
    fun tryDeserializeEnumList(kClass: KClass<Any>, value: String): List<Any> {
        return try {
            if (kClass.asDynamic().jClass.`$metadata$`.interfaces.first().name == "Enum") {
                parser.decodeFromString(ListSerializer(String.serializer()), value).map {
                    findEnumValue(kClass, parser.decodeFromString(String.serializer(), it))
                        ?: throw NotEnumTypeException()
                }
            } else {
                throw NotEnumTypeException()
            }
        } catch (e: Throwable) {
            throw NotEnumTypeException()
        }
    }

    fun findEnumValue(kClass: KClass<Any>, value: String): Any? {
        return (kClass.asDynamic().jClass.values() as Array<Any>).find {
            it.asDynamic().name == value
        }
    }
}