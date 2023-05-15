package com.app.road.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import com.app.road.R
import com.app.road.activity.AgreemenActivity
import com.app.road.activity.LoginActivity
import com.google.android.material.button.MaterialButton

class IntroThreeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_intro_three, container, false)
        val next = root.findViewById<MaterialButton>(R.id.btnNext)
        val check = root.findViewById<CheckBox>(R.id.checkBox)
        next.setOnClickListener {
            if(check.isChecked){
                requireActivity().startActivity(Intent(activity, LoginActivity::class.java))
                requireActivity().finish()
            } else {
                Toast.makeText(activity,"Вам необходимо принять условия пользования",Toast.LENGTH_SHORT).show();
            }
        }

        val agreemen = root.findViewById<View>(R.id.agreemen)
        agreemen.setOnClickListener {
            requireActivity().startActivity(Intent(activity, AgreemenActivity::class.java))
        }
        return root
    }

 }