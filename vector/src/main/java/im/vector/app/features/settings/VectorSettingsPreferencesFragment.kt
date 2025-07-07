package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSettingsPreferencesBinding
import im.vector.app.features.themes.ThemeUtils
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class VectorSettingsPreferencesFragment :
        VectorBaseFragment<VectorSettingsPreferencesBinding>() {

    @Inject lateinit var vectorLocale: VectorLocale

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsPreferencesBinding {
        return VectorSettingsPreferencesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Giao diện và ngôn ngữ"

        setupThemeRadioGroup()
        setupLanguageRadioGroup()
    }

    private fun setupThemeRadioGroup() {
        val currentTheme = ThemeUtils.getApplicationTheme(requireContext())
        val selectedThemeId = when (currentTheme) {
            "light" -> views.radioThemeLight.id
            "dark" -> views.radioThemeDark.id
            else -> views.radioThemeSystem.id
        }
        views.radioGroupTheme.check(selectedThemeId)

        views.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                views.radioThemeLight.id -> "light"
                views.radioThemeDark.id -> "dark"
                else -> "system"
            }
            ThemeUtils.setApplicationTheme(requireContext().applicationContext, theme)
            activity?.recreate()
        }
    }

    private fun setupLanguageRadioGroup() {
        val currentLanguage = vectorLocale.applicationLocale.language
        val selectedLangId = when (currentLanguage) {
            "lo" -> views.radioLangLao.id
            "vi" -> views.radioLangVietnamese.id
            else -> views.radioLangEnglish.id
        }
        views.radioGroupLanguage.check(selectedLangId)

        views.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            val lang = when (checkedId) {
                views.radioLangLao.id -> "lo"
                views.radioLangVietnamese.id -> "vi"
                else -> "en"
            }
            vectorLocale.saveApplicationLocale(Locale(lang))
            activity?.recreate()
        }
    }
}
