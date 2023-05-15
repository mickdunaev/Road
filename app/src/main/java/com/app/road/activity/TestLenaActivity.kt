package com.app.road.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.adapter.TestChatAdapter
import com.app.road.model.TestMessage
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TestLenaActivity : AppCompatActivity() {
    private lateinit var message_ui: EditText
    private lateinit var list: RecyclerView
    private val messages = ArrayList<TestMessage>()
    private lateinit var adapter: TestChatAdapter
    private var documents = mutableListOf<DocumentSnapshot>()
    private var position = 0

    private var count = 0.0f
    private var summa = 0.0f
    private var summa1 = 0.0f
    private var summa2 = 0.0f
    private var summa3 = 0.0f
    private var rezim = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val send = findViewById<View>(R.id.send_button)
        message_ui = findViewById(R.id.message)
        list = findViewById<RecyclerView>(R.id.list)
        list.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
        }
        val db = Firebase.firestore
        val auth = Firebase.auth
        val uid = auth.currentUser!!.uid
        val user = hashMapOf(
            "select_new_course" to true
        )
        db.collection("users")
            .document(uid)
            .update(user as Map<String, Any>)

        adapter = TestChatAdapter()
        list.adapter = adapter
//        messages.add(TestMessage(1, "Заголовок"))
//        messages.add(TestMessage(0, "В каждом секторе нужно ответить на несколько вопросов и оценить ваше положение в данной сфере финансовой жизни. Поставьте оценку от 1 до 10 при ответе на вопросы, где 0 это точно нет, 10 точно да. Заостри своё внимание на «просевшие» сферы."))
//        messages.add(TestMessage(2, "10"))
        adapter.setList(messages)

        db.collection("finans2")
            .orderBy("id", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
            if(error == null && value!= null) {
                documents.clear()
                documents.addAll(value.documents)
                updateUi(false)
            }
        }

        send.setOnClickListener {
            sendMessage()
        }

    }

    private fun updateUi(u: Boolean){
            //var numSection = 0
        while(position < documents.size) {

            val doc = documents[position]
            val style_lg = doc["style"] as Long? ?: 0L
            val style = style_lg.toInt()
            val q1 = doc["q"] as Boolean?
            var q = true
            if(q1 != null) q = q1
            if(style == 1) rezim ++
            val text = doc["text"] as String? ?: ""
            messages.add(TestMessage(style, text))
            position++
            if(q) break
        }
        //rezim = numSection
        if(u){
            var result = 0.0f
            if(count == 0.0f) return
            else {
                result = summa / count
                val res = "%.1f".format(result)
                Log.d("Mikhael", res)
                val auth = Firebase.auth
                val db = Firebase.firestore
                if(auth.currentUser == null) return
                val user = hashMapOf(
                    "rating_finans2" to res
                )

                db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>).addOnCompleteListener {
                    val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
                    pref.edit {
                        putBoolean("test2", true)
                        apply()
                    }
                    val intent = Intent(this, TestLenaResultActivity::class.java)
                    intent.putExtra("result", res)
                    intent.putExtra("result1",summa1.toString())
                    intent.putExtra("result2",summa2.toString())
                    intent.putExtra("result3",summa3.toString())
                    startActivity(intent)
                    finish()
                }

            }
            return
        }

        adapter.setList(messages)
    }

    private fun sendMessage(){
        val message = message_ui.text.toString()
        if(!message.isEmpty()){
            try {
                val num = message.toInt()
                if(num < 1 || num > 10){
                    Toast.makeText(baseContext, "Введите число от 1 до 10", Toast.LENGTH_SHORT).show()
                    message_ui.text.clear()
                } else {
                    messages.add(TestMessage(2, message))
                    message_ui.text.clear()
                    if(rezim == 1){
                        summa1 += num.toFloat()
                    } else if (rezim == 2){
                        summa2 += num.toFloat()
                    } else if(rezim == 3){
                        summa3 += num.toFloat()
                    }
                    summa += num.toFloat()
                    count += 1.0f
                    if(position == documents.size) updateUi(true)
                    else updateUi(false)
                }
            } catch (e: Exception){
                Toast.makeText(baseContext, "Введите число от 1 до 10", Toast.LENGTH_SHORT).show()
                message_ui.text.clear()
            }
        } else {
            Toast.makeText(baseContext, "Введите число от 1 до 10", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Прохождение теста Екатерины", this::class.java.simpleName)
    }
}