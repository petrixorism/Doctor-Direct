package uz.ravshanbaxranov.doctordirect.presentation.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.ravshanbaxranov.doctordirect.presentation.screen.OnboardingChildFragment


class OnboardingPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {

        val fragment = OnboardingChildFragment()
        fragment.arguments = Bundle().apply {
            putInt("object", position)
        }
        return fragment
    }


}