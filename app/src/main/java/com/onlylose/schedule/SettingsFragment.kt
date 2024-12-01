import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.onlylose.schedule.MainActivity
import com.onlylose.schedule.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val switchState = sharedPreferences.getBoolean("switchState", false)
        val switchNameOfLess = sharedPreferences.getBoolean("switchNameOfLess", false)
        val willClose = sharedPreferences.getBoolean("willClose", false)
        val mainGr = sharedPreferences.getString("mainGroup", "")

        binding.switchButNameOfLesson.isChecked = switchNameOfLess
        binding.switchBut.isChecked = switchState
        binding.willClose.isChecked = willClose
        binding.mainGroupEt.setText(mainGr)


        binding.switchBut.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("switchState", isChecked).apply()
            if (sharedPreferences.getBoolean("switchState", false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        binding.switchButNameOfLesson.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("switchNameOfLess", isChecked).apply()
        }
        binding.willClose.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("willClose", isChecked).apply()
        }
        binding.mainGroupEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val text = binding.mainGroupEt.text.toString().uppercase(Locale.getDefault())
                if (binding.mainGroupEt.text.toString() != text) {
                    binding.mainGroupEt.setText(text)
                    binding.mainGroupEt.setSelection(text.length)
                }
                val inputText = binding.mainGroupEt.text
                sharedPreferences.edit().putString("mainGroup", inputText.toString()).apply()
            }
        })
    }
}