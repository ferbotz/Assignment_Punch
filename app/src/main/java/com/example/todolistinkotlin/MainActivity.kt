package com.example.todolistinkotlin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistinkotlin.analytics.*
import com.example.todolistinkotlin.databinding.ActivityMainBinding
import org.jetbrains.anko.alert
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity(), OnItemClick {

    val list = mutableListOf<ToDoListData>()

    val c = Calendar.getInstance()

    val month: Int = c.get(Calendar.MONTH)
    val year: Int = c.get(Calendar.YEAR)
    val day: Int = c.get(Calendar.DAY_OF_MONTH)

    var cal = Calendar.getInstance()

    private val listAdapter = ListAdapter(list, this)

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: ToDoListViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedEditor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModel = ViewModelProviders.of(this).get(ToDoListViewModel::class.java)
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sharedEditor = sharedPreferences.edit();

        binding.rvTodoList.layoutManager = LinearLayoutManager(this)
        binding.rvTodoList.adapter = listAdapter
        binding.vieModel = viewModel

        if(isFirstTime()){
            application.enqueueAndroidEvent(
                AnalyticsEvent(
                    Event.APP_FIRST_OPEN,
                    JSONObject()
                )
            )
        }


        viewModel.getPreviousList()

        viewModel.toDoList.observe(this, androidx.lifecycle.Observer {
            //list.addAll(it)
            if (it == null)
                return@Observer

            list.clear()
            val tempList = mutableListOf<ToDoListData>()
            it.forEach {
                tempList.add(
                    ToDoListData(
                        title = it.title,
                        date = it.date,
                        time = it.time,
                        indexDb = it.id,
                        isShow = it.isShow
                    )
                )

            }

            list.addAll(tempList)
            listAdapter.notifyDataSetChanged()
            viewModel.position = -1;

            viewModel.toDoList.value = null
        })

        viewModel.toDoListData.observe(this, androidx.lifecycle.Observer {
            if (viewModel.position != -1) {
                list.set(viewModel.position, it)
                listAdapter.notifyItemChanged(viewModel.position)
            } else {
                list.add(it)
                listAdapter.notifyDataSetChanged()
            }
            viewModel.position = -1;
        })

        binding.editText.setOnClickListener {
            application.enqueueUserEvent(
                AnalyticsEvent(
                    Event.TITLE_EDIT_TEXT_CLICKED,
                    JSONObject().apply {
                        put(TASK_TITLE, binding.editText.text.toString())
                        put(TASK_DATE, binding.etdate.text.toString())
                        put(TASK_TIME, binding.etTime.text.toString())
                    }
                )
            )
        }

        binding.etdate.setOnClickListener {
            application.enqueueUserEvent(
                AnalyticsEvent(
                    Event.DATE_EDIT_TEXT_CLICKED,
                    JSONObject().apply {
                        put(TASK_TITLE, binding.editText.text.toString())
                        put(TASK_DATE, binding.etdate.text.toString())
                        put(TASK_TIME, binding.etTime.text.toString())
                    }
                )
            )

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                binding.etdate.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                viewModel.month = monthOfYear
                viewModel.year = year
                viewModel.day = dayOfMonth
            }, year, month, day)

            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()

        }
        binding.etTime.setOnClickListener {
            application.enqueueUserEvent(
                AnalyticsEvent(
                    Event.TIME_EDIT_TEXT_CLICKED,
                    JSONObject().apply {
                        put(TASK_TITLE, binding.editText.text.toString())
                        put(TASK_DATE, binding.etdate.text.toString())
                        put(TASK_TIME, binding.etTime.text.toString())
                    }
                )
            )
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                this.cal.set(Calendar.HOUR_OF_DAY, hour)
                this.cal.set(Calendar.MINUTE, minute)

                viewModel.hour = hour
                viewModel.minute = minute

                binding.etTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }

            this.cal = cal
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
    }


    override fun onResume() {
        super.onResume()
    }

    private fun isFirstTime(): Boolean {
        return if (sharedPreferences.getBoolean("firstTime", true)) {
            sharedEditor.putBoolean("firstTime", false)
            sharedEditor.commit()
            sharedEditor.apply()
            true
        } else {
            false
        }
    }


    override fun onItemClick(v: View, position: Int) {

        val item = list.get(position)
        application.enqueueUserEvent(
            AnalyticsEvent(
                Event.TASK_ITEM_CLICKED,
                JSONObject().apply {
                    put(TASK_TITLE, item.title)
                    put(TASK_DATE, item.date)
                    put(TASK_TIME, item.time)
                    put(TASK_POSITION, position)
                }
            )
        )

        alert {
            message = list.get(position).title
            positiveButton("Edit") {
                viewModel.title.set(list.get(position).title)
                viewModel.date.set(list.get(position).date)
                viewModel.time.set(list.get(position).time)
                viewModel.position = position
                viewModel.index = list.get(position).indexDb
                binding.editText.isFocusable = true
            }
            negativeButton("Delete") {
                viewModel.delete(list.get(position).indexDb, list.get(position))
            }

        }.show()

    }

    override fun onStop() {
        super.onStop()
    }
}
