package com.example.aop_part02_chapter03

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private val numberPicker1 : NumberPicker by lazy{
        findViewById<NumberPicker>(R.id.numberPicker1)
            .apply {
                minValue=0
                maxValue=9
            }
    }
    private val numberPicker2 : NumberPicker by lazy{
        findViewById<NumberPicker>(R.id.numberPicker2)
            .apply {
                minValue=0
                maxValue=9
            }
    }
    private val numberPicker3 : NumberPicker by lazy{
        findViewById<NumberPicker>(R.id.numberPicker3)
            .apply {
                minValue=0
                maxValue=9
            }
    }

    private val openButton: AppCompatButton by lazy{
        findViewById<AppCompatButton>(R.id.openButton)
    }
    private val changePasswordButton: AppCompatButton by lazy{
        findViewById<AppCompatButton>(R.id.changePasswordButton)
    }

    private var changePasswordMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // lazy하게 init
        numberPicker1
        numberPicker2
        numberPicker3

        //openButton 동작 정의
        openButton.setOnClickListener {
            //- 비밀번호 처리 중 일때 동작
            if(changePasswordMode){
                Toast.makeText(this,"비밀번호 변경 중입니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)

            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            //SharedPreference에서 불러온 값이 유저로 부터 입력된 패스워드와 비교
            if(passwordPreference.getString("password","000").equals(passwordFromUser)){
                //패스워드 성공
                //Todo 다이어리 페이지 작성 후에 넘겨주어야함
                startActivity(Intent(this,DiaryActivity::class.java))
            }else{
                //실패(에러 팝업(AlertDialog))
                showErrorAlertDialog()

            }
        }
        changePasswordButton.setOnClickListener {
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
            if(changePasswordMode){
                // 번호 저장하는 기능

//                passwordPreference.edit{
//                    val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
//                    putString("password",passwordFromUser)
//
//                    commit()
//                }

                //- 위와 동일한 코드
                passwordPreference.edit(true){
                    putString("password",passwordFromUser)
                }

                changePasswordMode = false
                changePasswordButton.setBackgroundColor(Color.BLACK)

           }else{
               // changePasswordMode 가 활성화 :: 비밀번화가 맞는지 체크
                //SharedPreference에서 불러온 값이 유저로 부터 입력된 패스워드와 비교
                if(passwordPreference.getString("password","000").equals(passwordFromUser)){

                    changePasswordMode = true
                    Toast.makeText(this,"변경할 패스워드를 입력해주세요",Toast.LENGTH_SHORT).show()
                    changePasswordButton.setBackgroundColor(Color.RED)
                }else{
                    //실패(에러 팝업(AlertDialog))
                    showErrorAlertDialog()

                }
            }
        }
    }
    private fun showErrorAlertDialog(){
        AlertDialog.Builder(this)
            .setTitle("실패!!")
            .setMessage("비밀번호가 잘못 되었습니다.")
            .setPositiveButton("확인"){ _,_-> }
            .create()
            .show()
    }
}