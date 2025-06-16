package com.nyinyi.quickfeed.ui.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun toReadableTimestamp(epochSeconds: Long): String {
    val now = Clock.System.now()
    val instant = Instant.fromEpochSeconds(epochSeconds)
    val duration = now - instant

    return when {
        duration < 1.minutes -> "now"
        duration < 1.hours -> "${duration.inWholeMinutes}m"
        duration < 24.hours -> "${duration.inWholeHours}h"
        duration < 365.days -> {
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            // Formats to "Month Day", e.g., "Jun 16"
            "${
                dateTime.month.name.substring(0, 3).lowercase().replaceFirstChar { it.titlecase() }
            } ${dateTime.dayOfMonth}"
        }

        else -> {
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            // Formats to "Month Day, Year", e.g., "Jun 16, 2024"
            "${
                dateTime.month.name.substring(0, 3).lowercase().replaceFirstChar { it.titlecase() }
            } ${dateTime.dayOfMonth}, ${dateTime.year}"
        }
    }
}
