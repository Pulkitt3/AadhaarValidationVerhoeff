package com.example.xbiztask2.ui.home

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.xbiztask2.databinding.FragmentHomeBinding
import com.example.xbiztask2.model.AadhaarUtils
import java.util.regex.Pattern


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.btnSubmit.setOnClickListener {
            if (TextUtils.isEmpty(binding.etAadhaar.text.toString().trim())) {
                Toast.makeText(requireContext(), "Please enter raw text", Toast.LENGTH_SHORT).show()
            } else {
                AadhaarUtils.findAadhaar(binding.etAadhaar.text.toString(), binding.textAadhaar)

            }
        }
        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}