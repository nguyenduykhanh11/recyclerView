package com.example.doanadroid.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ObseverViewModel:ViewModel() {
//    Định vị BottomAppbar
    var statusBottomApp = MutableLiveData<String>()

//    Lưu dữ liệu Calendar
    var idMission = MutableLiveData<Int>()
    var nameMission = MutableLiveData<String>()
    var dayMission = MutableLiveData<String>()
    var timeMission = MutableLiveData<String>()
    var categoryMission = MutableLiveData<String>()
    var completeMission = MutableLiveData<String>()

//    Listener click Item drawer layout
    var ActionDrawerItem = MutableLiveData<String>()
}