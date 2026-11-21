package com.example.dlsbot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

/**
 * ধাপ ১: শুধু টাচ পাঠানোর ক্ষমতা।
 * পরের ধাপে এখানে স্ক্রিন বিশ্লেষণ ও সিদ্ধান্ত যোগ হবে।
 */
class BotService : AccessibilityService() {

    companion object {
        @Volatile
        var instance: BotService? = null
            private set
    }

    /** একটি স্ট্রোক: (x1,y1) থেকে (x2,y2), শুরু ও সময়কাল মিলিসেকেন্ডে */
    data class Stroke(
        val x1: Float, val y1: Float,
        val x2: Float, val y2: Float,
        val startMs: Long = 0L,
        val durationMs: Long = 50L
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    fun tap(x: Float, y: Float) {
        perform(listOf(Stroke(x, y, x, y, 0L, 50L)))
    }

    /**
     * একাধিক স্ট্রোক একসাথে পাঠায়, তাই জয়স্টিক ধরে রেখে বাটন চাপা সম্ভব
     * (যেমন একটা স্ট্রোক জয়স্টিকে ৫০০ms, আরেকটা বাটনে ট্যাপ)।
     */
    fun perform(strokes: List<Stroke>) {
        val builder = GestureDescription.Builder()
        for (s in strokes) {
            val path = Path().apply {
                moveTo(s.x1, s.y1)
                if (s.x1 != s.x2 || s.y1 != s.y2) lineTo(s.x2, s.y2)
            }
            builder.addStroke(GestureDescription.StrokeDescription(path, s.startMs, s.durationMs))
        }
        dispatchGesture(builder.build(), null, null)
    }
}
