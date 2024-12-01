package com.onlylose.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.compose.ui.text.toUpperCase
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.onlylose.schedule.databinding.FragmentScheduleBinding
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale

class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding

    private var currentLoadedWeek: Int = 0
    private var currentWeek: Int = 0
    private var isMenuVisible = false
    private lateinit var previousWeekButton: AppCompatButton
    private var cal: Calendar = Calendar.getInstance()
    private lateinit var currentActiveWeekButton: AppCompatButton
    private var a = 0
    private var lastActiveDayButton: AppCompatButton? = null
    private val url = "https://cabinet.amursu.ru/public_api/group/"
    private var randomVariable: Int = 0
    private var selectedGroup: String? = null
    private var completedUrl: String = ""

    @SuppressLint("UseCompatLoadingForDrawables", "CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        cal.firstDayOfWeek = Calendar.MONDAY
        val shared = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val mainGr: String? = shared.getString("mainGroup", null)?.uppercase(Locale.ROOT)
        binding.popUpBut.setOnClickListener {
            menuLoad()
        }
        if (mainGr != null){
            groupSelected(mainGr)
            binding.hightv.text = "Расписание группы " + mainGr
            menuLoad()
            backGround()
            checkGroupIsNull()
        }
        else{
            menuLoad()
            backGround()
            checkGroupIsNull()
        }
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun backGround(){
        var sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("switchState", false)) {
            binding.tvCurrentWeek.setTextColor(requireContext().resources.getColor(R.color.white, requireContext().theme))
            binding.recyclerView.background = requireContext().resources.getDrawable(R.drawable.ppapblackvt, requireContext().theme)
            binding.textIf.setTextColor(requireContext().resources.getColor(R.color.white, requireContext().theme))
            binding.textIf.background = requireContext().resources.getDrawable(R.drawable.ppapblackvt, requireContext().theme)
        } else {
            binding.tvCurrentWeek.setTextColor(requireContext().resources.getColor(R.color.blueOfficial, requireContext().theme))
            binding.recyclerView.background = requireContext().resources.getDrawable(R.drawable.ppapwhite, requireContext().theme)
            binding.textIf.setTextColor(requireContext().resources.getColor(R.color.blueOfficial, requireContext().theme))
            binding.textIf.background = requireContext().resources.getDrawable(R.drawable.ppapwhite, requireContext().theme)
        }
    }

    private fun menuLoad(){
        val showPopupButton: Button = binding.popUpBut
        val recyclerView: RecyclerView = binding.menuRecyclerView

        val menuItems = listOf("Б221", "Б231", "Б241", "В211", "В221", "В231", "Г221", "Г231", "Г241", "Д211", "Д221", "Д231", "Д241", "И211", "И212", "И221", "И222", "И231", "И232", "И241", "ИС211", "ИС212", "ИС213", "ИС217", "ИС221", "ИС222", "ИС223", "ИС224", "ИС231", "ИС232", "ИС233", "ИС234", "ИС241", "ИС242", "К211", "КСК241", "Л231", "Л241", "М221", "М231", "М241", "П221", "П222", "П223", "П231", "П234", "Т221", "Т222", "Т231", "Т241", "Х211", "Х221", "Х231", "Х241", "Э221", "Э231", "Э241", "Ю241", "Ю242", "Ю243")
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val adapter = MenuAdapter(menuItems) { selectedItem ->
            groupSelected(selectedItem)
            if (sharedPreferences.getBoolean("willClose", false)){
                hideMenuWithAnimation(recyclerView)
            }
            binding.hightv.text = "Расписание группы " + selectedItem
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setBackgroundColor(Color.TRANSPARENT)
        showPopupButton.setOnClickListener {
            editor.putBoolean("isMenuActivated", isMenuVisible).apply()
            if (isMenuVisible) {
                hideMenuWithAnimation(recyclerView)
            } else {
                showMenuWithAnimation(recyclerView)
            }
        }
    }
    private fun showMenuWithAnimation(recyclerView: RecyclerView) {
        recyclerView.visibility = View.VISIBLE
        recyclerView.alpha = 0f
        recyclerView.translationY = 200f

        recyclerView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        isMenuVisible = true
    }

    private fun hideMenuWithAnimation(recyclerView: RecyclerView) {
        recyclerView.animate()
            .alpha(0f)
            .translationY(200f)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                recyclerView.visibility = View.GONE
            }
            .start()

        isMenuVisible = false
    }

    private fun groupSelected(title: CharSequence?) {
        selectedGroup = title.toString()
        var groupUrl = when (title.toString().uppercase(Locale.ROOT)) {
            "Б221" -> "1600"
            "Б231" -> "1680"
            "Б241" -> "1900"
            "В211" -> "1462"
            "В221" -> "1591"
            "В231" -> "1669"
            "Г221" -> "1592"
            "Г231" -> "1678"
            "Г241" -> "1868"
            "Д211" -> "1478"
            "Д221" -> "1599"
            "Д231" -> "1683"
            "Д241" -> "1874"
            "И211" -> "1457"
            "И212" -> "1458"
            "И221" -> "1588"
            "И222" -> "1589"
            "И231" -> "1674"
            "И232" -> "1675"
            "И241" -> "1865"
            "ИС211" -> "1453"
            "ИС212" -> "1454"
            "ИС213" -> "1455"
            "ИС217" -> "1456"
            "ИС221" -> "1584"
            "ИС222" -> "1585"
            "ИС223" -> "1586"
            "ИС224" -> "1587"
            "ИС231" -> "1671"
            "ИС232" -> "1672"
            "ИС233" -> "1673"
            "ИС234" -> "1570"
            "ИС241" -> "1863"
            "ИС242" -> "1864"
            "К211" -> "1464"
            "КСК241" -> "1903"
            "Л231" -> "1681"
            "Л241" -> "1870"
            "М221" -> "1593"
            "М231" -> "1679"
            "М241" -> "1869"
            "П221" -> "1594"
            "П222" -> "1595"
            "П223" -> "1596"
            "П231" -> "1684"
            "П234" -> "1685"
            "Т221" -> "1597"
            "Т222" -> "1598"
            "Т231" -> "1682"
            "Т241" -> "1873"
            "Х211" -> "1463"
            "Х221" -> "1583"
            "Х231" -> "1677"
            "Х241" -> "1867"
            "Э231" -> "1590"
            "Э234" -> "1676"
            "Э241" -> "1866"
            "Ю241" -> "1896"
            "Ю242" -> "1897"
            "Ю243" -> "1898"
            else -> {
                null
            }
        }
        if (groupUrl != null){
            completedUrl = url + groupUrl
            checkGroupIsNull()
        }
    }

    private fun checkGroupIsNull() {
        if (selectedGroup != null) {
            setupDayButtons()
            setupWeekButtons()
            loadCurrentWeekData()
            binding.textIf.isEnabled = false
            binding.textIf.text = ""
        } else {
            binding.textIf.text = "Выберите группу"
        }
    }

    private fun getDayButtonForWeekday(day: Int): AppCompatButton? {
        return when (day) {
            Calendar.MONDAY -> binding.but1
            Calendar.TUESDAY -> binding.but2
            Calendar.WEDNESDAY -> binding.but3
            Calendar.THURSDAY -> binding.but4
            Calendar.FRIDAY -> binding.but5
            Calendar.SATURDAY -> binding.but6
            else -> binding.but1
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
            previousButton.background =
                requireContext().resources.getDrawable(R.drawable.shapebut, requireContext().theme)
        }
        a++
        lastActiveDayButton = button
        button.isEnabled = false
        button.background = requireContext().resources.getDrawable(
            R.drawable.shapeinactivebut,
            requireContext().theme
        )

        loadJsonData(completedUrl, day)

        updateCurrentDayButtonState(day)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateCurrentDayButtonState(selectedDay: Int) {
        val currentDayButton =
            getDayButtonForWeekday(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
        if (selectedDay != Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            currentDayButton?.isEnabled = true
            currentDayButton?.background =
                requireContext().resources.getDrawable(R.drawable.shapebut, requireContext().theme)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleWeekButtonClick(button: AppCompatButton, week: Int) {
        if (currentLoadedWeek != 0) {
            currentActiveWeekButton.isEnabled = true
            currentActiveWeekButton.background =
                requireContext().resources.getDrawable(R.drawable.shapebut, requireContext().theme)
        }

        currentActiveWeekButton = button
        button.isEnabled = false
        button.background = requireContext().resources.getDrawable(
            R.drawable.shapeinactivebut,
            requireContext().theme
        )

        currentWeek = week
        val selectedDay = getSelectedDay()
        loadJsonData(completedUrl, selectedDay)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun loadCurrentWeekData() {
        val calendarDayWeek = cal.get(Calendar.DAY_OF_WEEK)
        val dayButton = getDayButtonForWeekday(calendarDayWeek)

        dayButton?.let {
            it.isEnabled = true
            it.background = requireContext().resources.getDrawable(
                R.drawable.shapeinactivebut,
                requireContext().theme
            )
            previousWeekButton = it
        }

        previousWeekButton.isEnabled = true
        previousWeekButton.background = requireContext().resources.getDrawable(
            R.drawable.shapeinactivebut,
            requireContext().theme
        )

        loadJsonData(completedUrl, calendarDayWeek)
    }

    private fun getSelectedDay(): Int {
        if (a > 0) {
            return when (lastActiveDayButton) {
                binding.but1 -> 2
                binding.but2 -> 3
                binding.but3 -> 4
                binding.but4 -> 5
                binding.but5 -> 6
                else -> 7
            }
        } else {
            val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            return when (currentDayOfWeek) {
                Calendar.MONDAY -> 2
                Calendar.TUESDAY -> 3
                Calendar.WEDNESDAY -> 4
                Calendar.THURSDAY -> 5
                Calendar.FRIDAY -> 6
                Calendar.SATURDAY -> 7
                else -> 2
            }
        }
    }

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun weekOnFirstLoad(view: View) {
        currentActiveWeekButton = view as AppCompatButton
        view.isEnabled = false
        view.background = requireContext().resources.getDrawable(
            R.drawable.shapeinactivebut,
            requireContext().theme
        )
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun handleJsonResponse(response: JSONObject, weekDay: Int) {
        if (randomVariable == 0) {
            currentLoadedWeek = response.getInt("current_week")
            binding.tvCurrentWeek.text = "Текущая неделя $currentLoadedWeek"
            currentWeek = currentLoadedWeek
            if (currentLoadedWeek == 1) {
                weekOnFirstLoad(binding.butWeek1)
            } else {
                weekOnFirstLoad(binding.butWeek2)
            }
        }
        val timetableTemplateLines = response.getJSONArray("timetable_tamplate_lines")
        val lessonNames = Array(7) { "" }
        val lessonCabinets = Array(7) { "" }
        val lessonTeachers = Array(7) { "" }
        val lessonSubgroup = Array(7) { 0 }
        for (i in 0 until timetableTemplateLines.length()) {
            val line = timetableTemplateLines.getJSONObject(i)
            processTimetableLine(
                line,
                weekDay,
                currentWeek,
                lessonNames,
                lessonCabinets,
                lessonTeachers,
                lessonSubgroup
            )
        }

        randomVariable++

        loadRecyclerViewData(lessonNames, lessonCabinets, lessonTeachers, lessonSubgroup)

    }

    private fun loadRecyclerViewData(
        lessonNames: Array<String>,
        lessonCabinets: Array<String>,
        lessonTeachers: Array<String>,
        lessonSubgroup: Array<Int>
    ) {
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
                lessonCabinets.getOrNull(i),
                lessonSubgroup.getOrNull(i)
            )
            newArrayList.add(data) // Добавляем данные в список
        }
        if (lessonTeachers.all { it == "" }){
            binding.textIf.text = "На этот день нет пар"
            binding.textIf.visibility = View.VISIBLE
        }
        else{
            binding.textIf.visibility = View.INVISIBLE
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = MyAdapter(requireContext(), newArrayList)
            binding.recyclerView.adapter = adapter
        }
    }

    private fun processTimetableLine(
        line: JSONObject,
        weekDay: Int,
        currentWeek: Int,
        lessonNames: Array<String>,
        lessonCabinets: Array<String>,
        lessonTeachers: Array<String>,
        lessonSubgroup: Array<Int>
    ) {
        if (!line.isNull("person_id") && line.getInt("weekday") == weekDay && (line.get("parity") == currentWeek || line.get("parity") == 0)) {
            val lessonIndex = line.getInt("lesson") - 1
            if (lessonIndex in lessonNames.indices) {
                val discipline = line.optString("discipline_str", null.toString())
                if (!discipline.isNullOrEmpty()) {
                    lessonNames[lessonIndex] = discipline
                    lessonCabinets[lessonIndex] = line.optString("classroom_str", "")
                    lessonTeachers[lessonIndex] = line.optString("person_str", "")
                    lessonSubgroup[lessonIndex] = line.optInt("subgroup", 0)
                } else {
                    lessonNames[lessonIndex] = "Нет пары"
                    lessonCabinets[lessonIndex] = ""
                    lessonTeachers[lessonIndex] = ""
                    lessonSubgroup[lessonIndex] = 0
                }
            }
        }
    }
}