package com.app.road.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.road.*
import com.app.road.adapter.TaskAdapter
import com.app.road.model.Audio
import com.app.road.model.Task
import com.app.road.model.TaskHelp
import com.app.road.service.MediaPlayerService
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class SchedulerActivity : AppCompatActivity() {
    private var player: MediaPlayerService? = null
    var serviceBound = false

    private lateinit var list: RecyclerView
    private val allTask = ArrayList<TaskHelp>()
    private var mselY: Int? = null
    private var mselM: Int? = null
    private var mselD: Int? = null
    private val adapter = TaskAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val play = findViewById<View>(R.id.play)
        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this)
        list.setFocusable(false)
        list.adapter = adapter
        //adapter.setList(allTask)

        val calendar = Calendar.getInstance()
        val currYear = calendar.get(Calendar.YEAR)
        val currMonth = calendar.get(Calendar.MONTH) + 1
        val currDay = calendar.get(Calendar.DAY_OF_MONTH)
        mselD = currDay
        mselM = currMonth
        mselY = currYear
        var selectYear = currYear
        var selectMonth = currMonth
        var selectDay = currDay

        val auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("tasks")
            .whereEqualTo("user_id", auth.uid)
            .addSnapshotListener { value, error ->
                if(error == null && value!= null) {
                    val docs = value.documents
                    val ls = ArrayList<Task>()
                    allTask.clear()
                    if (docs.size != 0) {
                        for (doc in docs) {
                            val id = doc.id
                            val is_complited = doc["is_complited"] as Boolean? ?: false
                            if(is_complited) continue
                            val header = doc["header"] as String? ?: ""
                            val text = doc["text"] as String? ?: ""
                            val fromYear = doc["from_year"] as Long? ?: 1970
                            val fromMonth = doc["from_month"] as Long? ?: 1
                            val fromDay = doc["from_day"] as Long? ?: 1
                            val toYear = doc["to_year"] as Long? ?: 1970
                            val toMonth = doc["to_month"] as Long? ?: 1
                            val toDay = doc["to_day"] as Long? ?: 1
                            val fromDate =
                                formatDate(fromYear.toInt(), fromMonth.toInt(), fromDay.toInt())
                            val toDate = formatDate(toYear.toInt(), toMonth.toInt(), toDay.toInt())
                            val task = Task(header, text, fromDate, toDate, id)
                            allTask.add(TaskHelp(task, toYear.toInt(), toMonth.toInt(), toDay.toInt(), fromYear.toInt(), fromMonth.toInt(), fromDay.toInt()))
                            var t = false
                            if(mselD!= null && mselM != null && mselY != null){
                                t = testDate(fromYear.toInt(), fromMonth.toInt(), fromDay.toInt(), toYear.toInt(), toMonth.toInt(), toDay.toInt(), mselY!!, mselM!!, mselD!!)
                            } else {
                                t = testDate(fromYear.toInt(), fromMonth.toInt(), fromDay.toInt(), toYear.toInt(), toMonth.toInt(), toDay.toInt(), currYear, currMonth, currDay)
                            }

                            if(t){
                                ls.add(task)
                            }
                        }
                        adapter.mselectYear = mselY
                        adapter.mselectMonth = mselM
                        adapter.mselectDay = mselD
                        adapter.setList(ls)
                    }
                }

            }
        adapter.taskSelect = { task ->
            val intent = Intent(this, TaskCompliteActivity::class.java)
            intent.putExtra("header", task.header)
            intent.putExtra("text", task.text)
            intent.putExtra("fromDate", task.fromDate)
            intent.putExtra("toDate", task.toDate)
            intent.putExtra("id", task.id)
            startActivity(intent)
        }

        play.setOnClickListener {
            //play header
            playAudio("http://mybestway.ru/audio/scheduler.mp3")
        }
        adapter.calandarSelect = { year, month, day ->
            selectYear = year
            selectMonth = month + 1
            selectDay = day
            val ls = ArrayList<Task>()
            for(at in allTask){
                val fy = at.fy
                val fm = at.fm
                val fd = at.fd
                val ty = at.ty
                val tm = at.tm
                val td = at.td
                if(testDate(fy, fm, fd, ty, tm, td, selectYear, selectMonth, selectDay)) ls.add(at.task)
            }
            mselY = selectYear
            mselM = selectMonth
            mselD = selectDay
            adapter.mselectYear = mselY
            adapter.mselectMonth = mselM
            adapter.mselectDay = mselD
            adapter.setList(ls)
            readNote()
        }

        val addTask = findViewById<View>(R.id.add_task)
        addTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra("year", selectYear)
            intent.putExtra("month", selectMonth)
            intent.putExtra("day", selectDay)
            startActivity(intent)
        }
        readNote()
    }

    private fun readNote(){
        val auth = Firebase.auth
        val db = Firebase.firestore
        val id = auth.uid + mselY.toString() + mselM.toString() + mselD.toString()
        db.collection("notes").document(id).get().addOnCompleteListener { result ->
            val doc = result.result
            if(doc != null){
                val note = doc["text"] as String? ?: ""
                if(!note.isEmpty()){
                    adapter.setNote(note)
                }
            }
        }
    }

    private fun saveNote(){
        if(adapter.noteText.isEmpty()) return
        val auth = Firebase.auth
        val db = Firebase.firestore
        val id = auth.uid + mselY.toString() + mselM.toString() + mselD.toString()
        val note = hashMapOf(
            "text" to adapter.noteText
        )
        db.collection("notes").document(id).set(note as Map<String, Any>)

    }

    private fun testDate(fy: Int, fm: Int, fd: Int, ty: Int, tm: Int, td: Int, y: Int, m: Int, d: Int): Boolean{
//        if(ty >= y && y >= fy) {
//            if(tm >= m && m >= fm) {
//                if(td >= d && d >= fd) {
//                    return true
//                }
//            }
//        }
        val from = fy * 500 + fm * 31 + fd
        val to = ty * 500 + tm * 31 + td
        val day = y * 500 + m * 31 + d
        if(day >= from && day <=to) return true
        return false
    }

    private fun formatNum(num: Int): String {
        var ret = ""
        if(num < 10) ret = "0" + num.toString()
        else ret = num.toString()
        return ret
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val y = year.toString()
        val m = formatNum(month)
        val d = formatNum(day)
        return d + "." + m + "." + y
    }
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("serviceStatus", serviceBound)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("serviceStatus")
    }

    //Binding this Client to the AudioPlayer Service
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder: MediaPlayerService.LocalBinder = service as MediaPlayerService.LocalBinder
            player = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }
    private fun playAudio(path: String) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
//            val storage = StorageUtil(applicationContext)
//            storage.storeAudio(audioList)
//            storage.storeAudioIndex(audioIndex)
            Repository.audioText = "Планировщик"
            RepositoryAudio.activeAudio = Audio(path, "Планировщик", "", "")
            val playerIntent = Intent(this, MediaPlayerService::class.java)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, BIND_AUTO_CREATE)
        } else {
            //Store the new audioIndex to SharedPreferences
//            val storage = StorageUtil(applicationContext)
//            storage.storeAudioIndex(audioIndex)

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            RepositoryAudio.activeAudio = Audio(path, "", "", "")
            val broadcastIntent = Intent(MainActivity.Broadcast_PLAY_NEW_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
            //service is active
            //mHandler.
            player!!.stopSelf()
        }
        saveNote()
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Планировщик задач", this::class.java.simpleName)
    }
}