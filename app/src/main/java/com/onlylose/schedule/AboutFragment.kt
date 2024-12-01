package com.onlylose.schedule

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.onlylose.schedule.databinding.FragmentAboutBinding
import com.onlylose.schedule.databinding.FragmentSettingsBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.contBut1.setOnClickListener {
            val tgUri = "https://t.me/onlyl0se"
            val uri = Uri.parse(tgUri)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/onlyl0se"))
                startActivity(browserIntent)
            }
        }

        binding.contBut2.setOnClickListener {
            val vkUrl = "https://vk.com/onlylosee"
            val uri = Uri.parse(vkUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(vkUrl))
                startActivity(browserIntent)
            }
        }

        binding.contBut3.setOnClickListener {
            val githubUrl = "https://github.com/onlylosee"
            val uri = Uri.parse(githubUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                startActivity(browserIntent)
            }
        }

        binding.contBut4.setOnClickListener {
            val email = "youremail@example.com"
            val subject = "Hello"
            val uri = Uri.parse("mailto:$email?subject=$subject")

            val intent = Intent(Intent.ACTION_SENDTO, uri)

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com/mail/?view=cm&fs=1&to=$email&su=$subject"))
                startActivity(browserIntent)
            }
        }
    }
}