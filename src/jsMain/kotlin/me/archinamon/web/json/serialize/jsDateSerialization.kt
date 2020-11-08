package me.archinamon.web.json.serialize

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.Date
import kotlin.math.absoluteValue

internal object JsonDateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("kotlin.js.Date")

    override fun deserialize(decoder: Decoder): Date {
        return decoder.decodeString().toDateInternal()
    }

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(value.toStringInternal())
    }
}

@Suppress("ComplexMethod", "MagicNumber")
internal fun String.toDateInternal(): Date {
    val dt = this.split(':', 'T', '-', '+')
    val utcCheck = this[length - 1] == 'Z'
    val ds = if (utcCheck) dt[5].dropLast(1).split(".") else dt[5].split(".")
    val tzCheck = this[length - 6]
    return if (!utcCheck && tzCheck != '-' && tzCheck != '+') {
        Date(
            dt[0].toInt(),
            dt[1].toInt() - 1,
            dt[2].toInt(),
            dt[3].toInt(),
            dt[4].toInt(),
            ds[0].toInt(),
            if (ds.size == 2) ds[1].toInt() else 0
        )
    } else {
        val sign = if (utcCheck || tzCheck == '+') 1 else -1
        Date(
            Date.UTC(
                dt[0].toInt(),
                dt[1].toInt() - 1,
                dt[2].toInt(),
                if (utcCheck) {
                    dt[3].toInt()
                } else {
                    dt[3].toInt() - sign * dt[6].toInt()
                },
                dt[4].toInt(),
                ds[0].toInt(),
                if (ds.size == 2) ds[1].toInt() else 0
            )
        )
    }
}

internal fun Date.toStringInternal(): String {
    val tz = this.getTimezoneOffset() / 60
    val sign = if (tz > 0) "-" else "+"
    return "" + this.getFullYear() + "-" + ("0" + (this.getMonth() + 1)).takeLast(2) + "-" +
        ("0" + this.getDate()).takeLast(2) + "T" + ("0" + this.getHours()).takeLast(2) + ":" +
        ("0" + this.getMinutes()).takeLast(2) + ":" + ("0" + this.getSeconds()).takeLast(2) + "." +
        this.getMilliseconds() + sign + ("0${tz.absoluteValue}").takeLast(2) + ":00"
}
