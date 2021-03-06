package com.example.aop_part2_chapter4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import com.example.aop_part2_chapter4.model.History
import java.lang.NumberFormatException
import kotlin.math.exp

class MainActivity : AppCompatActivity() {

    private val expressionTextView: TextView by lazy {
        findViewById<TextView>(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }

    private val historyLayout: View by lazy {
        findViewById<View>(R.id.historyLayout)
    }

    private val historyLinearLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.historyLinearLayout)
    }


    lateinit var db: AppDatabase

    private var isOperator = false
    private var hasOperator = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
    }

    //-인자를 view를 받는 함수 구현
    //-버튼 아이디를 통해서 판별
    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.Button0 -> numberButtonClicked("0")
            R.id.Button1 -> numberButtonClicked("1")
            R.id.Button2 -> numberButtonClicked("2")
            R.id.Button3 -> numberButtonClicked("3")
            R.id.Button4 -> numberButtonClicked("4")
            R.id.Button5 -> numberButtonClicked("5")
            R.id.Button6 -> numberButtonClicked("6")
            R.id.Button7 -> numberButtonClicked("7")
            R.id.Button8 -> numberButtonClicked("8")
            R.id.Button9 -> numberButtonClicked("9")

            R.id.ButtonPlus -> operatorButtonClicked("+")
            R.id.ButtonMinus -> operatorButtonClicked("-")
            R.id.ButtonMulti -> operatorButtonClicked("*")
            R.id.ButtonDivider -> operatorButtonClicked("/")
            R.id.ButtonModulo -> operatorButtonClicked("%")

        }
    }

    private fun numberButtonClicked(number: String) {
        //연산자를 치고
        if (isOperator) {
            expressionTextView.append(" ")
        }

        isOperator = false

        val expressionText = expressionTextView.text.split(" ")

        if (expressionText.isNotEmpty() && expressionText.last().length >= 15) {
            Toast.makeText(this, " 15자리 까지만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this, " 0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        expressionTextView.append(number)

        resultTextView.text = calculateExpression()
    }

    private fun operatorButtonClicked(operator: String) {

        //- 연산자를 먼저 눌렀을 경우
        if (expressionTextView.text.isEmpty()) {
            return
        }

        when {
            isOperator -> {
                val text = expressionTextView.text.toString()
                expressionTextView.text =
                    text.dropLast(1) + operator                              //-맨끝 한자리만 지워주게 된다.
            }
            hasOperator -> {
                Toast.makeText(this, " 연산자는 한번만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                expressionTextView.append(" ${operator}")
            }
        }

        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        expressionTextView.text = ssb

        isOperator = true
        hasOperator = true
    }

    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")

        //- region 예외처리
        //- 비어있거나 숫자밖에없을경우 return
        if (expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }
        //-숫자1와 연산자 까지만 입력한경우(아직 완료되지않은 식)
        if (expressionTexts.size != 3 && hasOperator) {
            Toast.makeText(this, " 아직 완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {

            Toast.makeText(this, " 아직 완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        //-endregion
        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        // todo DB에 넣어주는 부분
        //- db에 저장할때는 main스레드가 아닌 새로운 스레드에서 진행해야한다.
        Thread(Runnable {
            db.historyDao().insertHistory(History(null,expressionText,resultText))
        }).start()
        resultTextView.text = ""
        expressionTextView.text = resultText

        isOperator = false
        hasOperator = false
    }

    private fun calculateExpression(): String {

        val expressionTexts = expressionTextView.text.split(" ")

        if (hasOperator.not() || expressionTexts.size != 3) {
            return ""
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            return ""
        }
        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "*" -> (exp1 * exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""

        }
    }

    //- clear버튼 함수 구현
    fun clearButtonClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }

    fun historyButtonClicked(v: View) {

        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()    //-리니어레이아웃 하위의 뷰들이 삭제가됨
        //TODO 디비에서 모든 기록 가져오기
        //TODO 뷰에 모든 기록 할당하기
        Thread(Runnable {
            db.historyDao().getAll().reversed().forEach{

                runOnUiThread{
                    //- historyView 를 LayoutInfalter를 통해생성
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row,null,false)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"

                    historyLinearLayout.addView(historyView)
                }

            }
        }).start()


    }

    fun closeHistoryButtonClicked(v: View) {
        //TODO historyLayout visibiltiy->gone
        historyLayout.isVisible = false
    }

    fun historyClearButtonClicked(v: View) {
        // TODO 뷰에서 모든 기록 삭제
        historyLinearLayout.removeAllViews()
        // TODO DB에서 모든 기록 삭제
        Thread(Runnable {
            db.historyDao().deleteALL()
        }).start()
    }

}

// 확장함수
fun String.isNumber(): Boolean {
    return try {
        this.toBigInteger()
        return true
    } catch (e: NumberFormatException) {
        false
    }
}