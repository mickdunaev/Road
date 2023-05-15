package com.app.road.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.app.road.R

class IntroOneFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_intro_one, container, false)
        val next = root.findViewById<View>(R.id.next)
        next.setOnClickListener {
            val two = IntroTwoFragment()
            val ft = requireFragmentManager().beginTransaction()
            ft.replace(R.id.fragment, two)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            //ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
            ft.addToBackStack(null)
            ft.commit()
        }
        return root
    }

}