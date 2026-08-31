package com.example.myalarmapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import java.util.*

class AlarmService : Service(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var timer: Timer? = null

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val taskName = intent?.getStringExtra("TASK_NAME") ?: "تنبيه مهم"

        // تكرار نطق اسم المهمة كل 5 ثوانٍ في الخلفية
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                tts.speak(taskName, TextToSpeech.QUEUE_ADD, null, null)
            }
        }, 0, 5000)

        return START_STICKY
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("ar")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        timer?.cancel()
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}
