package com.vtl.javicalendar.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.LocalDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())
}

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Color) = encoder.encodeLong(value.toArgb().toLong())
    override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeLong().toInt())
}

/**
 * Presentation model for displaying a single date with all formatted strings and colors
 */
@Serializable
data class DateInfo(
    // Original date value for handling clicks and logic
    @Serializable(with = LocalDateSerializer::class)
    val value: LocalDate,
    
    // Gregorian date display
    val year: DisplayField,
    val month: DisplayField,
    val day: DisplayField,
    val weekday: DisplayField,
    val fullDisplayDay: DisplayField?,
    
    // Japanese date
    val japaneseDate: JapaneseDateDisplay,
    
    // Lunar date
    val lunarDate: LunarDateDisplay
)

@Serializable
data class DisplayField(
    val value: String,
    @Serializable(with = ColorSerializer::class)
    val color: Color,
    @Serializable(with = ColorSerializer::class)
    val backgroundColor: Color? = null
)

@Serializable
data class JapaneseDateDisplay(
    val year: DisplayField?,
    val holiday: DisplayField?
)

@Serializable
data class LunarDateDisplay(
    val year: DisplayField?,
    val month: DisplayField?,
    val day: DisplayField?,
    val fullDisplayDay: DisplayField?,
    val observance: DisplayField?,
    val lucDieu: DisplayField?,
    val lucDieuFullDisplay: DisplayField?,
    val auspiciousHours: DisplayField?
)
