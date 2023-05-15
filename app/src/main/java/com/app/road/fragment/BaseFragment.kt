package com.app.road.fragment

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.*
import com.app.road.model.Author
import com.app.road.service.MediaPlayerService
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yandex.metrica.impl.ob.Vi
import java.util.*

class BaseFragment : Fragment() {
    private val url = "http://mybestway.ru/audio/desc.mp3"
    var isPlay = false
    var isResume = false
    private lateinit var playImage: ImageView
    private fun updateUi(){
        if(isPlay){
            playImage.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            playImage.setImageResource(R.drawable.icon_play)
        }
    }

    private lateinit var day_ui:TextView
    private lateinit var morning: View
    private lateinit var morning_day1: View

    //timestamp время установки приложения
    private fun calculateDay(timestamp: Long){
        val setupCal = Calendar.getInstance()
        setupCal.timeInMillis = timestamp
        val currentCal = Calendar.getInstance()
        val currentMils = currentCal.timeInMillis
        Log.d("Mikhael",currentCal.toString())
        var day = 1L
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
        Repository.day = day
        if(Repository.day > 7 && Repository.trial){
            Repository.day = 7
            day_ui.text = Repository.day.toString()
            val intent = Intent(requireContext(), SubscribeBaseActivity::class.java)
            //startActivity(intent)
            //requireActivity().finish()
        } else {
            day_ui.text = Repository.day.toString()
        }
        if(Repository.day == 1L){
            morning.visibility = View.INVISIBLE
            morning_day1.visibility = View.VISIBLE
        } else {
            morning.visibility = View.VISIBLE
            morning_day1.visibility = View.INVISIBLE
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_base, container, false)

        val trial = root.findViewById<View>(R.id.trial)
        val name = root.findViewById<TextView>(R.id.name)
        day_ui = root.findViewById<TextView>(R.id.day)
        playImage = root.findViewById(R.id.play)
        if(Repository.trial) trial.visibility = View.VISIBLE
        else trial.visibility = View.INVISIBLE

        name.text = Repository.name + "!"

        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.currentUser!!.uid

        val plus = root.findViewById<View>(R.id.plus)
        val minus = root.findViewById<View>(R.id.minus)
        morning = root.findViewById<View>(R.id.morning)
        val evening = root.findViewById<View>(R.id.evening)
        val sleep = root.findViewById<View>(R.id.sleep)
        val diary = root.findViewById<View>(R.id.diary)
        val scheduler = root.findViewById<View>(R.id.scheduler)
        morning_day1 = root.findViewById<View>(R.id.morning_day1)
        updateUi()

        plus.setOnClickListener {
            Repository.day ++
            if(Repository.day > 7 && !Repository.premium) {
                Repository.day = 7
                plus.visibility = View.GONE
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Приобретите курс, чтобы получить доступ ко всем дням")

                builder.setPositiveButton("Купить") { dialog, which ->
                    val intent = Intent(requireContext(), SubscribeBaseActivity::class.java)
                    intent.putExtra("author", Author.VASILIY.name)
                    startActivity(intent)
                }

                builder.setNegativeButton("Позже") { dialog, which -> }
                builder.show()
                if(Repository.trial){

                }
            }else if(Repository.day > 28){
                Repository.day = 28
                plus.visibility = View.INVISIBLE
            } else {
                minus.visibility = View.VISIBLE
            }
            day_ui.text = Repository.day.toString()
            if(Repository.day == 1L){
                morning.visibility = View.INVISIBLE
                morning_day1.visibility = View.VISIBLE
            } else {
                morning.visibility = View.VISIBLE
                morning_day1.visibility = View.INVISIBLE
            }

        }
        minus.setOnClickListener {
            Repository.day --
            if(Repository.day < 1) {
                Repository.day = 1
                minus.visibility = View.GONE
            } else {
                plus.visibility = View.VISIBLE
            }
            day_ui.text = Repository.day.toString()
            if(Repository.day == 1L){
                morning.visibility = View.INVISIBLE
                morning_day1.visibility = View.VISIBLE
            } else {
                morning.visibility = View.VISIBLE
                morning_day1.visibility = View.INVISIBLE
            }

        }

         morning_day1.setOnClickListener {
            val activity = requireActivity() as BaseActivity
            //activity.playAudio(url)
             if(!isPlay){
                 if(!isResume){
                     activity.playAudio(url)
                 } else {
                     activity.resumeAudio()
                 }

                 isPlay = true
                 updateUi()
             } else {
                 isResume = true
                 isPlay = false
                 activity.pauseAudio()
                 updateUi()
             }

         }

        db.collection("users").document(uid).get().addOnCompleteListener {
            val doc = it.result
            if(doc != null){
                var timestamp = doc["timestamp"] as Long? ?: 0L
                if(timestamp == 0L){
                    val cal = Calendar.getInstance()
                    timestamp = cal.timeInMillis
                    val user = hashMapOf(
                        "timestamp" to timestamp
                    )
                    db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>)
                }
                calculateDay(timestamp)
            }
        }


        if(Repository.day == 1L){
            morning.visibility = View.INVISIBLE
            morning_day1.visibility = View.VISIBLE
        } else {
            morning.visibility = View.VISIBLE
            morning_day1.visibility = View.INVISIBLE
        }

        morning.setOnClickListener {
            val act = requireActivity() as BaseActivity
            act.stopSelf()
            val intent = Intent(requireContext(), TraningActivity::class.java)
            val mode = 0
            intent.putExtra("mode", mode)
            val collection = when(Repository.day){
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
                8L -> "finans_day8"
                9L -> when(mode){
                    2 -> "finans_day8"
                    else -> "finans_day9"
                }
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
                28L -> when(mode){
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

        evening.setOnClickListener {
            val act = requireActivity() as BaseActivity
            act.stopSelf()
            val intent = Intent(requireContext(), TraningActivity::class.java)
            val mode = 1
            intent.putExtra("mode", mode)
            val collection = when(Repository.day){
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
            val act = requireActivity() as BaseActivity
            act.stopSelf()
            val intent = Intent(requireContext(), TraningActivity::class.java)
            val mode = 2
            intent.putExtra("mode", mode)
            val collection = when(Repository.day){
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
                28L -> when(mode){
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

        diary.setOnClickListener {
            startActivity(Intent(requireContext(), DiaryActivity::class.java))
        }

        scheduler.setOnClickListener {
            val intent = Intent(requireContext(), SchedulerActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Главный экран", this::class.java.simpleName)
    }
}