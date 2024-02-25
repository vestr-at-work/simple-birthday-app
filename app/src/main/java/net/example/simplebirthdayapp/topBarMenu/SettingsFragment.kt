package net.example.simplebirthdayapp.topBarMenu

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import net.example.simplebirthdayapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val editTextPreference = preferenceManager.findPreference<EditTextPreference>("notification_hour")
        editTextPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.filters = Array<InputFilter>(1) {InputFilterMinMax(0, 23)}
        }

    }

    class InputFilterMinMax(
        val min: Int,
        val max: Int
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
