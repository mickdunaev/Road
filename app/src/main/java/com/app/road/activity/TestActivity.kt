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

class TestActivity : AppCompatActivity() {
    private lateinit var message_ui: EditText
    private lateinit var list: RecyclerView
    private val messages = ArrayList<TestMessage>()
    private lateinit var adapter: TestChatAdapter
    private var documents = mutableListOf<DocumentSnapshot>()
    private var position = 0

    private var count = 0.0f
    private var summa = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val send = findViewById<View>(R.id.send_button)
        message_ui = findViewById(R.id.message)
        list = findViewById<RecyclerView>(R.id.list)
        list.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
        }

        adapter = TestChatAdapter()
        list.adapter = adapter
//        messages.add(TestMessage(1, "Заголовок"))
//        messages.add(TestMessage(0, "В каждом секторе нужно ответить на несколько вопросов и оценить ваше положение в данной сфере финансовой жизни. Поставьте оценку от 1 до 10 при ответе на вопросы, где 0 это точно нет, 10 точно да. Заостри своё внимание на «просевшие» сферы."))
//        messages.add(TestMessage(2, "10"))
        adapter.setList(messages)

        val db = Firebase.firestore

        db.collection("finans")
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

        while(position < documents.size) {
            val doc = documents[position]
            val style_lg = doc["style"] as Long? ?: 0L
            val style = style_lg.toInt()
            val q = doc["q"] as Boolean? ?: false
            val text = doc["text"] as String? ?: ""
            messages.add(TestMessage(style, text))
            position++
            if(q) break
        }
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
                    "rating_finans" to res
                )

                db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>).addOnCompleteListener {
                    val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
                    pref.edit {
                        putBoolean("test", true)
                        apply()
                    }
                    val intent = Intent(this, TestResultActivity::class.java)
                    intent.putExtra("result", res)
                    startActivity(intent)
                    finish()
                }

            }
            return
        }

        adapter.setList(messages)
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Прохождение теста", this::class.java.simpleName)
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
}