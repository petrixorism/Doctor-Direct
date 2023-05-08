package uz.ravshanbaxranov.doctordirect.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentOnboardingChildBinding

class OnboardingChildFragment : Fragment(R.layout.fragment_onboarding_child) {

    private val binding by viewBinding(FragmentOnboardingChildBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf {
            it.containsKey("object")
        }?.apply {
            when (getInt("object")) {
                0 -> {
                    binding.contentIv.setAnimation(R.raw.doctor_search)
                    binding.titleTv.setText(R.string.title1)
                    binding.subtitleTv.setText(R.string.subtitle1)
                }

                1 -> {
                    binding.contentIv.setAnimation(R.raw.doctor_chat)
                    binding.titleTv.setText(R.string.title2)
                    binding.subtitleTv.setText(R.string.subtitle2)
                }

                2 -> {
                    binding.contentIv.setAnimation(R.raw.doctor_types)
                    binding.titleTv.setText(R.string.title3)
                    binding.subtitleTv.setText(R.string.subtitle3)
                }
            }


        }
    }

}