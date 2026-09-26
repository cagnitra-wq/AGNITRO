package com.example.data.network

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object MarketHoursUtil {

    private val IST_TIMEZONE = TimeZone.getTimeZone("Asia/Kolkata")

    fun getIstCalendar(): Calendar {
        return Calendar.getInstance(IST_TIMEZONE)
    }

    /**
     * Checks if NSE/BSE market is currently in active normal trading session (09:15 to 15:30 IST, Mon-Fri)
     */
    fun isMarketOpen(): Boolean {
        val cal = getIstCalendar()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        // Weekend check: Sunday = 1, Saturday = 7
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false
        }

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val currentMinutes = hour * 60 + minute

        val marketOpenMinutes = 9 * 60 + 15   // 09:15 AM
        val marketCloseMinutes = 15 * 60 + 30 // 03:30 PM

        return currentMinutes in marketOpenMinutes..marketCloseMinutes
    }

    /**
     * Returns formatted IST time, e.g. "11:24:32 AM"
     */
    fun formatIstTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH)
        sdf.timeZone = IST_TIMEZONE
        return sdf.format(Date(timestamp))
    }

    /**
     * Returns trading session date string, e.g. "2026-09-26"
     */
    fun getCurrentSessionDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        sdf.timeZone = IST_TIMEZONE
        return sdf.format(Date())
    }

    /**
     * Formats market status description
     */
    fun getMarketStatusLabel(): String {
        val isOpen = isMarketOpen()
        return if (isOpen) {
            "LIVE MARKET (NSE/BSE)"
        } else {
            "OFF-MARKET (NSE: 09:15 - 15:30 IST)"
        }
    }
}
