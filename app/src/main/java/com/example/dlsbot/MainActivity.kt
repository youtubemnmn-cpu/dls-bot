package com.example.dlsbot

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pad = (24 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(pad, pad * 2, pad, pad)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "DLS Bot (ধাপ ১)"
            textSize = 24f
            setTextColor(Color.BLACK)
        }
        status = TextView(this).apply {
            textSize = 16f
            setPadding(0, pad, 0, pad)
            setTextColor(Color.DKGRAY)
        }
        val openSettings = Button(this).apply {
            text = "Accessibility সেটিংস খুলুন"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
        val testTap = Button(this).apply {
            text = "টেস্ট: ৩ সেকেন্ড পর স্ক্রিনের মাঝখানে ট্যাপ"
            setOnClickListener { runTapTest() }
        }

        root.addView(title)
        root.addView(status)
        root.addView(openSettings)
        root.addView(testTap)
        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun refreshStatus() {
        status.text = if (BotService.instance != null) {
            "সার্ভিস: চালু ✅"
        } else {
            "সার্ভিস: বন্ধ ❌\nসেটিংস > Installed apps > DLS Bot চালু করুন"
        }
    }

    private fun runTapTest() {
        val service = BotService.instance
        if (service == null) {
            refreshStatus()
            return
        }
        // হোম স্ক্রিনে গিয়ে দেখুন ট্যাপ কাজ করছে কিনা
        val dm = resources.displayMetrics
        handler.postDelayed({
            service.tap(dm.widthPixels / 2f, dm.heightPixels / 2f)
        }, 3000)
    }
}
