package com.vtl.holidaycalendar.domain.model

data class LunarDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val isLeap: Boolean,
    val canChi: String,
    val monthCanChi: String,
    val yearCanChi: String,
    val observance: String?,
    val isAuspicious: Boolean,
    val statusLabel: String, // e.g. "Thanh Long"
    val statusPrefix: String, // "Ngày hoàng đạo" or "Ngày hắc đạo"
    val auspiciousHours: String
)
