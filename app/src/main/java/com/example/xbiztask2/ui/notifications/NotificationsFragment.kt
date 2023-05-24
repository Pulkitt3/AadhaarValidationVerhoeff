package com.example.xbiztask2.ui.notifications

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xbiztask2.adapter.PinAdapter
import com.example.xbiztask2.databinding.FragmentNotificationsBinding
import com.example.xbiztask2.model.PostOfficeItem

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    lateinit var notificationsViewModel: NotificationsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        notificationsViewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.rvPin.layoutManager = LinearLayoutManager(requireContext())
        binding.btnSubmit.setOnClickListener {
            hideKeyBoard()
            if (TextUtils.isEmpty(binding.etPinNumber.text.toString())) {
                Toast.makeText(requireContext(), "Please Enter pincode", Toast.LENGTH_SHORT).show()
            } else if (binding.etPinNumber.text.toString().length < 6) {
                Toast.makeText(requireContext(),
                    "Please Enter 6 digits pincode",
                    Toast.LENGTH_SHORT).show()
            } else {
                binding.progressCircular.visibility = View.VISIBLE
                notificationsViewModel.getPinCode(binding.etPinNumber.text.toString())

            }
        }

        notificationsViewModel.getPinStates.observe(viewLifecycleOwner) {
            binding.progressCircular.visibility = View.GONE
            Log.d("TAG", "onCreateView: $it")
            if (it[0].status.equals("Success", true)) {
                binding.tvTotal.text = it[0].message
                binding.rvPin.visibility = View.VISIBLE
                binding.tvTotal.visibility = View.VISIBLE
                binding.textNotifications.visibility = View.GONE
                binding.rvPin.adapter = PinAdapter(it[0].postOffice)
            } else {
                binding.tvTotal.visibility = View.GONE
                binding.rvPin.visibility = View.GONE
                binding.textNotifications.visibility = View.VISIBLE
                binding.textNotifications.text = it[0].message
            }


        }
        return root
    }

    fun hideKeyBoard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}