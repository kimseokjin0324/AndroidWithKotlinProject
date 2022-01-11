package com.example.aop_part02_chapter05

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity : AppCompatActivity(){

    private val photoList= mutableListOf<Uri>()

    //- 이미지가 몇번 돌았는지
    private var currentPosition = 0

    private var timer : Timer? = null

    private val photoImageView : ImageView by lazy{
        findViewById<ImageView>(R.id.photoImageView)
    }

    private val backgroundPhotoImageView : ImageView by lazy{
        findViewById<ImageView>(R.id.backgroundPhotoImageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoframe)
        Log.d("PhotoFrame","onCreate")
        getPhotoUriFromIntent()                     //- 추상화 진행
//        startTimer()   //- onCreate()에서는 최초 한번만 실행이 되기떄문에 onStop->onStart시 타이머를 실행될수 없으므로 onStart시로 옮겨줌
    }

    private fun getPhotoUriFromIntent(){
        val size = intent.getIntExtra("photoListSize",0)
        for (i in 0..size){
            intent.getStringExtra("photo$i")?.let {
                photoList.add(Uri.parse(it))
            }
        }
    }

    private fun startTimer(){
        timer = timer(period = 5*1000){
            runOnUiThread {
                Log.d("PhotoFrame","5초 지나감")

                val current= currentPosition
                val next = if (photoList.size <= currentPosition + 1) 0 else currentPosition +1

                backgroundPhotoImageView.setImageURI(photoList[current])
                photoImageView.alpha = 0f
                photoImageView.setImageURI(photoList[next])
                photoImageView.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next

            }
        }
    }

    // 액티비티가 백그라운드 로가서 중단될때
    override fun onStop() {
        super.onStop()
        Log.d("PhotoFrame","onStop!! timer cancle")
        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()
        Log.d("PhotoFrame","onStart!! timer start")
        startTimer()        //- 새로운타이머 생성
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PhotoFrame","onDestory!! timer cancle")


        timer?.cancel()
    }


}