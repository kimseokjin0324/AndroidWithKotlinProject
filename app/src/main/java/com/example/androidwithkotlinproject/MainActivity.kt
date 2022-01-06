package com.example.androidwithkotlinproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val heightEditText: EditText = findViewById(R.id.edtHeight)         //명시적선언
        val weightEditText = findViewById<EditText>(R.id.edtWeight)           //추론적선언
        val btnResult: Button = findViewById(R.id.btnCheckbtn)

        btnResult.setOnClickListener {//-람다형식
            Log.d("MainActivity", "btnResult버튼이 클릭됨")
            //빈값일 경우 예외 처리
            if(heightEditText.text.isEmpty()||weightEditText.text.isEmpty()){
                Toast.makeText(this,"빈 값이 있습니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener //이렇게 @setOnClickListener를 통해서 여기 리스너를 탈출한다라는것을 알려준다.
            }

            // 이 아래로는 절대 빈값이 절대 될수 없음

            val height : Int =heightEditText.text.toString().toInt()
            val weight : Int =weightEditText.text.toString().toInt()

            val intent = Intent(this, ResultActivity::class.java)
            //인텐트에 weight 와 height를 담아서 넘어준다.
            intent.putExtra("height",height)
            intent.putExtra("weight",weight)
            startActivity(intent)

        }

    }
}