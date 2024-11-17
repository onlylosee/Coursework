package com.onlylose.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.onlylose.schedule.databinding.FragmentScheduleBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding

    private var currentWeek: Int = 0
    private lateinit var previousWeekButton: AppCompatButton
    private var cal: Calendar = Calendar.getInstance()
    private lateinit var currentActiveWeekButton: AppCompatButton
    private var a = 0

    private var lastActiveDayButton: AppCompatButton? = null
    private val url = "https://cabinet.amursu.ru/public_api/group/1584"

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        cal.firstDayOfWeek = Calendar.MONDAY

        currentActiveWeekButton = binding.butWeek1
        currentActiveWeekButton.background = requireContext().resources.getDrawable(R.drawable.shapeinactivebut, requireContext().theme)
        currentActiveWeekButton.isEnabled = false

        setupDayButtons()
        setupWeekButtons()
        loadCurrentWeekData()

        return binding.root
    }


    private fun getDayButtonForWeekday(day: Int): AppCompatButton? {
        return when (day) {
            Calendar.MONDAY -> binding.but1
            Calendar.TUESDAY -> binding.but2
            Calendar.WEDNESDAY -> binding.but3
            Calendar.THURSDAY -> binding.but4
            Calendar.FRIDAY -> binding.but5
            Calendar.SATURDAY -> binding.but6
            else -> null
        }
    }


    private fun setupWeekButtons() {
        binding.butWeek1.setOnClickListener {
            handleWeekButtonClick(it as AppCompatButton, 1)
        }
        binding.butWeek2.setOnClickListener {
            handleWeekButtonClick(it as AppCompatButton, 2)
        }
    }


    private fun setupDayButtons() {
        val dayButtons = listOf(
            binding.but1, binding.but2, binding.but3,
            binding.but4, binding.but5, binding.but6
        )

        val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        dayButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                handleDayButtonClick(button, index + 2) // дни с 2 по 7
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleDayButtonClick(button: AppCompatButton, day: Int) {
        lastActiveDayButton?.let { previousButton ->
            previousButton.isEnabled = true
            previousButton.background = requireContext().resources.getDrawable(R.drawable.shapebut, requireContext().theme)
        }
        a++
        lastActiveDayButton = button
        button.isEnabled = false
        button.background = requireContext().resources.getDrawable(
            R.drawable.shapeinactivebut,
            requireContext().theme
        )

        loadJsonData(url, day)

        updateCurrentDayButtonState(day)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateCurrentDayButtonState(selectedDay: Int) {
        // Получаем кнопку для текущего дня
        val currentDayButton = getDayButtonForWeekday(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))

        // Если выбранный день не текущий, сбросьте кнопку текущего дня
        if (selectedDay != Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            currentDayButton?.isEnabled = true
            currentDayButton?.background = requireContext().resources.getDrawable(R.drawable.shapebut, requireContext().theme)
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleWeekButtonClick(button: AppCompatButton, week: Int) {
        // Сбрасываем состояние предыдущей активной кнопки недели
        currentActiveWeekButton.isEnabled = true
        currentActiveWeekButton.background = requireContext().resources.getDrawable(R.drawable.shapebut, requireContext().theme)

        // Устанавливаем текущую кнопку как активную
        currentActiveWeekButton = button
        button.isEnabled = false
        button.background = requireContext().resources.getDrawable(R.drawable.shapeinactivebut, requireContext().theme)

        currentWeek = week

        val selectedDay = getSelectedDay()
        loadJsonData(url, selectedDay)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun loadCurrentWeekData() {
        val calendarDayWeek = cal.get(Calendar.DAY_OF_WEEK)
        val dayButton = getDayButtonForWeekday(calendarDayWeek)

        dayButton?.let {
            it.isEnabled = true
            it.background = requireContext().resources.getDrawable(R.drawable.shapeinactivebut, requireContext().theme)
            previousWeekButton = it
        }

        if (previousWeekButton != null) {
            previousWeekButton?.isEnabled = true
            previousWeekButton?.background = requireContext().resources.getDrawable(R.drawable.shapeinactivebut, requireContext().theme)
        } else {
            previousWeekButton = dayButton ?: return
        }
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedSchedule = sharedPreferences.getString("shedule", null)

        if (savedSchedule != null) {
            loadScheduleFromPreferences(savedSchedule)
        } else {
            loadJsonData(url, calendarDayWeek)
        }

    }

    private fun getSelectedDay(): Int {
        if (a > 0){
            return when (lastActiveDayButton){
                binding.but1 -> 2
                binding.but2 -> 3
                binding.but3 -> 4
                binding.but4 -> 5
                binding.but5 -> 6
                else -> 7
            }
        }
        else{
            val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            return when (currentDayOfWeek) {
                Calendar.MONDAY -> 2
                Calendar.TUESDAY -> 3
                Calendar.WEDNESDAY -> 4
                Calendar.THURSDAY -> 5
                Calendar.FRIDAY -> 6
                Calendar.SATURDAY -> 7
                else -> 2 // или любой день по умолчанию
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun loadJsonData(url: String, day: Int) {
        val queue = Volley.newRequestQueue(requireContext())
        val weekDay = when (day) {
            2 -> 1
            3 -> 2
            4 -> 3
            5 -> 4
            6 -> 5
            7 -> 6
            else -> return
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                handleJsonResponse(response, weekDay)
            },
            { error ->
                Log.d("Volley Error", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }
    private fun handleJsonResponse(response: JSONObject, weekDay: Int) {
        val timetableTemplateLines = response.getJSONArray("timetable_tamplate_lines")
        val currentWeek = response.getInt("current_week")

        val lessonNames = Array(7) { "" }
        val lessonCabinets = Array(7) { "" }
        val lessonTeachers = Array(7) { "" }

        for (i in 0 until timetableTemplateLines.length()) {
            val line = timetableTemplateLines.getJSONObject(i)
            processTimetableLine(line, weekDay, currentWeek, lessonNames, lessonCabinets, lessonTeachers)
        }

        saveSchedule(weekDay, lessonNames, lessonCabinets, lessonTeachers)

        binding.tvCurrentWeek.text = "Текущая неделя $currentWeek"

        loadRecyclerViewData(lessonNames, lessonCabinets, lessonTeachers)
    }

    private fun saveSchedule(weekDay: Int, lessonNames: Array<String>, lessonCabinets: Array<String>, lessonTeachers: Array<String>) {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Сериализация данных в строку (JSON)
        val jsonSchedule = JSONObject()
        jsonSchedule.put("weekday", weekDay)
        jsonSchedule.put("lessonNames", JSONArray(lessonNames.toList()))
        jsonSchedule.put("lessonCabinets", JSONArray(lessonCabinets.toList()))
        jsonSchedule.put("lessonTeachers", JSONArray(lessonTeachers.toList()))

        // Сохраняем строку в SharedPreferences
        editor.putString("schedule_$weekDay", jsonSchedule.toString())
        editor.apply()
    }


    private fun loadScheduleFromPreferences(savedSchedule: String) {
        val jsonObject = JSONObject(savedSchedule)
        val lessonNames = jsonObject.getJSONArray("lessonNames")
        val lessonCabinets = jsonObject.getJSONArray("lessonCabinets")
        val lessonTeachers = jsonObject.getJSONArray("lessonTeachers")

        val names = Array(lessonNames.length()) { lessonNames.getString(it) }
        val cabinets = Array(lessonCabinets.length()) { lessonCabinets.getString(it) }
        val teachers = Array(lessonTeachers.length()) { lessonTeachers.getString(it) }

        loadRecyclerViewData(names, cabinets, teachers)
    }
    private fun loadRecyclerViewData(lessonNames: Array<String>, lessonCabinets: Array<String>, lessonTeachers: Array<String>) {
        val newArrayList = ArrayList<DataClass>()

        val startLessonTimes = arrayOf(
            "8:15", "9:55", "11:55",
            "13:35", "15:15", "16:55", "18:35"
        )
        val endLessonTimes = arrayOf(
            "9:45", "11:25", "13:25",
            "15:05", "16:45", "18:25", "20:05"
        )

        for (i in lessonNames.indices) {
            val data = DataClass(
                startLessonTimes.getOrNull(i),
                lessonNames[i],
                lessonTeachers.getOrNull(i),
                endLessonTimes.getOrNull(i),
                lessonCabinets.getOrNull(i)
            )
            newArrayList.add(data) // Добавляем данные в список
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = MyAdapter(newArrayList) // Создаем адаптер и передаем список
        binding.recyclerView.adapter = adapter // Устанавливаем адаптер для RecyclerView
    }

    private fun processTimetableLine(
        line: JSONObject,
        weekDay: Int,
        currentWeek: Int,
        lessonNames: Array<String>,
        lessonCabinets: Array<String>,
        lessonTeachers: Array<String>
    ) {
        if (!line.isNull("person_id") && line.getInt("weekday") == weekDay) {
            val lessonIndex = line.getInt("lesson") - 1
            if (lessonIndex in lessonNames.indices) {
                // Проверка на discipline_str
                val discipline = line.optString("discipline_str", null.toString())
                if (!discipline.isNullOrEmpty()) {
                    // Если discipline не пустое, обновляем остальные массивы
                    lessonNames[lessonIndex] = discipline
                    lessonCabinets[lessonIndex] = line.optString("classroom_str", "")
                    lessonTeachers[lessonIndex] = line.optString("person_str", "")
                } else {
                    // Если discipline пустое, устанавливаем "Нет пары"
                    lessonNames[lessonIndex] = "Нет пары"
                    // Можно также очистить остальные массивы или оставить их прежними
                    lessonCabinets[lessonIndex] = "" // Здесь можно оставить прежнее значение
                    lessonTeachers[lessonIndex] = "" // А здесь тоже
                }
            }
        }
    }

}
//class ScheduleFragment : Fragment() {
//    private lateinit var binding: FragmentScheduleBinding
//    private lateinit var newArrayList: ArrayList<DataClass>
//    private lateinit var newRecyclerView: RecyclerView
//
//    private var currentWeek: Int = 0
//    private lateinit var previousWeekButton: AppCompatButton
//    private var selectedDayButton: AppCompatButton? = null
//    private lateinit var currentActiveWeekButton: AppCompatButton
//    private var cal: Calendar = Calendar.getInstance()
//
//    private val url = "https://cabinet.amursu.ru/public_api/group/1584"
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        binding = FragmentScheduleBinding.inflate(inflater, container, false)
//        cal.firstDayOfWeek = Calendar.MONDAY
//
//        currentActiveWeekButton = binding.butWeek1
//        currentActiveWeekButton.background = requireContext().resources.getDrawable(R.drawable.shapeinactivebut, requireContext().theme)
//        currentActiveWeekButton.isEnabled = false
//
//        setupDayButtons()
//        setupWeekButtons()
//        loadCurrentWeekData()
//
//        return binding.root
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    private fun loadCurrentWeekData() {
//        val calendar = cal.get(Calendar.DAY_OF_WEEK)
//        val dayButton = getDayButtonForWeekday(calendar)
//        if (dayButton != null) {
//            previousWeekButton = dayButton
//        }
//        dayButton?.isEnabled = true
//        dayButton?.background = requireContext().resources.getDrawable(R.drawable.shapeinactivebut, requireContext().theme)
//
//        loadJsonData(url, calendar)
//    }
//
//    private fun getDayButtonForWeekday(day: Int): AppCompatButton? {
//        return when (day) {
//            Calendar.MONDAY -> binding.but1
//            Calendar.TUESDAY -> binding.but2
//            Calendar.WEDNESDAY -> binding.but3
//            Calendar.THURSDAY -> binding.but4
//            Calendar.FRIDAY -> binding.but5
//            Calendar.SATURDAY -> binding.but6
//            else -> null
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun loadJsonData(url: String, day: Int) {
//        val queue = Volley.newRequestQueue(requireContext())
//        val weekDay = when (day) {
//            2 -> 1
//            3 -> 2
//            4 -> 3
//            5 -> 4
//            6 -> 5
//            7 -> 6
//            else -> return
//        }
//
//        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
//            { response ->
//                handleJsonResponse(response, weekDay)
//            },
//            { error ->
//                Log.d("Volley Error", error.toString())
//            }
//        )
//        queue.add(jsonObjectRequest)
//    }
//
//    private fun handleJsonResponse(response: JSONObject, weekDay: Int) {
//        val timetableTemplateLines = response.getJSONArray("timetable_tamplate_lines")
//        val currentWeek = response.getInt("current_week")
//
//        val lessonNames = Array(7) { "" }
//        val lessonCabinets = Array(7) { "" }
//        val lessonTeachers = Array(7) { "" }
//
//
//        for (i in 0 until timetableTemplateLines.length()) {
//            val line = timetableTemplateLines.getJSONObject(i)
//            processTimetableLine(line, weekDay, currentWeek, lessonNames, lessonCabinets, lessonTeachers)
//        }
//
//        binding.tvCurrentWeek.text = "Текущая неделя $currentWeek"
//        loadRecyclerViewData(lessonNames, lessonCabinets, lessonTeachers, )
//    }
//
//    private fun processTimetableLine(line: JSONObject, weekDay: Int, currentWeek: Int, lessonNames: Array<String>, lessonCabinets: Array<String>, lessonTeachers: Array<String>,
//                                     ) {
//        if (!line.isNull("person_id") && line.getInt("weekday") == weekDay) {
//            val lessonIndex = line.getInt("lesson") - 1
//            if (lessonIndex in lessonNames.indices) {
//                lessonNames[lessonIndex] = line.getString("discipline_str") ?: ""
//                lessonCabinets[lessonIndex] = line.getString("classroom_str") ?: ""
//                lessonTeachers[lessonIndex] = line.getString("person_str") ?: ""
//            }
//        }
//    }
//    private fun setupWeekButtons() {
//        binding.butWeek1.setOnClickListener {
//            handleWeekButtonClick(it as AppCompatButton, 1)
//        }
//        binding.butWeek2.setOnClickListener {
//            handleWeekButtonClick(it as AppCompatButton, 2)
//        }
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    private fun handleWeekButtonClick(button: AppCompatButton, week: Int) {
//        currentActiveWeekButton.let {
//            it.isEnabled = true
//            it.background = requireContext().resources.getDrawable(R.drawable.shapebut, requireContext().theme)
//        }
//        currentActiveWeekButton = button
//        button.isEnabled = false
//        button.background = requireContext().resources.getDrawable(R.drawable.shapeinactivebut, requireContext().theme)
//
//        currentWeek = week
//
//        loadCurrentWeekData()
//
//        selectedDayButton?.let { selectedButton ->
//            selectedButton.isEnabled = false
//            selectedButton.background = requireContext().resources.getDrawable(R.drawable.shapeinactivebut, requireContext().theme)
//        }
//    }
//    private fun loadRecyclerViewData(lessonNames: Array<String>, lessonCabinets: Array<String>, lessonTeachers: Array<String>) {
//        newArrayList = ArrayList()
//        val startLessonTimes = arrayOf(
//            "9:45", "11:25", "13:25",
//            "15:05", "16:45", "18:25", "20:05"
//        )
//        val endLessonTimes = arrayOf(
//            "9:45", "11:25", "13:25",
//            "15:05", "16:45", "18:25", "20:05"
//        )
//
//        for (i in lessonNames.indices) {
//            val data = DataClass(startLessonTimes.getOrNull(i) ?: "", lessonNames[i], lessonTeachers.getOrNull(i) ?: "", endLessonTimes.getOrNull(i) ?: "", lessonCabinets.getOrNull(i) ?: "")
//            newArrayList.add(data)
//        }
//
//        newRecyclerView = binding.recyclerView
//        newRecyclerView.layoutManager = LinearLayoutManager(context)
//        newRecyclerView.adapter = MyAdapter(newArrayList)
//    }
//
//
//    private fun setupDayButtons() {
//        val dayButtons = listOf(
//            binding.but1, binding.but2, binding.but3,
//            binding.but4, binding.but5, binding.but6
//        )
//
//        dayButtons.forEachIndexed { index, button ->
//            button.setOnClickListener {
//                handleDayButtonClick(button, index + 2) // дни с 2 по 7
//            }
//        }
//    }
//
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    private fun handleDayButtonClick(button: AppCompatButton, day: Int) {
//        if (::previousWeekButton.isInitialized) {
//            previousWeekButton.isEnabled = true
//            previousWeekButton.background = requireContext().resources.getDrawable(R.drawable.shapebut, requireContext().theme)
//        }
//
//        previousWeekButton = button
//        button.isEnabled = false
//        button.background = requireContext().resources.getDrawable(R.drawable.shapeinactivebut, requireContext().theme)
//
//        selectedDayButton = button
//
//        loadJsonData(url, day)
//    }
//    companion object {
//        @JvmStatic
//        fun newInstance() = ScheduleFragment()
//    }
//}