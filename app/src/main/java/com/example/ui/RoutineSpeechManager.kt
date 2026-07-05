package com.example.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class RoutineSpeechManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("RoutineSpeechManager", "Default language not supported or missing. Falling back to US English.")
                tts?.setLanguage(Locale.US)
            }
            isInitialized = true
        } else {
            Log.e("RoutineSpeechManager", "TTS initialization failed")
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RoutineTTSID")
        } else {
            Log.e("RoutineSpeechManager", "TTS not initialized yet")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
