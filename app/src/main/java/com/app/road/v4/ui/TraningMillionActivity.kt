package com.app.road.v4.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.DiaryActivity
import com.app.road.activity.MeditationActivity
import com.app.road.activity.PlayMeditationActivity
import com.app.road.activity.SchedulerActivity
import com.app.road.v4.Utils
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TraningMillionActivity : AppCompatActivity() {
    private lateinit var block1: ConstraintLayout
    private lateinit var block2: ConstraintLayout
    private lateinit var block3: ConstraintLayout
    private lateinit var block4: ConstraintLayout
    private lateinit var block5: ConstraintLayout
    private lateinit var block6: ConstraintLayout
    private lateinit var block7: ConstraintLayout
    private lateinit var block8: ConstraintLayout
    private lateinit var block9: ConstraintLayout
    private lateinit var block10: ConstraintLayout
    private lateinit var block15: ConstraintLayout

    private lateinit var block9Check: CheckBox
    private lateinit var block7Check: CheckBox
    private lateinit var block1Check: CheckBox
    private lateinit var block2Check: CheckBox
    private lateinit var block3Check: CheckBox
    private lateinit var block10Check: CheckBox

    private fun genegateCollectionName():String {
        val day = Repository.dayMillion.toString()
        return "million" + day
    }

    private fun getCheckField(collection: String, block: String): String{
        return collection + "_" + block
    }
    private fun getCheckState(c: String) {
        val collection = genegateCollectionName()
        val db = Firebase.firestore
        val uid = Firebase.auth.uid!!
        db.collection("users").document(uid).get().addOnCompleteListener {
            val doc = it.result
            if (doc != null) {
                val b9 = doc[getCheckField(collection, "block9")] as Boolean? ?: false
                val b7 = doc[getCheckField(collection, "block7")] as Boolean? ?: false
                val b1 = doc[getCheckField(collection, "block1")] as Boolean? ?: false
                val b2 = doc[getCheckField(collection, "block2")] as Boolean? ?: false
                val b3 = doc[getCheckField(collection, "block3")] as Boolean? ?: false
                val b10 = doc[getCheckField(collection, "block10")] as Boolean? ?: false

                block9Check.isChecked = b9
                block7Check.isChecked = b7
                block1Check.isChecked = b1
                block2Check.isChecked = b2
                block3Check.isChecked = b3
                block10Check.isChecked = b10
            }
        }
    }
    private fun saveCheckState(c: String, block: String, state: Boolean){
        val collection = genegateCollectionName()
        val st = getCheckField(collection, block)
        val db = Firebase.firestore
        val uid = Firebase.auth.uid!!
        val user = hashMapOf(
            st to state
        )
        db.collection("users").document(uid).update(user as Map<String, Any>)
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Тренинг миллиона", this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traning_million)
        val mode = intent.getIntExtra("mode", 0)
        val trial = findViewById<TextView>(R.id.trial)
        val back = findViewById<View>(R.id.back)
        val mode_ui = findViewById<TextView>(R.id.mode)
        val day_ui = findViewById<TextView>(R.id.day)
        val name = findViewById<TextView>(R.id.name)
        var collection = intent.getStringExtra("collection") ?: "ml"
        name.text = Repository.name + "!"
        day_ui.text = Repository.dayMillion.toString()
        block1 = findViewById(R.id.block1)
        block2 = findViewById(R.id.block2)
        block3 = findViewById(R.id.block3)
        block4 = findViewById(R.id.block4)
        block5 = findViewById(R.id.block5)
        block6 = findViewById(R.id.block6)
        block7 = findViewById(R.id.block7)
        block8 = findViewById(R.id.block8)
        block9 = findViewById(R.id.block9)
        block10 = findViewById(R.id.block10)
        block15 = findViewById(R.id.block15)

        block9Check = findViewById(R.id.block9_check)
        block7Check = findViewById(R.id.block7_check)
        block1Check = findViewById(R.id.block1_check)
        block2Check = findViewById(R.id.block2_check)
        block3Check = findViewById(R.id.block3_check)
        block10Check = findViewById(R.id.block10_check)

        block7.setOnClickListener {
            val intent = Intent(this, SchedulerActivity::class.java)
            startActivity(intent)
        }

        getCheckState(collection)
        block9Check.setOnCheckedChangeListener { compoundButton, b ->
            saveCheckState(collection, "block9", b)
        }
        block7Check.setOnCheckedChangeListener { compoundButton, b ->
            saveCheckState(collection, "block7", b)
        }
        block1Check.setOnCheckedChangeListener { compoundButton, b ->
            saveCheckState(collection, "block1", b)
        }
        block2Check.setOnCheckedChangeListener { compoundButton, b ->
            saveCheckState(collection, "block2", b)
        }
        block3Check.setOnCheckedChangeListener { compoundButton, b ->
            saveCheckState(collection, "block3", b)
        }
        block10Check.setOnCheckedChangeListener { compoundButton, b ->
            saveCheckState(collection, "block10", b)
        }
        hideAllBlocks()
        when(mode){
            0 -> {
                mode_ui.text = "Утренние упражнения"
                setupMorning()
            }
            1 -> {
                mode_ui.text = "Теория и задания"
                setupDay()
            }
            2 -> {
                mode_ui.text = "Упражнения перед сном"
                setupEvening()
            }
        }
        if(Repository.trialMillion) trial.visibility = View.VISIBLE
        else trial.visibility = View.INVISIBLE
        back.setOnClickListener {
            finish()
        }
        val eye1 = findViewById<View>(R.id.eye1)
        val info1 = findViewById<View>(R.id.info1)
        val text1 = findViewById<TextView>(R.id.text1)
        val eye2 = findViewById<View>(R.id.eye2)
        val info2 = findViewById<View>(R.id.info2)
        val text2 = findViewById<TextView>(R.id.text2)
        val eye3 = findViewById<View>(R.id.eye3)
        val info3 = findViewById<View>(R.id.info3)
        val text3 = findViewById<TextView>(R.id.text3)
        val eye7 = findViewById<View>(R.id.eye17)
        val info7 = findViewById<View>(R.id.info17)
        val text7 = findViewById<TextView>(R.id.text17)
        val eye8 = findViewById<View>(R.id.eye18)
        val info8 = findViewById<View>(R.id.info18)
        val text8 = findViewById<TextView>(R.id.text18)
        val eye89 = findViewById<View>(R.id.eye189)
        val info89 = findViewById<View>(R.id.info189)
        val text89 = findViewById<TextView>(R.id.text189)
        val eye88 = findViewById<View>(R.id.eye188)
        val info88 = findViewById<View>(R.id.info188)
        val text88 = findViewById<TextView>(R.id.text188)
        val eye85 = findViewById<View>(R.id.eye185)
        val info85 = findViewById<View>(R.id.info185)
        val text85 = findViewById<TextView>(R.id.text185)
        val eye9 = findViewById<View>(R.id.eye19)
        val info9 = findViewById<View>(R.id.info19)
        val text9 = findViewById<TextView>(R.id.text19)
        val eye10 = findViewById<View>(R.id.eye110)
        val info10 = findViewById<View>(R.id.info110)
        val text10 = findViewById<TextView>(R.id.text110)
        info1.visibility = View.GONE
        info2.visibility = View.GONE
        info3.visibility = View.GONE
        info7.visibility = View.GONE
        info8.visibility = View.GONE
        info89.visibility = View.GONE
        info88.visibility = View.GONE
        info9.visibility = View.GONE
        info10.visibility = View.GONE
        eye1.setOnClickListener {
            eye1.visibility = View.INVISIBLE
            info1.visibility = View.VISIBLE
            text1.maxLines = 100
        }
        eye2.setOnClickListener {
            eye2.visibility = View.INVISIBLE
            info2.visibility = View.VISIBLE
            text2.maxLines = 100
        }
        eye3.setOnClickListener {
            eye3.visibility = View.INVISIBLE
            info3.visibility = View.VISIBLE
            text3.maxLines = 100
        }
        eye7.setOnClickListener {
            eye7.visibility = View.INVISIBLE
            info7.visibility = View.VISIBLE
            text7.maxLines = 100
        }
        eye8.setOnClickListener {
            eye8.visibility = View.INVISIBLE
            info8.visibility = View.VISIBLE
            text8.maxLines = 100
        }
        eye89.setOnClickListener {
            eye89.visibility = View.INVISIBLE
            info89.visibility = View.VISIBLE
            text89.maxLines = 100
        }
        eye88.setOnClickListener {
            eye88.visibility = View.INVISIBLE
            info88.visibility = View.VISIBLE
            text88.maxLines = 100
        }
        eye85.setOnClickListener {
            eye85.visibility = View.INVISIBLE
            info85.visibility = View.VISIBLE
            text85.maxLines = 100
        }
        eye9.setOnClickListener {
            eye9.visibility = View.INVISIBLE
            info9.visibility = View.VISIBLE
            text9.maxLines = 100
        }
        eye10.setOnClickListener {
            eye10.visibility = View.INVISIBLE
            info10.visibility = View.VISIBLE
            text10.maxLines = 100
        }

        info1.setOnClickListener {
            eye1.visibility = View.VISIBLE
            info1.visibility = View.INVISIBLE
            text1.maxLines = 3
        }
        info2.setOnClickListener {
            eye2.visibility = View.VISIBLE
            info2.visibility = View.INVISIBLE
            text2.maxLines = 3
        }
        info3.setOnClickListener {
            eye3.visibility = View.VISIBLE
            info3.visibility = View.INVISIBLE
            text3.maxLines = 3
        }
        info7.setOnClickListener {
            eye7.visibility = View.VISIBLE
            info7.visibility = View.INVISIBLE
            text7.maxLines = 3
        }
        info8.setOnClickListener {
            eye8.visibility = View.VISIBLE
            info8.visibility = View.INVISIBLE
            text8.maxLines = 3
        }
        info89.setOnClickListener {
            eye89.visibility = View.VISIBLE
            info89.visibility = View.INVISIBLE
            text89.maxLines = 3
        }
        info88.setOnClickListener {
            eye88.visibility = View.VISIBLE
            info88.visibility = View.INVISIBLE
            text88.maxLines = 3
        }
        info85.setOnClickListener {
            eye85.visibility = View.VISIBLE
            info85.visibility = View.INVISIBLE
            text85.maxLines = 3
        }
        info9.setOnClickListener {
            eye9.visibility = View.VISIBLE
            info9.visibility = View.INVISIBLE
            text9.maxLines = 3
        }
        info10.setOnClickListener {
            eye10.visibility = View.VISIBLE
            info10.visibility = View.INVISIBLE
            text10.maxLines = 3
        }
        block4.setOnClickListener {
            val intent = Intent(this, MeditationActivity::class.java)
            intent.putExtra("mode", mode)
            intent.putExtra("collection", collection)
            startActivity(intent)
        }
        block6.setOnClickListener {
            val intent = Intent(this, MeditationActivity::class.java)
            intent.putExtra("mode", mode)
            intent.putExtra("collection", collection)
            startActivity(intent)
        }
        block10.setOnClickListener {
//            val intent = Intent(this, MeditationActivity::class.java)
//            intent.putExtra("mode", mode)
//            intent.putExtra("collection", collection)
//            startActivity(intent)
            val intent = Intent(this, PlayMeditationActivity::class.java)
            intent.putExtra("link", "http://mybestway.ru/music/1.mp3")
            intent.putExtra("title", "Музыка для сна")
            intent.putExtra("duration", "3:09:34")
            startActivity(intent)

        }
        block8.setOnClickListener {
            val intent = Intent(this, MeditationActivity::class.java)
            intent.putExtra("mode", mode)
            intent.putExtra("collection", collection)
            startActivity(intent)
        }
        block5.setOnClickListener {
            val intent = Intent(this, MeditationActivity::class.java)
            intent.putExtra("mode", 10)
            collection = genCollection()
            intent.putExtra("collection", collection)
            startActivity(intent)
        }
        block15.setOnClickListener {
            startActivity(Intent(this, DiaryActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnFinishAction).setOnClickListener {
            finish()
        }
    }
    private fun hideAllBlocks(){
        block1.visibility = View.GONE
        block2.visibility = View.GONE
        block3.visibility = View.GONE
        block4.visibility = View.GONE
        block5.visibility = View.GONE
        block6.visibility = View.GONE
        block7.visibility = View.GONE
        block8.visibility = View.GONE
        block9.visibility = View.GONE
        block10.visibility = View.GONE
        block15.visibility = View.GONE
    }

    private fun setupMorning(){
        block1.visibility = View.VISIBLE
        block2.visibility = View.VISIBLE
        block3.visibility = View.VISIBLE
        block4.visibility = View.VISIBLE
        block5.visibility = View.VISIBLE
    }

    private fun setupDay(){
        block6.visibility = View.VISIBLE
    }
    private fun setupEvening(){
        block7.visibility = View.VISIBLE
        block8.visibility = View.VISIBLE
        block9.visibility = View.VISIBLE
        block10.visibility = View.VISIBLE
        block15.visibility = View.VISIBLE
        block5.visibility = View.VISIBLE
    }
    private fun genCollection(): String {
        var c = "finansa1"
        var d = Repository.dayMillion
            c = if(d == 1L) "starta"
            else if(d == 2L) "starta2"
            else if(d == 3L) "starta3"
            else if(d == 4L) "starta4"
            else if(d == 5L) "starta5"
            else if(d == 6L) "starta6"
            else if(d == 7L) "starta7"
            else if(d == 8L) "starta8"
            else if(d < 13) "finansa1"
            else if(d < 17) "finansa2"
            else if(d < 21) "finansa3"
            else if(d < 25) "finansa4"
            else if(d < 29) "finansa5"
            else if(d < 33) "finansa6"
            else "finansa7"
        return c
    }
}