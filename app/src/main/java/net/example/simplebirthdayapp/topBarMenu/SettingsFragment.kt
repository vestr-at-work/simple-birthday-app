package net.example.simplebirthdayapp.topBarMenu

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import net.example.simplebirthdayapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val notificationHourEditTextPreference = preferenceManager.findPreference<EditTextPreference>(getString(R.string.notification_hour))
        notificationHourEditTextPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.filters = Array<InputFilter>(1) { InputFilterMinMax(0, 23) }
        }

        val switchValue = context?.let {
            PreferenceManager
                .getDefaultSharedPreferences(it)
                .getBoolean("in_advance_notification_switch", true)
        }

        val inAdvanceDaysEditTextPreference = preferenceManager.findPreference<EditTextPreference>("in_advance_notification_days")
        inAdvanceDaysEditTextPreference?.isVisible = switchValue == true


        inAdvanceDaysEditTextPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.filters = Array<InputFilter>(1) { InputFilterMinMax(1, 31) }
        }

        val inAdvanceSwitchPreference = preferenceManager.findPreference<SwitchPreference>("in_advance_notification_switch")
        inAdvanceSwitchPreference?.setOnPreferenceChangeListener { preference, newValue ->
            inAdvanceDaysEditTextPreference?.isVisible = newValue != false
            true
        }
    }

    class InputFilterMinMax(
        private val min: Int,
        private val max: Int
    ) : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            try {
                val input : Int = Integer.parseInt(dest?.subSequence(0, dstart).toString() + source + dest?.subSequence(dend, dest.length))
                if (input in min..max) {
                    return null
                }
            }
            catch (_: NumberFormatException) {}
            return ""
        }
    }
}
