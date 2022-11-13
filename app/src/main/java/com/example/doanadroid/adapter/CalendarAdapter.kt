package com.example.doanadroid.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doanadroid.R
import com.example.doanadroid.databinding.ListDataItemBinding
import com.example.doanadroid.model.entity.CalendarEntity
import com.example.todolist.utils.Constants

class CalendarAdapter(private val showMenuOnlongClick: (Boolean) -> Unit): RecyclerView.Adapter<CalendarAdapter.ViewHolder>(){
    lateinit var onClickItem : ((CalendarEntity) ->Unit)
    lateinit var onLongClickItem : ((CalendarEntity) ->Unit)
    lateinit var checkedItem : ((CalendarEntity) ->Unit)
    lateinit var sendItemDelete : ((CalendarEntity) ->Unit)
    var calendarList = mutableListOf<CalendarEntity>()
    private var listOfLongItem = mutableListOf<CalendarEntity>()

    inner class ViewHolder(var binding: ListDataItemBinding):RecyclerView.ViewHolder(binding.root)


    fun setCalendar(newList: List<CalendarEntity>){
        this.calendarList.clear()
        this.calendarList = mutableListOf(*newList.toTypedArray())
//        this.calendarList.toMutableList().addAll(newList)
//        this.calendarList.addAll(newList)
        Log.d("this", newList.size.toString() + " " + calendarList.toString())
//        this.calendarList.addAll(listCalendar)
//        listCalendar.size
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListDataItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setUpViewItem(holder, position)
        checkOnclickItem(holder, position)
        setOnLongClickItem(holder, position)
        setUpListenerCheckBoxItem(holder, position)
    }

    private fun setUpViewItem(holder: CalendarAdapter.ViewHolder, position: Int) {
        with(holder.binding){
            tvMission.text = calendarList[position].name
            tvDayTime.text = calendarList[position].day +" "+calendarList[position].time
            tvCategory.text = calendarList[position].category
            if (calendarList[position].complete == Constants.COMPLETE){
                chkMission.isChecked = true
            }
            else{
                chkMission.isChecked = false
            }
            holder.binding.recyclerViewBackground.setBackgroundResource(R.drawable.bg_color_nomal)
        }
    }

    private fun setUpListenerCheckBoxItem(holder: CalendarAdapter.ViewHolder, position: Int) {
        holder.binding.chkMission.setOnClickListener{
            holder.binding.chkMission.isChecked = false
            checkedItem.invoke(calendarList[position])
        }
    }

    private fun checkOnclickItem(holder: CalendarAdapter.ViewHolder, position: Int) {
        holder.binding.cardClickChange.setOnClickListener {
            Log.d("this", listOfLongItem.size.toString())
            if (listOfLongItem.size == 0) {
                onClickItem.invoke(calendarList[position])
            }
            else{
                checkedItemLongClick(holder, position)
            }
        }
    }

    private fun checkedItemLongClick(holder: CalendarAdapter.ViewHolder, position: Int) {
        if (listOfLongItem.contains(calendarList[position])) {
            listOfLongItem.remove(calendarList[position])
            holder.binding.recyclerViewBackground.setBackgroundResource(R.drawable.bg_color_nomal)
            if(listOfLongItem.size == 0){
                showMenuOnlongClick(false)
            }

        } else {
            holder.binding.recyclerViewBackground.setBackgroundResource(R.drawable.bg_color_long_click_item)
            listOfLongItem.add(calendarList[position])
        }
    }

    private fun setOnLongClickItem(holder: CalendarAdapter.ViewHolder, position: Int) {
        holder.binding.cardClickChange.setOnLongClickListener{
            onLongClickItem.invoke(calendarList[position])
            holder.binding.recyclerViewBackground.setBackgroundResource(R.drawable.bg_color_long_click_item)
            listOfLongItem.add(calendarList[position])
            Log.d("this", listOfLongItem.size.toString())
            showMenuOnlongClick(true)
            true
        }
    }

    fun deleteSelecterItem() {
        if (listOfLongItem.isNotEmpty()) {
            listOfLongItem.forEach { calendar->
                if (listOfLongItem.contains(calendar)) {
                    sendItemDelete?.invoke(calendar)
                }
            }
            listOfLongItem.clear()
        }
    }

    override fun getItemCount(): Int =  calendarList.size
}