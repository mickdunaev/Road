package com.app.road.v4.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.*
import com.app.road.log
import com.app.road.model.Author
import com.app.road.model.Meditation
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class NewBaseLenaFragment : Fragment() {
    private lateinit var day_ui: TextView
    private var tvDay: TextView? = null
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
        updateData(root, Repository.dayLena)
    }
    private lateinit var morning: View

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Главный экран", this::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_new_base_lena, container, false)
        val trial = root.findViewById<TextView>(R.id.trial)
        val name = root.findViewById<TextView>(R.id.name)
        day_ui = root.findViewById<TextView>(R.id.day)
        if(Repository.trialLena) trial.visibility = View.VISIBLE
        else trial.visibility = View.INVISIBLE

        val course = root.findViewById<TextView>(R.id.course)

        if(Repository.videoMode == 2) {
            course.text = "Тело и психика"
        } else {
            course.text = "Деньги в голове"
        }
        name.text = Repository.name + "!"
        trial.text = " "

        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.currentUser!!.uid

        val plus = root.findViewById<View>(R.id.plus)
        val minus = root.findViewById<View>(R.id.minus)
        morning = root.findViewById<View>(R.id.morning)
        val evening = root.findViewById<View>(R.id.evening)
        val sleep = root.findViewById<View>(R.id.sleep)

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
            updateData(root, Repository.dayLena)
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

            updateData(root, Repository.dayLena)
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
                calculateDay(root, timestamp)
            }
        }
        
//        morning.setOnClickListener {
//            val act = requireActivity() as BaseLenaActivity
//            act.stopSelf()
//            val intent = Intent(requireContext(), TraningActivity::class.java)
//            val mode = 0
//            intent.putExtra("mode", mode)
//            intent.putExtra("lena",true)
//            val collection = when(Repository.dayLena){
//                0L -> "ms"
//                1L -> "ms"
//                else -> "ml"
//            }
//            intent.putExtra("collection", collection)
//            startActivity(intent)
//        }
        setupMorning()
        evening.setOnClickListener {
            val act = requireActivity() as BaseLenaActivity
            act.stopSelf()
            val intent = Intent(requireContext(), LenaTraningActivity::class.java)
            val mode = 1
            intent.putExtra("mode", mode)
            intent.putExtra("lena",true)
            val collection = when(Repository.dayLena){
                10L -> when(mode){
                    2 -> "finans_day8"
                    0 -> "finans_day9"
                    else -> "finans_day10"
                }
                11L -> when(mode){
                    2 -> "finans_day8"
                    0 -> "finans_day9"
                    else -> "finans_day11"
                }
                12L -> when(mode){
                    2 -> "finans_day8"
                    0 -> "finans_day9"
                    else -> "finans_day12"
                }
                13L -> when(mode){
                    2 -> "finans_day8"
                    0 -> "finans_day9"
                    else -> "finans_day13"
                }
                14L -> when(mode){
                    2 -> "finans_day8"
                    0 -> "finans_day9"
                    else -> "finans_day14"
                }

                9L -> when(mode){
                    2 -> "finans_day8"
                    else -> "finans_day9"
                }
                8L -> "finans_day8"
                1L -> "finans_day1"
                2L -> "finans_day2"
                3L -> when(mode){
                    1 -> "finans_day3"
                    else -> "finans_day2"
                }
                4L -> when(mode){
                    1 -> "finans_day4"
                    else -> "finans_day2"
                }
                5L -> when(mode){
                    1 -> "finans_day5"
                    else -> "finans_day2"
                }
                6L -> when(mode){
                    1 -> "finans_day6"
                    else -> "finans_day2"
                }
                7L -> when(mode){
                    1 -> "finans_day7"
                    else -> "finans_day2"
                }
                15L -> when(mode){
                    0 -> "finans_day15"
                    1 -> "finans_day15"
                    2 -> "finans_day15"
                    else -> "finans_day15"
                }
                16L -> when(mode){
                    0 -> "finans_day16"
                    1 -> "finans_day16"
                    2 -> "finans_day15"
                    else -> "finans_day16"
                }
                17L -> when(mode){
                    0 -> "finans_day16"
                    1 -> "finans_day17"
                    2 -> "finans_day15"
                    else -> "finans_day17"
                }
                18L -> when(mode){
                    0 -> "finans_day16"
                    1 -> "finans_day18"
                    2 -> "finans_day15"
                    else -> "finans_day18"
                }
                19L -> when(mode){
                    0 -> "finans_day16"
                    1 -> "finans_day19"
                    2 -> "finans_day19"
                    else -> "finans_day19"
                }
                20L -> when(mode){
                    0 -> "finans_day16"
                    1 -> "finans_day20"
                    2 -> "finans_day15"
                    else -> "finans_day20"
                }
                21L -> when(mode){
                    0 -> "finans_day16"
                    1 -> "finans_day21"
                    2 -> "finans_day15"
                    else -> "finans_day21"
                }
                22L -> when(mode){
                    0 -> "finans_day22"
                    1 -> "finans_day22"
                    2 -> "finans_day22"
                    else -> "finans_day22"
                }
                23L -> when(mode){
                    0 -> "finans_day23"
                    1 -> "finans_day23"
                    2 -> "finans_day22"
                    else -> "finans_day23"
                }
                24L -> when(mode){
                    0 -> "finans_day23"
                    1 -> "finans_day24"
                    2 -> "finans_day22"
                    else -> "finans_day24"
                }
                25L -> when(mode){
                    0 -> "finans_day23"
                    1 -> "finans_day25"
                    2 -> "finans_day22"
                    else -> "finans_day25"
                }
                26L -> when(mode){
                    0 -> "finans_day23"
                    1 -> "finans_day26"
                    2 -> "finans_day22"
                    else -> "finans_day26"
                }
                27L -> when(mode){
                    0 -> "finans_day23"
                    1 -> "finans_day27"
                    2 -> "finans_day22"
                    else -> "finans_day27"
                }
                28L -> when(mode) {
                    0 -> "finans_day23"
                    1 -> "finans_day28"
                    2 -> "finans_day22"
                    else -> "finans_day28"
                }
                else -> "finans_day1"
            }
            intent.putExtra("collection", collection)
            startActivity(intent)
        }

        sleep.setOnClickListener {
            val act = requireActivity() as BaseLenaActivity
            act.stopSelf()
            val intent = Intent(requireContext(), TraningActivity::class.java)
            val mode = 2
            intent.putExtra("mode", mode)
            intent.putExtra("lena",true)
            val collection = when(Repository.dayLena){
                0L -> "ms"
                1L -> "ms"
                else -> "ml"
            }
            intent.putExtra("collection", collection)
            startActivity(intent)
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

        return root
    }
    private fun setupMorning(){
        if(Repository.dayLena != 1L){
            morning.setOnClickListener {
                val act = requireActivity() as BaseLenaActivity
                act.stopSelf()
                val intent = Intent(requireContext(), TraningActivity::class.java)
                val mode = 0
                intent.putExtra("mode", mode)
                intent.putExtra("lena",true)
                val collection = when(Repository.dayLena){
                    0L -> "ms"
                    1L -> "ms"
                    else -> "ml"
                }
                intent.putExtra("collection", collection)
                startActivity(intent)
            }
        } else {
            morning.setOnClickListener {
                val act = requireActivity() as BaseLenaActivity
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
        setupMorning()
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
        val morning = root.findViewById<TextView>(R.id.morning)
        if(Repository.dayLena == 1L){
            morning.text = "Важно перед стартом"

        } else {
            morning.text = "Утренние упражнения"
        }
    }

}