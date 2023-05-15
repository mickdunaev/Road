package com.app.road.v4.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.*
import com.app.road.log
import com.app.road.model.Author
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MillionFragment : Fragment() {
    private lateinit var day_ui: TextView
    private lateinit var plus: View
    private var nextStage = false
    private fun calculateDay(root: View, timestamp: Long){
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
        Repository.dayMillion = day
        if(Repository.dayMillion > 2 && Repository.selectMillion && !Repository.millionPremium) {
            Repository.dayMillion = 2
            day_ui.text = Repository.dayMillion.toString()
        } else {
            day_ui.text = Repository.dayMillion.toString()
        }
        Repository.nextStage = false
        if(Repository.dayMillion > 8){
           // gotoNewCourse()
            nextStage = true
            Repository.nextStage = true
        }
        updateData(root, Repository.dayMillion)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_million, container, false)
        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.currentUser!!.uid
        val trial = root.findViewById<TextView>(R.id.trial)
        val name = root.findViewById<TextView>(R.id.name)
        day_ui = root.findViewById<TextView>(R.id.day)
        if(Repository.trialMillion) trial.visibility = View.VISIBLE
        else trial.visibility = View.INVISIBLE
        val diary = root.findViewById<View>(R.id.diary)
        val notepad = root.findViewById<View>(R.id.notepad)
        val scheduler = root.findViewById<View>(R.id.scheduler)
        plus = root.findViewById<View>(R.id.plus)
        val minus = root.findViewById<View>(R.id.minus)
        name.text = Repository.name + "!"
        day_ui.text = Repository.dayMillion.toString()
        val morning = root.findViewById<TextView>(R.id.morning)
        val evening = root.findViewById<View>(R.id.evening)
        val sleep = root.findViewById<View>(R.id.sleep)
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

        db.collection("users").document(uid).get().addOnCompleteListener {
            val doc = it.result
            if(doc != null){
                var timestamp = doc["timestamp_million"] as Long? ?: 0L
                if(timestamp == 0L){
                    val cal = Calendar.getInstance()
                    timestamp = cal.timeInMillis
                    val user = hashMapOf(
                        "timestamp_million" to timestamp
                    )
                    db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>)
                }
                calculateDay(root, timestamp)
            }
        }
        plus.setOnClickListener {
            Repository.dayMillion ++
            Repository.nextStage = false
            if(Repository.dayMillion > 2 && Repository.selectMillion && !Repository.millionPremium) {
                Repository.dayMillion = 2
                plus.visibility = View.GONE
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Приобретите курс, чтобы получить доступ ко всем дням")

                builder.setPositiveButton("Купить") { dialog, which ->
                    val intent = Intent(requireContext(), SubscribeBaseActivity::class.java)
                    intent.putExtra("author", "Екатерина")
                    intent.putExtra("course_name", "Путь на миллион")
                    startActivity(intent)
                }

                builder.setNegativeButton("Позже") { dialog, which ->

                }
                builder.show()
            }else if(Repository.dayMillion > 8){
                nextStage = true
                Repository.nextStage = true
                //gotoNewCourse()
                //Repository.dayMillion = 8
                //plus.visibility = View.INVISIBLE
                //requireActivity().startActivity(Intent(requireContext(), ReportActivity::class.java))
            }else if(Repository.dayMillion > 36){
                //gotoNewCourse()
                Repository.dayMillion = 36
                plus.visibility = View.INVISIBLE
                requireActivity().startActivity(Intent(requireContext(), ReportActivity::class.java))
            } else {
                minus.visibility = View.VISIBLE
            }

            day_ui.text = Repository.dayMillion.toString()
            updateData(root, Repository.dayMillion)
        }
        minus.setOnClickListener {
            Repository.dayMillion --
            if(Repository.dayMillion >  8){
                Repository.nextStage = true
            } else {
                Repository.nextStage = false
            }
            if(Repository.dayMillion < 1) {
                Repository.dayMillion = 1
                minus.visibility = View.GONE
            } else {
                plus.visibility = View.VISIBLE
            }
            day_ui.text = Repository.dayMillion.toString()
            updateData(root, Repository.dayMillion)
        }

        setupMorning(root)
        evening.setOnClickListener {
            val act = requireActivity() as BaseMillionActivity
            act.stopSelf()
            val intent = Intent(requireContext(), EveningMillionActivity::class.java)
            val mode = 1
            intent.putExtra("mode", mode)
            intent.putExtra("day",Repository.dayMillion)
            intent.putExtra("next", nextStage)
            startActivity(intent)
        }

        sleep.setOnClickListener {
            val act = requireActivity() as BaseMillionActivity
            act.stopSelf()
            val intent = Intent(requireContext(), TraningMillionActivity::class.java)
            val mode = 2
            intent.putExtra("mode", mode)
            intent.putExtra("day",Repository.dayMillion)
            val collection = when(Repository.dayMillion){
                0L -> "ms"
                1L -> "ms"
                else -> "ml"
            }
            intent.putExtra("collection", collection)

            startActivity(intent)
        }


        return root
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Главный экран", this::class.java.simpleName)
    }

    private fun setupMorning(root: View){
        val morning = root.findViewById<View>(R.id.morning)
        if(Repository.dayMillion != 1L){
            morning.setOnClickListener {
                val act = requireActivity() as BaseMillionActivity
                act.stopSelf()
                val intent = Intent(requireContext(), TraningMillionActivity::class.java)
                val mode = 0
                intent.putExtra("mode", mode)
                intent.putExtra("day",Repository.dayMillion)
                val collection = when(Repository.dayMillion){
                    0L -> "ms"
                    1L -> "ms"
                    else -> "ml"
                }
                intent.putExtra("collection", collection)

                startActivity(intent)
            }
        } else {
            morning.setOnClickListener {
                val act = requireActivity() as BaseMillionActivity
                act.stopSelf()
                val intent = Intent(requireContext(), PlayMeditationActivity::class.java)
                intent.putExtra("link", "http://mybestway.ru/start/333.mp3")
                intent.putExtra("title", "Важно перед стартом")
                intent.putExtra("duration", "04:36")
                startActivity(intent)
            }
        }
    }
    private fun updateData(root: View, day: Long){
        setupMorning(root)
        if(day < 3){
            root.findViewById<TextView>(R.id.course).text = "Вводный курс"
        } else if(day < 9){
            root.findViewById<TextView>(R.id.course).text = "Энергия"
        } else {
            root.findViewById<TextView>(R.id.course).text = "Деньги в голове"
        }
        val morning = root.findViewById<TextView>(R.id.morning)
        if(Repository.dayMillion == 1L){
            morning.text = "Важно перед стартом"

        } else {
            morning.text = "Утренние упражнения"
        }

    }
    private  fun gotoNewCourse(){
        val db = Firebase.firestore
        val auth = Firebase.auth
        plus.visibility = View.INVISIBLE
        Repository.selectMillion = false
        Repository.dayMillion = 8
        Repository.dayLena = 1
        Repository.selectLena = true
        Repository.videoMode = 1
        val pref = requireActivity().getSharedPreferences("road", Context.MODE_PRIVATE)
        pref.edit {
            putInt("video", 1)
            putString("current_author", "lena")
            apply()
        }
        val uid = auth.currentUser!!.uid
        val cal = Calendar.getInstance()
        val timestamp = cal.timeInMillis

        val user = hashMapOf(
            "trial_new_course" to false,
            "premium_new_course" to true,
            "select_new_course" to true,
            "select_million" to false,
            "timestamp_new" to timestamp
        )
        db.collection("users")
            .document(uid)
            .update(user as Map<String, Any>)
            .addOnCompleteListener {
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .get()
                    .addOnCompleteListener { value ->
                        if (value != null) {
                            val doc = value.result
                            var trial = doc["trial_count"] as Long? ?: 0L
                            trial += 1L
                            val usr = hashMapOf(
                                "trial_count" to trial
                            )
                            db.collection("users")
                                .document(uid)
                                .update(usr as Map<String, Any>)
                                .addOnCompleteListener {
                                    startActivity(Intent(requireActivity(), BaseLenaActivity::class.java))
                                    requireActivity().finish()

                                }

                        }
                    }

            }


    }
}