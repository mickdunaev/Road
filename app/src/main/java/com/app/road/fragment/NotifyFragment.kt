package com.app.road.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.DayAffirmationActivity
import com.app.road.activity.PlayMeditationActivity
import com.app.road.adapter.NotifyAdapter
import com.app.road.model.Notify
import com.app.road.v4.Utils
import com.app.road.v4.ui.BaseMillionActivity
import com.app.road.v4.ui.TraningMillionActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotifyFragment : Fragment() {

    private lateinit var list: RecyclerView
    private val adapter = NotifyAdapter()
    private val notificationList: ArrayList<Notify> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_notify, container, false)
        list = root.findViewById(R.id.list)
        list.adapter = adapter
        adapter.containerSelect = { notify ->
            when(notify.message){
                "Поддерживающие уведомление" -> {
                    if(!Repository.selectMillion) {
                        val i = Intent(requireContext(), DayAffirmationActivity::class.java)
                        startActivity(i)
                    }else {
                        val intent = Intent(requireContext(), PlayMeditationActivity::class.java)
                        intent.putExtra("link", "http://mybestway.ru/meditation/1.mp3")
                        intent.putExtra("title", "Короткая медитация")
                        intent.putExtra("duration", "06:09")
                        startActivity(intent)
                    }
                }
            }
        }
        val auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("notify")
            .whereEqualTo("user_id", auth.uid)
            .orderBy("time", Query.Direction.DESCENDING)
            .get().addOnCompleteListener {
                val docs = it.result
                if (docs != null) {
                    notificationList.clear()
                    for(doc in docs){
                        val mes = doc["message"] as String? ?: ""
                        val time = doc["time"] as String? ?: ""
                        val del = doc["deleted"] as Boolean? ?: false
                        val id = doc.id
                        notificationList.add(Notify(mes,time,id))
                    }
                    adapter.setList(notificationList)
                }
            }
        adapter.notifySelect = { notify ->
            db.collection("notify")
                .document(notify.id)
                .delete()
                .addOnCompleteListener {
                    db.collection("notify")
                        .whereEqualTo("user_id", auth.uid)
                        .orderBy("time", Query.Direction.DESCENDING)
                        .get().addOnCompleteListener {
                            val docs = it.result
                            if (docs != null) {
                                val nl = ArrayList<Notify>()
                                for(doc in docs){
                                    val mes = doc["message"] as String? ?: ""
                                    val time = doc["time"] as String? ?: ""
                                    val del = doc["deleted"] as Boolean? ?: false
                                    val id = doc.id
                                    //if(del) continue
                                    nl.add(Notify(mes,time,id))
                                }
                                adapter.setList(nl)
                            }
                        }
                }

        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // this method is called
                // when the item is moved.
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                val deletedNotify: Notify =
                    notificationList[viewHolder.adapterPosition]

                removeNotificationFirebase(db, auth, deletedNotify)
            }
        }).attachToRecyclerView(list)

        return root
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Уведомления", this::class.java.simpleName)
    }

    private fun removeNotificationFirebase(db: FirebaseFirestore, auth: FirebaseAuth, notify: Notify) {
        db.collection("notify")
            .document(notify.id)
            .delete()
            .addOnCompleteListener {
                db.collection("notify")
                    .whereEqualTo("user_id", auth.uid)
                    .orderBy("time", Query.Direction.DESCENDING)
                    .get().addOnCompleteListener {
                        val docs = it.result
                        if (docs != null) {
                            val nl = ArrayList<Notify>()
                            for(doc in docs){
                                val mes = doc["message"] as String? ?: ""
                                val time = doc["time"] as String? ?: ""
                                val del = doc["deleted"] as Boolean? ?: false
                                val id = doc.id
                                //if(del) continue
                                nl.add(Notify(mes,time,id))
                            }
                            adapter.setList(nl)
                        }
                    }
            }
    }
}