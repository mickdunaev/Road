package com.app.road.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.*
import com.app.road.adapter.MeditationAdapterLena
import com.app.road.log
import com.app.road.model.Author
import com.app.road.model.Meditation
import com.app.road.ui.SelectCourseActivity
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class BaseLenaFragment : Fragment() {

    private lateinit var day_ui: TextView
    private var tvDay: TextView? = null

    private var adapter = MeditationAdapterLena()

    //timestamp время установки приложения
    private fun calculateDay(timestamp: Long){
        val setupCal = Calendar.getInstance()
        setupCal.timeInMillis = timestamp
        val currentCal = Calendar.getInstance()
        val currentMils = currentCal.timeInMillis
        Log.d("Mikhael",currentCal.toString())
        var day = if(Repository.videoMode == 1) 0L else 1L
        setupCal.set(Calendar.HOUR_OF_DAY, 0)
        setupCal.set(Calendar.MINUTE, 0)
        setupCal.set(Calendar.SECOND, 0)
        while (true){
            setupCal.add(Calendar.DAY_OF_MONTH, 1)
            val nt = setupCal.timeInMillis
            if(nt > currentMils) break
            day++
            if(day > 28L) {
                day = 28L
                break
            }
        }
        Repository.dayLena = day
        if(Repository.dayLena > 5 && Repository.trialLena && Repository.selectLena) {
            Repository.dayLena = 5
            day_ui.text = Repository.dayLena.toString()
            //val intent = Intent(requireContext(), SubscribeBaseActivity::class.java)
            //startActivity(intent)
            //requireActivity().finish()
            day_ui.text = Repository.dayLena.toString()
        } else {
            day_ui.text = Repository.dayLena.toString()
        }
        updateData(Repository.dayLena)
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Главный экран", this::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_base_lena, container, false)

        val trial = root.findViewById<TextView>(R.id.trial)
        val name = root.findViewById<TextView>(R.id.name)
        day_ui = root.findViewById<TextView>(R.id.day)
        if(Repository.trialLena) trial.visibility = View.VISIBLE
        else trial.visibility = View.INVISIBLE

        val course = root.findViewById<TextView>(R.id.course)

        if(Repository.videoMode == 2) {
            course.text = "Тело и психика"
        }

        name.text = Repository.name + "!"
        trial.text = " "

        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.currentUser!!.uid

        val plus = root.findViewById<View>(R.id.plus)
        val minus = root.findViewById<View>(R.id.minus)
        val diary = root.findViewById<View>(R.id.diary)
        val notepad = root.findViewById<View>(R.id.notepad)
        val scheduler = root.findViewById<View>(R.id.scheduler)

        tvDay = root.findViewById(R.id.tvDay)

        plus.setOnClickListener {
            Repository.dayLena ++
            log("dayLena: ${Repository.dayLena}")
            log("premiumLena: ${Repository.premiumLena}")
            log("selectLena: ${Repository.selectLena}")
            if(Repository.dayLena > 5 && !Repository.premiumLena && Repository.selectLena) {
                Repository.dayLena = 5
                plus.visibility = View.GONE
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Приобретите курс, чтобы получить доступ ко всем дням")

                builder.setPositiveButton("Купить") { dialog, which ->
                    val intent = Intent(requireContext(), SubscribeBaseActivity::class.java)
                    intent.putExtra("author", Author.ELENA.name)
                    startActivity(intent)
                }

                builder.setNegativeButton("Позже") { dialog, which ->

                }
                builder.show()
//                if(Repository.trialLena){
//                    val intent = Intent(requireContext(), SubscribeBaseActivity::class.java)
//                    startActivity(intent)
//                    requireActivity().finish()
//                }
            }else if(Repository.dayLena > 28){
                Repository.dayLena = 28
                plus.visibility = View.INVISIBLE
                requireActivity().startActivity(Intent(requireContext(), ReportActivity::class.java))
            } else {
                minus.visibility = View.VISIBLE
            }

            if(Repository.dayLena == 0L) {
                day_ui.visibility = View.INVISIBLE
                tvDay?.text = "Введение в курс"
            } else {
                day_ui.text = Repository.dayLena.toString()
                day_ui.visibility = View.VISIBLE
                tvDay?.text = "День"
            }

            day_ui.text = Repository.dayLena.toString()
            updateData(Repository.dayLena)
        }
        minus.setOnClickListener {
            Repository.dayLena --

            val minDay = if (Repository.videoMode == 1) 0 else 1
            if(Repository.dayLena < minDay) {
                Repository.dayLena = minDay.toLong()
                minus.visibility = View.GONE
            } else {
                if(Repository.videoMode == 1) {
                    day_ui.visibility = View.VISIBLE
                }
                plus.visibility = View.VISIBLE
            }
            if(Repository.dayLena == 0L && Repository.videoMode == 1) {
                day_ui.visibility = View.INVISIBLE
                tvDay?.text = "Введение в курс"
            } else {
                day_ui.text = Repository.dayLena.toString()
                day_ui.visibility = View.VISIBLE
                tvDay?.text = "День"
            }

            updateData(Repository.dayLena)
        }

        db.collection("users").document(uid).get().addOnCompleteListener {
            val doc = it.result
            if(doc != null){
                var timestamp = doc["timestamp_new"] as Long? ?: 0L
                if(timestamp == 0L){
                    val cal = Calendar.getInstance()
                    timestamp = cal.timeInMillis
                    val user = hashMapOf(
                        "timestamp_new" to timestamp
                    )
                    db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>)
                }
                calculateDay(timestamp)
            }
        }

        notepad.setOnClickListener {
            startActivity(Intent(requireContext(), NotepadActivity::class.java))
        }

        diary.setOnClickListener {
            //startActivity(Intent(requireContext(), NotepadActivity::class.java))
            startActivity(Intent(requireContext(), DiaryActivity::class.java))
        }

        scheduler.setOnClickListener {
            val intent = Intent(requireContext(), SchedulerActivity::class.java)
            startActivity(intent)
        }

        adapter.notepadSelect = {
            startActivity(Intent(requireContext(), NotepadActivity::class.java))
        }

        adapter.meditationSelect = {meditation ->
            if(meditation.mode == 0L){
                val intent = Intent(requireContext(), PlayMeditationActivity::class.java)
                intent.putExtra("link", meditation.link)
                intent.putExtra("title", meditation.title)
                intent.putExtra("duration", meditation.duration)
                startActivity(intent)
            } else {
                val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
                intent.putExtra("url", meditation.link)
                intent.putExtra("title", meditation.title)
                intent.putExtra("duration", meditation.duration)
                startActivity(intent)
            }
        }

        val list = root.findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter

        //updateData(Repository.dayLena)


        return root
    }

    private fun updateData(day: Long){
        val db = Firebase.firestore
        var course = "course"

        log("videoMode: ${Repository.videoMode}")
        if(Repository.videoMode == 2){
            course = "course2"
        } else {
            if(Repository.dayLena == 0L && Repository.videoMode == 1) {
                day_ui.visibility = View.INVISIBLE
                tvDay?.text = "Введение в курс"
            } else {
                day_ui.text = Repository.dayLena.toString()
                day_ui.visibility = View.VISIBLE
                tvDay?.text = "День"
            }
        }
        db.collection(course)
            .orderBy("id", Query.Direction.ASCENDING)
            .whereEqualTo("day", day)
            .get()
            .addOnCompleteListener {
                val docs = it.result
                if (docs != null) {
                    val ls = ArrayList<Meditation>()
                    for(doc in docs) {
                        val title = doc["title"] as String? ?: ""
                        val link = doc["link"] as String? ?: ""
                        val duration = doc["duration"] as String? ?: ""
                        val id = doc["id"] as Long? ?: 0L
                        val m = doc["mode"] as Long? ?: -1L
                        ls.add(Meditation(
                            title,
                            id,
                            m,
                            link,
                            duration,
                            courseButton = day == 0L // для того, чтобы в нулевом дне на кнопке снизу менять текст
                        ))
                    }
                    adapter.setList(ls)
                    Log.d("Mikhael", docs.size().toString())
                }

            }

    }


}