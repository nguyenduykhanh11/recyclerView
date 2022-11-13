package com.example.doanadroid.ui.activities

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.doanadroid.R
import com.example.doanadroid.databinding.ActivityMainBinding
import com.example.doanadroid.model.entity.CalendarEntity
import com.example.doanadroid.viewModel.CalendarViewModel
import com.example.doanadroid.viewModel.ObseverViewModel
import com.example.todolist.utils.Constants
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    private val mCalendarViewModel: CalendarViewModel by lazy { ViewModelProvider(this).get(CalendarViewModel::class.java) }
    private val navGraph by lazy{ Navigation.findNavController(this, R.id.host_fragment) }
    private val mObsevedViewModel: ObseverViewModel by viewModels()
    private var checkStatusBottomNavigation =  0;
    private var checkEnbleMission = ""
    private var id: Int? = null
    private var name = ""
    private var day = ""
    private var time = ""
    private var category = ""
    private var complete = ""
    private var statusSave = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpView()
        setUpViewNavigationDrawer()
        setUpNavigationBackStack()
        setUpClickFloatBottomAddOrSave()
        setUponBackPresseDispatcher()
        setUpdateItemMission()
    }

    private fun arrowBack() {
        mObsevedViewModel.nameMission.observe(this){ checkEnbleMission = it }
        if(checkEnbleMission.trim() != ""){
            AlertDialog.Builder(this).apply {
                setTitle("Bạn có chắc chắn?")
                setMessage("Thoát mà không lưu?")
                setPositiveButton("Vâng", DialogInterface.OnClickListener { dialogInterface, i ->
                    mObsevedViewModel.nameMission.value = ""
                    dialogInterface.dismiss()
                    eventSettingBackStack()
                })
                setNegativeButton("Hủy bỏ", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                create()
                show()
            }
        }
        else {
            eventSettingBackStack()
        }
    }

//    hàm xữ lý khi người dùng back ở máy
    private fun setUponBackPresseDispatcher() {
        onBackPressedDispatcher.addCallback(this) {
            when(checkStatusBottomNavigation){
                Constants.ACTION_ADD_SAVE -> arrowBack()
                Constants.ACTION_UPDATE_SAVE -> arrowBack()
                else ->finish()
            }
        }
    }
//    hàm show view Navigation Drawer
    private fun setUpViewNavigationDrawer() {
        binding.navView.setNavigationItemSelectedListener(this)
        val drawerToggle = ActionBarDrawerToggle(
            this,
            binding.layoutDrawer,
            binding.bottomAppBar,
            R.string.open_drawer,
            R.string.close_drawer)
        binding.layoutDrawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    //        buttom chuyển đên fragment add missuon
    private fun setUpClickFloatBottomAddOrSave() {
        binding.floatingActionButton.setOnClickListener {
            handleViewBottomAppbar()
        }
    }

//    hàm check và xữ lý view cho bottomAppbar and Navigation Icon Toolbar
    private fun handleViewBottomAppbar() {
        when (checkStatusBottomNavigation) {
            Constants.ACTION_ADD -> {
                eventSettingAddMission()
            }
            Constants.ACTION_ADD_SAVE -> {
                setUpSaveAddOrSaveUpdate(Constants.STATUS_SAVE_ADD)
            }
            Constants.ACTION_UPDATE_SAVE -> {
                setUpSaveAddOrSaveUpdate(Constants.STATUS_SAVE_UPDATE)
            }
        }
    }

//    Hàm xữ lý chuyển màn đến fragment update
    private fun setUpdateItemMission() {
        mObsevedViewModel.statusBottomApp.observe(this){
            if (it == Constants.STATUS_UPDATE){
                checkStatusBottomNavigation = Constants.ACTION_UPDATE_SAVE
                binding.apply {
                    bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                    floatingActionButton.setImageDrawable(resources.getDrawable(R.drawable.ic_save))
                    toolbar.navigationIcon= resources.getDrawable(R.drawable.ic_back)
                }
            }
        }

    }

    private fun setUpSaveAddOrSaveUpdate(save : String) {
        mObsevedViewModel.statusBottomApp.value = save
        mObsevedViewModel.nameMission.observe(this@MainActivity){
            checkEnbleMission = it
        }
        if (String.format(checkEnbleMission) != "") {
            Snackbar.make(binding.layoutDrawer,"Thêm Nhiệm Vụ Thành Công", Snackbar.LENGTH_LONG)
                .setDuration(10000)
                .setAnchorView(binding.floatingActionButton)
                .show()
                setUpSaveData()
            eventSettingBackStack()
        }else{
            Snackbar.make(binding.layoutDrawer,"Bạn Chưa nhập Nhiệm vụ", Snackbar.LENGTH_LONG)
                .setDuration(10000)
                .setAnchorView(binding.floatingActionButton)
                .show()
        }
    }

    private fun setUpSaveData() {
        with(mObsevedViewModel){
            idMission.observe(this@MainActivity){
                id = it
            }
            nameMission.observe(this@MainActivity){
                name = it
            }
            dayMission.observe(this@MainActivity) {
                day = it
            }
            timeMission.observe(this@MainActivity) {
                time = it
            }
            categoryMission.observe(this@MainActivity){
                category = it
            }
            completeMission.observe(this@MainActivity){
                complete = it
            }
        }
        mObsevedViewModel.statusBottomApp.observe(this){
            statusSave = it
        }
        when(statusSave){
            Constants.STATUS_SAVE_ADD -> {
                mCalendarViewModel.insertCalendar(CalendarEntity(null, name, day, time, category, complete))
            }
            Constants.STATUS_SAVE_UPDATE -> {
                mCalendarViewModel.updateCalendar(CalendarEntity(id, name, day, time, category, complete))
            }
        }
    }

    //    hàm sử lý sự kiện backStack
    private fun eventSettingBackStack() {
        Log.d("this", checkStatusBottomNavigation.toString())
        checkStatusBottomNavigation = 0
        navGraph.popBackStack()
        binding.apply {
            toolbar.navigationIcon=null
            bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            floatingActionButton.setImageDrawable(resources.getDrawable(R.drawable.ic_add))
        }
    }

//    hàm sử lý Click nút add
    private fun eventSettingAddMission() {
        navGraph.navigate(R.id.addFragment)
        checkStatusBottomNavigation = 1
        mObsevedViewModel.statusBottomApp.value = Constants.STATUS_ADD
        binding.apply {
            bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
            floatingActionButton.setImageDrawable(resources.getDrawable(R.drawable.ic_save))
            toolbar.navigationIcon= resources.getDrawable(R.drawable.ic_back)
        }
    }

//    hàm xữ lý sự kiện navigationToolbar click
    private fun setUpNavigationBackStack() {
        binding.toolbar.setNavigationOnClickListener {
            when(checkStatusBottomNavigation){
                1 -> mObsevedViewModel.statusBottomApp.value = Constants.STATUS_SAVE_ADD
                2 -> mObsevedViewModel.statusBottomApp.value = Constants.STATUS_SAVE_UPDATE
                else -> {}
            }
            arrowBack()
        }
    }


//    setUpView all fragment in Navigation
    private fun setUpView() {
        setSupportActionBar(binding.toolbar)
        setUpListenerClickItemDrawer(R.string.list_all, R.drawable.ic_home)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.sub_list_all -> {
                setUpListenerClickItemDrawer(R.string.list_all, R.drawable.ic_home)
                setUpEventClickItemDrawer(Constants.LIST_ALL)
                true
            }
            R.id.sub_default -> {
                setUpListenerClickItemDrawer(R.string.defaul_t, R.drawable.ic_category_list)
                setUpEventClickItemDrawer(Constants.DEFAULT)
                true
            }
            R.id.sub_individual -> {
                setUpListenerClickItemDrawer(R.string.individual, R.drawable.ic_category_list)
                setUpEventClickItemDrawer(Constants.INDIVIDUAL)
                true
            }
            R.id.sub_work -> {
                setUpListenerClickItemDrawer(R.string.work, R.drawable.ic_category_list)
                setUpEventClickItemDrawer(Constants.WORK)
                true
            }
            R.id.sub_shopping -> {
                setUpListenerClickItemDrawer(R.string.shopping, R.drawable.ic_category_list)
                setUpEventClickItemDrawer(Constants.SHOPPING)
                true
            }
            R.id.sub_complete -> {
                setUpListenerClickItemDrawer(R.string.complete, R.drawable.ic_complete)
                setUpEventClickItemDrawer(Constants.COMPLETE)
                true
            }
            else -> false
        }
    }

    private fun setUpEventClickItemDrawer(actionItem: String) {
        mObsevedViewModel.ActionDrawerItem.value = actionItem
    }

    private fun setUpListenerClickItemDrawer(titlee: Int, icon: Int) {
        title = " "+resources.getString(titlee)
        binding.toolbar.logo = resources.getDrawable(icon)
        if (binding.layoutDrawer.isDrawerOpen(GravityCompat.START)) {
            binding.layoutDrawer.closeDrawer(GravityCompat.START);
        }
    }
}