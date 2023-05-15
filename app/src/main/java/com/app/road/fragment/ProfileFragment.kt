package com.app.road.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.*
import com.app.road.ui.SelectCourseActivity
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    fun cardViewColor(card: CardView, mode: Int){
        if(mode == 0){
            card.background.setTint(card.context.getColor(R.color.green_result))
        }else if(mode == 1){
            card.background.setTint(card.context.getColor(R.color.yellow_result))
        } else {
            card.background.setTint(card.context.getColor(R.color.red_result))
        }
    }

    fun setupNewCourse(root: View){
        val lichnost = root.findViewById<CardView>(R.id.lichnost)
        val zelaniay = root.findViewById<CardView>(R.id.zelaniay)
        val strategiay = root.findViewById<CardView>(R.id.strategiay)
        val auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("users").document(auth.currentUser!!.uid).get().addOnCompleteListener {
            val doc = it.result
            if(doc != null){
                val lich = doc["lich"] as Long? ?: 0L
                val zel = doc["zel"] as Long? ?: 0L
                val strat = doc["strat"] as Long? ?: 0L
                cardViewColor(lichnost, lich.toInt())
                cardViewColor(zelaniay, zel.toInt())
                cardViewColor(strategiay, strat.toInt())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Мой профиль", this::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val name = root.findViewById<TextView>(R.id.name)
        name.setOnClickListener {// test
            //startActivity(Intent(requireContext(), SelectCourseActivity::class.java))
        }
        val rating = root.findViewById<TextView>(R.id.rating)
        val buy = root.findViewById<View>(R.id.buy)
        val favorites = root.findViewById<View>(R.id.favorites)
        val selectCourse = root.findViewById<View>(R.id.select_course)
        val newcourse = root.findViewById<View>(R.id.newcourse)
        val tvPrice = root.findViewById<TextView>(R.id.tvPrice)

        val sharedPreferences = requireActivity().getSharedPreferences("road", Context.MODE_PRIVATE)
        val currentAuthor = sharedPreferences.getString("current_author", "")

        if(currentAuthor.equals("lena")) {
            tvPrice.text = "Купить курс за 3500 рублей"
        } else tvPrice.text = "Купить курс за 2500 рублей"

        if(Repository.selectLena){
            rating.visibility = View.GONE
            newcourse.visibility = View.VISIBLE
            setupNewCourse(root)
        } else {
            rating.visibility = View.VISIBLE
            newcourse.visibility = View.GONE
        }

        selectCourse.setOnClickListener {
            val intent = Intent(requireContext(), SelectCourseActivity::class.java)
            startActivity(intent)
        }

        val auth = Firebase.auth
        val db = Firebase.firestore
        name.text = Repository.name
        rating.text = Repository.rating + "/10"

        val feedback = root.findViewById<View>(R.id.feedback)
        feedback.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/the_happiest_millionaire"))
            startActivity(browserIntent)
        }

        val stat = root.findViewById<View>(R.id.stat)
        val statSeparator = root.findViewById<View>(R.id.view33)
        stat.setOnClickListener {
            startActivity(Intent(requireContext(), AutorActivity::class.java))
        }
        favorites.setOnClickListener {
            startActivity(Intent(requireContext(), FavoritesActivity::class.java))
 //           requireActivity().finish()
        }


        db.collection("users")
            .document(auth.currentUser!!.uid)
            .addSnapshotListener { value, error ->
                if(value != null && error == null) {
                    val doc = value
                    Repository.rating = doc["rating_finans"] as String? ?: "0.0"
                    Repository.name = doc["name"] as String? ?: ""
                    name.text = Repository.name
                    rating.text = Repository.rating + "/10"
                    val premium = doc["premium"] as Boolean? ?: false
                    if(premium){
                        buy.visibility = View.INVISIBLE
                    }
                    val isAutor= doc["is_author"] as Boolean? ?: false
                    if(isAutor){
                        stat.visibility = View.VISIBLE
                        statSeparator.visibility = View.VISIBLE
                    } else {
                        stat.visibility = View.GONE
                        statSeparator.visibility = View.GONE
                    }
                }

            }

        val exit = root.findViewById<View>(R.id.exit)
        exit.setOnClickListener {
            auth.signOut()
            requireActivity().finish()
        }
        auth.addAuthStateListener {
            if(auth.currentUser == null){
                try{
                    startActivity(Intent(requireContext(), SplashActivity::class.java))
                    requireActivity().finish()
                }
                catch (e: Exception){
                    //requireActivity().finish()
                }
            }
        }

        buy.setOnClickListener {
            val intent = Intent(requireContext(), SubscribeBaseActivity::class.java)
            intent.putExtra("only_year", true)
            startActivity(intent)
        }
        val edit = root.findViewById<View>(R.id.edit)
        edit.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
        val settings = root.findViewById<View>(R.id.settings)
        settings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
        val lessons = root.findViewById<View>(R.id.lessons)
        lessons.setOnClickListener {
            startActivity(Intent(requireContext(), LessonsActivity::class.java))
        }

        return root
    }
}