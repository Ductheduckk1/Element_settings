package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSettingsRootBinding
import im.vector.app.features.settings.legals.LegalsFragment
import im.vector.app.features.settings.notifications.VectorSettingsNotificationFragment

@AndroidEntryPoint
class VectorSettingsRootFragment : VectorBaseFragment<VectorSettingsRootBinding>() {


    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsRootBinding {
        return VectorSettingsRootBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Cài đặt"

        views.itemNotifications.setOnClickListener {
            navigateTo(VectorSettingsNotificationFragment::class.java)
        }

        views.itemMessage.setOnClickListener {
            navigateTo(VectorSettingsMessageFragment::class.java)
        }

        views.itemCall.setOnClickListener {
            navigateTo(VectorSettingsVoiceVideoFragment::class.java)
        }

        views.itemContact.setOnClickListener {
            navigateTo(VectorSettingsContactFragment::class.java)
        }

        views.itemUiLanguage.setOnClickListener {
            navigateTo(VectorSettingsPreferencesFragment::class.java)
        }

        views.itemSecurity.setOnClickListener {
            navigateTo(VectorSettingsSecurityFragment::class.java)
        }

        views.itemOther.setOnClickListener {
            navigateTo(VectorSettingsOtherSettingsFragment::class.java)
        }

        views.itemHelp.setOnClickListener {
            navigateTo(VectorSettingsHelpAboutFragment::class.java)
        }

        views.itemPrivacy.setOnClickListener {
            navigateTo(LegalsFragment::class.java)
        }
    }

    private fun navigateTo(fragmentClass: Class<out Fragment>) {
        parentFragmentManager.beginTransaction()
                .replace((view?.parent as ViewGroup).id, fragmentClass, null)
                .addToBackStack(null)
                .commit()
    }
}
