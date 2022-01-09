package com.example.aop_part02_chapter03

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity : AppCompatActivity() {

    private val diaryEditText: EditText by lazy {
        findViewById<EditText>(R.id.diaryEditText)
    }

    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val detailPreference = getSharedPreferences("diary", Context.MODE_PRIVATE)

        diaryEditText.setText(detailPreference.getString("detail", ""))

        //스레드 기능을 이용해서 잠시 입력이 멈췄을대 sharedPreference에 저장
        val runnable = Runnable {
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit {
                //-백그라운드에서 글을 저장하는 기능이기 때문에 UI 수정은 안하고 commit은 false , apply를 사용
                putString("detail", diaryEditText.text.toString())
                Log.d("DiaryActivity", "Save!!!! :: ${diaryEditText.text.toString()}")

            }

        }
        diaryEditText.addTextChangedListener {
            Log.d("DiaryActivity", "TextChanged :: $it")
            handler.removeCallbacks(runnable) //- 아직 실행되지않고 팬딩된 runnalbe 스레드가 있다면 삭제한다.
            handler.postAtTime(runnable, 500)

        }
    }
}