package com.app.road.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.app.road.R

class IntroTwoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_intro_two, container, false)
        val next = root.findViewById<View>(R.id.next)
        next.setOnClickListener {
            val two = IntroThreeFragment()
            val ft = requireFragmentManager().beginTransaction()
            ft.replace(R.id.fragment, two)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(null)
            ft.commit()
        }
        return root
    }
}