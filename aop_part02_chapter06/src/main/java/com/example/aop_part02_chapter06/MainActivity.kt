package com.example.aop_part02_chapter06

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }
    private var currentCountDownTimer: CountDownTimer? = null

    private val soundPool = SoundPool.Builder().build()
    private var tickingSoundId : Int? = null
    private var bellSoundId : Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()

        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoPause()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun initSounds() {
        tickingSoundId = soundPool.load(this,R.raw.timer_ticking,1)
        bellSoundId = soundPool.load(this,R.raw.timer_bell,1)
    }



    //- 각각의 뷰의 리스너와 실제로직을 연결하는 코드들
    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        updateRemainTime(progress * 60 * 1000L)
                    }
                }

                //- 내가 다시 타이머를 셋업하기위해서는 기존 타이머를 멈춰줘야한다.
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                    currentCountDownTimer?.cancel()
//                    currentCountDownTimer = null
                    // 위의 코드를 추상화진행
                    stopCountDown()
                }

                //- 시크바에서 손을뗄때
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return  //-시크바가 null일 경우 return ?:(elvis..)

                    if(seekBar.progress==0){
                        stopCountDown()
                    }
                    else {
                        startCountDown()
                    }
                }
            }

        )
    }

    //-카운트다운 타이머 생성함수 인자 몇Millisencds이
    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)

            }

            override fun onFinish() {
                completeCountDown()
            }

        }
    private fun startCountDown(){
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        // loop: -1 -> loop forever
        tickingSoundId?.let{ soundId->
            soundPool.play(soundId,1F,1F,0,-1, 1F)
        }
    }
    private fun stopCountDown(){
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun completeCountDown(){
        updateRemainTime(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let{soundId->
            soundPool.play(soundId,1F,1F,0,0,1F)
        }
    }

    private fun updateRemainTime(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000
        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
}
