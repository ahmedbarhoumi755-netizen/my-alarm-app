package com.example.myalarmapp

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var tvClock: TextView
    private var isDarkMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)

        tvClock = findViewById(R.id.tvClockDisplay)
        val btnToggleTheme = findViewById<Button>(R.id.btnToggleTheme)
        val btnSpeakTime = findViewById<Button>(R.id.btnSpeakTime)
        val btnSetAlarm = findViewById<Button>(R.id.btnSetAlarm)
        val etTimeInput = findViewById<EditText>(R.id.etTimeInput)
        val etTaskName = findViewById<EditText>(R.id.etTaskName)

        // تحديث الوقت على الشاشة لحظياً
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    tvClock.text = currentTime
                }
            }
        }, 0, 1000)

        // زر تبديل الوضع الداكن والفاتح
        btnToggleTheme.setOnClickListener {
            isDarkMode = !isDarkMode
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // زر نطق الوقت الحالي عند النقر
        btnSpeakTime.setOnClickListener {
            val currentTime = SimpleDateFormat("HH mm", Locale.getDefault()).format(Date())
            tts.speak("الوقت الحالي هو $currentTime", TextToSpeech.QUEUE_FLUSH, null, null)
        }

        // زر تشغيل المنبه والخدمة الخلفية
        btnSetAlarm.setOnClickListener {
            val alarmTime = etTimeInput.text.toString()
            val taskName = etTaskName.text.toString()

            val intent = Intent(this, AlarmService::class.java).apply {
                putExtra("ALARM_TIME", alarmTime)
                putExtra("TASK_NAME", taskName)
            }
            startForegroundService(intent)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("ar")
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
