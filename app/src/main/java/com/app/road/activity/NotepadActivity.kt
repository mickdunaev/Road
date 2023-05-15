package com.app.road.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.adapter.NotepadAdapter
import com.app.road.data.FirestoreNames
import com.app.road.log
import com.app.road.model.Notepad
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotepadActivity : AppCompatActivity() {
    val adapter = NotepadAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        var day = Repository.dayLena
        var dayDB = day

        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.uid
        var list = ArrayList<Notepad>()
        var collection = FirestoreNames.ekaterinaMoneyCourseTasks
        if(Repository.selectMillion){
            collection = FirestoreNames.millionCourse
            day = Repository.dayMillion
            dayDB = day
            if(Repository.nextStage){
                dayDB = dayDB - 8
                collection = FirestoreNames.ekaterinaMoneyCourseTasks
            }
        } else
        if (Repository.videoMode == 2) {
            collection = FirestoreNames.ekaterinaBodyCourseTasks
        }
        db.collection(collection)
            .whereEqualTo("day", dayDB)
            .orderBy("id", Query.Direction.ASCENDING)
            .get().addOnCompleteListener {
                list.clear()
                val docs = it.result
                if (docs != null) {
                    for (doc in docs) {
                        val id = doc["id"] as Long ?: 0L
                        val mode = doc["mode"] as Long ?: 0L
                        val daydb = doc["day"] as Long ?: 0L
                        val text = doc["text"] as String
                        list.add(
                            Notepad(
                                daydb.toInt(),
                                mode.toInt(),
                                id.toInt(),
                                text.replace("\\n", "\n")
                            )
                        )
                    }
                }
                Log.d("Mikhael", list.toString())
                //adapter.setListM(list)
                var collection2 = "notepad"
                if(Repository.selectMillion){
                    collection2 = "notepad3"
                } else
                if (Repository.videoMode == 2) {
                    collection2 = "notepad2"
                }

                db.collection(collection2)
                    .whereEqualTo("day", day)
                    .whereEqualTo("uid", uid)
                    .get().addOnCompleteListener {
                        val docs2 = it.result
                        if (docs2 != null) {
                            for (doc in docs2) {
                                val answerId = doc.id
                                val answer = doc["answer"] as String ?: ""
                                val id = doc["id"] as Long ?: 0L
                                addAnswer(list, id.toInt(), answer, answerId)
                            }
                        }
                        Log.d("Mikhael", list.toString())
                        adapter.setListM(list)
                    }

            }

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    private fun copyCollection() {
        val db = Firebase.firestore

        val list = ArrayList<Notepad>()
        val collection = "notepadq2"
        db.collection(collection)
            .get().addOnCompleteListener {
                list.clear()
                val docs = it.result
                if (docs != null) {
                    for (doc in docs) {
                        val id = doc["id"] as Long ?: 0L
                        val mode = doc["mode"] as Long ?: 0L
                        val daydb = doc["day"] as Long ?: 0L
                        val text = doc["text"] as String
                        list.add(
                            Notepad(
                                daydb.toInt(),
                                mode.toInt(),
                                id.toInt(),
                                text
                            )
                        )
                    }
                }

                log("tasks size: ${list.size}")
                for(task in list) {
                    val hashMap = HashMap<String, Any>()
                    hashMap.put("day", task.day)
                    hashMap.put("id", task.id)
                    hashMap.put("mode", task.mode)
                    hashMap.put("text", task.text)
                    db.collection(FirestoreNames.ekaterinaBodyCourseTasks).add(hashMap as Map<String, Any>)
                }
            }
    }

    private fun addAnswer(notes: ArrayList<Notepad>, id: Int, answer: String, answerId: String) {
        for (note in notes) {
            if (note.id == id) {
                note.answer = answer
                note.answerId = answerId
                break
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.uid
        val list = adapter.list
        if (list.size != 0) {
            for (note in list) {
                if (note.mode == 0) continue
                val notepad = hashMapOf(
                    "uid" to uid,
                    "id" to note.id,
                    "day" to note.day,
                    "answer" to note.answer
                )
                var collection2 = "notepad"
                if(Repository.selectMillion){
                    collection2 = "notepad3"
                } else
                if (Repository.videoMode == 2) {
                    collection2 = "notepad2"
                }

                if (note.answerId.isEmpty()) {
                    db.collection(collection2).document().set(notepad as Map<String, Any>)
                } else {
                    db.collection(collection2).document(note.answerId)
                        .update(notepad as Map<String, Any>)
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Блокнот", this::class.java.simpleName)
    }
}