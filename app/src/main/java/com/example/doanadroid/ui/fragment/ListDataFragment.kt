package com.example.doanadroid.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doanadroid.R
import com.example.doanadroid.adapter.CalendarAdapter
import com.example.doanadroid.databinding.FragmentListDataBinding
import com.example.doanadroid.model.entity.CalendarEntity
import com.example.doanadroid.model.entity.logByName
import com.example.doanadroid.viewModel.CalendarViewModel
import com.example.doanadroid.viewModel.ObseverViewModel
import com.example.todolist.utils.Constants

class ListDataFragment : Fragment() {

    private lateinit var binding: FragmentListDataBinding
    private val mCalendarAdapter: CalendarAdapter by lazy {
        CalendarAdapter { show ->
            showMenuOnlongClick(
                show
            )
        }
    }
    private var mainMenu: Menu? = null
    private val mCalendarViewModel: CalendarViewModel by lazy {
        ViewModelProvider(this).get(
            CalendarViewModel::class.java
        )
    }
    private val navGraph by lazy {
        Navigation.findNavController(
            requireActivity(),
            R.id.host_fragment
        )
    }
    private val mObsevedViewModel: ObseverViewModel by activityViewModels()
    private var checkCalendarComplete = ""
    private var check_Current_Position = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpView()
        getDataFollowItemDrawer()
        setListenerOnClickItem()
        setListenerOnLongClickItem()
        setUpListenerCheckedBox()
        deleteItemAdapter()

    }

    private fun setUpListenerCheckedBox() {
        mCalendarAdapter.checkedItem = { calendar ->
            if (calendar.complete == Constants.NO_YET){
                mCalendarViewModel.updateCalendar(
                    CalendarEntity(calendar.id, calendar.name, calendar.day, calendar.time, calendar.category, Constants.COMPLETE))
                Log.d("this", check_Current_Position)
//                checkLocation(check_Current_Position)

            }else{
                mCalendarViewModel.updateCalendar(
                    CalendarEntity(calendar.id, calendar.name, calendar.day, calendar.time, calendar.category, Constants.NO_YET))
                Log.d("this", check_Current_Position)
//                checkLocation(check_Current_Position)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        mainMenu = menu
        inflater.inflate(R.menu.menu_long_item, menu)
        showMenuOnlongClick(false)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //    set up VIsible cho menu
    private fun showMenuOnlongClick(show: Boolean) {
        if (checkCalendarComplete == Constants.COMPLETE) {
            mainMenu?.findItem(R.id.menu_checkbox)?.isVisible = show
            mainMenu?.findItem(R.id.menu_share)?.isVisible = show
            mainMenu?.findItem(R.id.menu_delete)?.isVisible = show
        } else if (checkCalendarComplete == Constants.NO_YET) {
            mainMenu?.findItem(R.id.menu_checkbox_complete)?.isVisible = show
            mainMenu?.findItem(R.id.menu_share)?.isVisible = show
            mainMenu?.findItem(R.id.menu_delete)?.isVisible = show
        }
    }

    private fun getDataFollowItemDrawer() {
        mObsevedViewModel.ActionDrawerItem.observe(viewLifecycleOwner) { item ->
            check_Current_Position = item
            Log.d("thiss", check_Current_Position)
            checkLocation(check_Current_Position)
        }
    }

    private fun setListenerOnClickItem() {
        mCalendarAdapter.onClickItem = { calendar ->
            val bundel = Bundle()
            bundel.putParcelable(Constants.BUNDEL_SEND_CALENDAR, calendar)
            mObsevedViewModel.statusBottomApp.value = Constants.STATUS_UPDATE
            navGraph.navigate(R.id.updateFragment, bundel)
        }
    }

    private fun setListenerOnLongClickItem() {
        mCalendarAdapter.onLongClickItem = { calendar ->
            checkCalendarComplete = calendar.complete.toString()
//            val bundel = Bundle()
//            bundel.putParcelable(Constants.BUNDEL_SEND_CALENDAR , calendar)
//            mObsevedViewModel.statusBottomApp.value = Constants.STATUS_UPDATE
//            navGraph.navigate(R.id.updateFragment, bundel)
        }
    }

    private fun setUpView() {
        with(mCalendarViewModel) {
            readAllData.observe(viewLifecycleOwner) { calendarList ->
                calendarList.logByName(screen = "ListDataFragment 0")
                mCalendarAdapter.setCalendar(ArrayList(calendarList))
            }
            check_Current_Position = Constants.LIST_ALL
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mCalendarAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                deleteListItem()
                getDataFollowItemDrawer()
                true
            }
            R.id.menu_share -> {
                Toast.makeText(this.requireContext(), "share", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_checkbox -> {
                selectCheckBox()
                true
            }
            R.id.menu_checkbox_complete -> {
                unCheckbox()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun selectCheckBox() {
        TODO("Not yet implemented")
    }

    private fun unCheckbox() {
        TODO("Not yet implemented")
    }

    //    hàm hiện thông báo khi click itemMenu delete
    private fun deleteListItem() {
        AlertDialog.Builder(this.requireContext()).run {
            setTitle("Bạn có chắc chắn?")
            setMessage("Xóa nhiệm vụ?")
            setPositiveButton("Vâng") { _, _ ->
                mCalendarAdapter.deleteSelecterItem()
                showMenuOnlongClick(false)
            }
            setNegativeButton("Hủy bỏ") { _, _ -> }
            show()
        }
    }

    //    Hàm xoá các item khi click vào itemMenu delete
    private fun deleteItemAdapter() {
        mCalendarAdapter.sendItemDelete = {
            mCalendarViewModel.deleteCalendar(it)
        }
    }
    private fun checkLocation(category: String?) {
        when(category){
            Constants.LIST_ALL ->{
                setUpView()
            }
            Constants.COMPLETE ->{

                mCalendarViewModel.readAllDataComplete.observe(viewLifecycleOwner) { calendar ->
                    calendar.logByName(screen = "ListDataFragment 1")
                    mCalendarAdapter.setCalendar(ArrayList(calendar))
                }
            }
            else->{
                mCalendarViewModel.readCategory(category!!).observe(viewLifecycleOwner) { calendar ->
                    calendar.logByName(screen = "ListDataFragment 2")
                    mCalendarAdapter.setCalendar(ArrayList(calendar))
                }
            }
        }
    }
}