package com.example.doanadroid.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.doanadroid.R
import com.example.doanadroid.databinding.FragmentListDataBinding
import com.example.doanadroid.databinding.FragmentUpdateBinding
import com.example.doanadroid.model.entity.CalendarEntity
import com.example.doanadroid.viewModel.ObseverViewModel
import com.example.todolist.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class UpdateFragment : Fragment() {
    private lateinit var binding: FragmentUpdateBinding
    private val observerViewModel: ObseverViewModel by activityViewModels()
    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewUpdate()
        setUpListenerSaveUpdate()
    }

    private fun setUpViewUpdate() {
        arguments?.getParcelable<CalendarEntity>(Constants.BUNDEL_SEND_CALENDAR).let { calendar->
            with(binding){
                id = calendar?.id
                txtMission.setText(calendar?.name)
                txtDay.setText(calendar?.day)
                textTime.setText(calendar?.time)
                autoCompleteTextView.setText(calendar?.category)
            }
        }
    }

    private fun setUpListenerSaveUpdate() {
        observerViewModel.statusBottomApp.observe(viewLifecycleOwner){
            if (it == Constants.STATUS_SAVE_UPDATE){
                observerViewModel.idMission.value = id
                observerViewModel.nameMission.value=binding.txtMission.text.toString()
                observerViewModel.dayMission.value = binding.txtDay.text.toString()
                observerViewModel.timeMission.value = binding.textTime.text.toString()
                observerViewModel.categoryMission.value = binding.autoCompleteTextView.text.toString()
                observerViewModel.completeMission.value = Constants.NO_YET
            }
        }
        setValueDropDownMenu()
    }
    private fun setValueDropDownMenu() {
        val items = listOf(Constants.DEFAULT,Constants.INDIVIDUAL, Constants.WORK, Constants.SHOPPING)
        (binding.tilCategory.editText as? AutoCompleteTextView)?.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, items))
    }
//
//    binding.imgAddDown.setOnClickListener {
//        val calendar = Calendar.getInstance().also {
//            it.timeInMillis
//            Log.d("this", it.timeInMillis.toString())
//            val date = Date(it.timeInMillis)
//            val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
//            Log.d("this", format.format(date).toString())
//        }
//    }


}