package com.example.redbutton

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var sirenPlayer: MediaPlayer? = null
    private var countDownTimer: CountDownTimer? = null
    private var isRed = false
    private var isEvacuationBlinking = false

    // Объявляем переменные для View элементов
    private lateinit var titleText: TextView
    private lateinit var redButton: Button
    private lateinit var timerText: TextView
    private lateinit var evacuationText: TextView
    private lateinit var falseAlarmText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализируем View элементы
        initializeViews()

        setupButton()
        startTitleBlinking()
    }

    private fun initializeViews() {
        titleText = findViewById(R.id.titleText)
        redButton = findViewById(R.id.redButton)
        timerText = findViewById(R.id.timerText)
        evacuationText = findViewById(R.id.evacuationText)
        falseAlarmText = findViewById(R.id.falseAlarmText)
    }

    private fun setupButton() {
        // Делаем кнопку круглой и красной
        redButton.setBackgroundResource(R.drawable.red_circle_button)

        redButton.setOnClickListener {
            startRocketSequence()
        }
    }

    private fun startTitleBlinking() {
        // Анимация мигания заголовка (желтый-красный)
        object : CountDownTimer(1000000, 500) {
            override fun onTick(millisUntilFinished: Long) {
                isRed = !isRed
                titleText.setTextColor(
                    if (isRed) ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark)
                    else ContextCompat.getColor(this@MainActivity, android.R.color.holo_orange_dark)
                )
            }
            override fun onFinish() {}
        }.start()
    }

    private fun startRocketSequence() {
        // 1. Прячем заголовок
        titleText.visibility = View.GONE

        // 2. Проигрываем звук взлета ракеты
        playRocketSound()

        // 3. Запускаем таймер
        startCountdownTimer()
    }

    private fun playRocketSound() {
        // Для теста используем системный звук вместо своего файла
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.rocket_launch)
            // Если у вас есть файл rocket_launch.mp3 в res/raw/ - используйте:
            // mediaPlayer = MediaPlayer.create(this, R.raw.rocket_launch)

            // Временное решение - используем системный звук

            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playSirenSound() {
        try {
            sirenPlayer = MediaPlayer.create(this, R.raw.siren)
            // Если у вас есть файл siren.mp3 в res/raw/ - используйте:
            //

            // Временное решение - используем системный звук
            sirenPlayer?.isLooping = true
            sirenPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startCountdownTimer() {
        timerText.visibility = View.VISIBLE
        evacuationText.visibility = View.VISIBLE

        // Запускаем сирену
        playSirenSound()

        // Запускаем мигание "ЭВАКУИРУЙТЕСЬ!"
        startEvacuationBlinking()

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                timerText.text = seconds.toString()
            }

            override fun onFinish() {
                timerText.text = "0"
                endSequence()
            }
        }.start()
    }

    private fun startEvacuationBlinking() {
        isEvacuationBlinking = true
        object : CountDownTimer(60000, 500) {
            override fun onTick(millisUntilFinished: Long) {
                if (isEvacuationBlinking) {
                    evacuationText.visibility =
                        if (evacuationText.visibility == View.VISIBLE) View.INVISIBLE
                        else View.VISIBLE
                }
            }
            override fun onFinish() {}
        }.start()
    }

    private fun endSequence() {
        // Останавливаем сирену и мигание
        isEvacuationBlinking = false
        sirenPlayer?.stop()

        // Прячем таймер и текст эвакуации
        timerText.visibility = View.GONE
        evacuationText.visibility = View.GONE

        // Показываем "ЛОЖНАЯ ТРЕВОГА" с миганием
        showFalseAlarm()
    }

    private fun showFalseAlarm() {
        falseAlarmText.visibility = View.VISIBLE

        // Одно мигание
        val blinkAnimation = AlphaAnimation(1.0f, 0.0f)
        blinkAnimation.duration = 500
        blinkAnimation.repeatCount = 1
        blinkAnimation.repeatMode = AlphaAnimation.REVERSE

        falseAlarmText.startAnimation(blinkAnimation)

        // Через 3 секунды перезапускаем систему
        falseAlarmText.postDelayed({
            resetSystem()
        }, 3000)
    }

    private fun resetSystem() {
        // Останавливаем все таймеры и плееры
        countDownTimer?.cancel()
        mediaPlayer?.release()
        sirenPlayer?.release()

        // Возвращаем в исходное состояние
        falseAlarmText.visibility = View.GONE
        titleText.visibility = View.VISIBLE
        redButton.isEnabled = true

        // Перезапускаем мигание заголовка
        startTitleBlinking()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        mediaPlayer?.release()
        sirenPlayer?.release()
    }
}