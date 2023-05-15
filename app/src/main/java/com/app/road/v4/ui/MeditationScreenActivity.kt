package com.app.road.v4.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.PlayMeditationActivity
import com.app.road.activity.SubscribeBaseActivity
import com.app.road.adapter.MediAdapter
import com.app.road.adapter.MeditationAdapter
import com.app.road.model.Meditation
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MeditationScreenActivity : AppCompatActivity() {
    private var adapter = MediAdapter()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val db = Firebase.firestore
        db.collection("medi")
            .orderBy("id", Query.Direction.ASCENDING)
            .get()
            .addOnCompleteListener {medi ->
                val uid = Firebase.auth.uid
                db.collection("pay_medi")
                    .whereEqualTo("user", uid)
                    .get()
                    .addOnCompleteListener {
                        val pay_medi = ArrayList<Long>()
                        val docs_pay_medi = it.result
                        if(docs_pay_medi != null){
                            for(doc_pay_medi in docs_pay_medi){
                                val id = doc_pay_medi["medi"] as Long? ?: 0L
                                pay_medi.add(id)
                            }
                        }
                        val docs = medi.result
                        if (docs != null) {
                            val ls = ArrayList<Meditation>()
                            for(doc in docs){
                                val title = doc["name"] as String? ?: ""
                                val link = doc["link"] as String? ?: ""
                                val duration = doc["duration"] as String? ?: ""
                                val id = doc["id"] as Long? ?: 0L
                                val m = -1L
                                val meditation = Meditation(title,id,m,link, duration)
                                for(tm in pay_medi){
                                    if(id == tm){
                                        meditation.isKuplena = true
                                        break
                                    }
                                }
                                ls.add(meditation)
                            }
                            adapter.setList(ls)
                            Log.d("Mikhael", docs.size().toString())
                        }

                    }

            }

    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Список платных медитаций", this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_screen)
        findViewById<View>(R.id.back).setOnClickListener {
            finish()
        }
        findViewById<View>(R.id.payCourse).setOnClickListener {
            val intent = Intent(this, SubscribeBaseActivity::class.java)
            intent.putExtra("author", "Медитации")
            intent.putExtra("course_name", "Медитации")
            startActivityForResult(intent,111)

        }
        val itog_medi = findViewById<TextView>(R.id.itog_medi)
        val itog_summa = findViewById<TextView>(R.id.itog_summa)
        adapter.meditationSelect = {meditation ->
            val intent = Intent(this, PlayMeditationActivity::class.java)
            intent.putExtra("link", meditation.link)
            intent.putExtra("title", meditation.title)
            intent.putExtra("duration", meditation.duration)
            if(meditation.isKuplena){
                intent.putExtra("medi", false)
            } else {
                intent.putExtra("medi", true)
            }

            startActivity(intent)
        }
        adapter.paySelect = {medis ->
            var summa = 0
            var medi = 0
            Repository.payList.clear()
            for(med in medis){
                if(med.pay){
                    summa += 100
                    medi ++
                    Repository.payList.add(med.id)
                }
            }
            when(medi){
                1 -> {
                    itog_medi.text = "Выбрана: 1 медитация"
                }
                else -> {
                    itog_medi.text = "Выбрано: ${medi} медитаций"
                }
            }
            itog_summa.text = "Итого: ${summa}₽"

        }
        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter
        val db = Firebase.firestore
        db.collection("medi")
            .orderBy("id", Query.Direction.ASCENDING)
            .get()
            .addOnCompleteListener {medi ->
                val uid = Firebase.auth.uid
                db.collection("pay_medi")
                    .whereEqualTo("user", uid)
                    .get()
                    .addOnCompleteListener {
                        val pay_medi = ArrayList<Long>()
                        val docs_pay_medi = it.result
                        if(docs_pay_medi != null){
                            for(doc_pay_medi in docs_pay_medi){
                                val id = doc_pay_medi["medi"] as Long? ?: 0L
                                pay_medi.add(id)
                            }
                        }
                        val docs = medi.result
                        if (docs != null) {
                            val ls = ArrayList<Meditation>()
                            for(doc in docs){
                                val title = doc["name"] as String? ?: ""
                                val link = doc["link"] as String? ?: ""
                                val duration = doc["duration"] as String? ?: ""
                                val id = doc["id"] as Long? ?: 0L
                                val m = -1L
                                val meditation = Meditation(title,id,m,link, duration)
                                for(tm in pay_medi){
                                    if(id == tm){
                                        meditation.isKuplena = true
                                        break
                                    }
                                }
                                ls.add(meditation)
                            }
                            adapter.setList(ls)
                            Log.d("Mikhael", docs.size().toString())
                        }

                    }

            }

    }
}