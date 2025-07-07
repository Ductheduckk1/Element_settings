package im.vector.app.features.settings.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSettingsNotificationsBinding
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.settings.notifications.defaults.VectorSettingsDefaultNotificationFragment
import im.vector.app.features.settings.notifications.keywordandmentions.VectorSettingsKeywordAndMentionsNotificationFragment
import im.vector.app.features.settings.notifications.other.VectorSettingsOtherNotificationFragment
import javax.inject.Inject

@AndroidEntryPoint
class VectorSettingsNotificationFragment :
        VectorBaseFragment<VectorSettingsNotificationsBinding>(), MavericksView {

    @Inject lateinit var vectorPreferences: VectorPreferences
    private val viewModel: VectorSettingsNotificationViewModel by fragmentViewModel()

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsNotificationsBinding {
        return VectorSettingsNotificationsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Thông báo"
        setupSwitches()
        setupClicks()
        restoreStates()
    }

    override fun invalidate() {
        // Không cần xử lý nếu chỉ dùng event
    }

    private fun setupSwitches() {
        views.switchEnableDeviceNotif.setOnCheckedChangeListener { _, isChecked ->
            val action = if (isChecked) {
                VectorSettingsNotificationViewAction.EnableNotificationsForDevice("")
            } else {
                VectorSettingsNotificationViewAction.DisableNotificationsForDevice
            }
            viewModel.handle(action)
        }

        views.switchEnableInAppNotif.setOnCheckedChangeListener { _, isChecked ->
            vectorPreferences.setInAppNotificationEnabled(isChecked)
        }

        views.switchShowDecrypted.setOnCheckedChangeListener { _, isChecked ->
            vectorPreferences.setShowDecryptedContentInNotifications(isChecked)
        }

        views.switchPinMissedRooms.setOnCheckedChangeListener { _, isChecked ->
            vectorPreferences.setPinMissedNotificationsRooms(isChecked)
        }

        views.switchPinUnreadRooms.setOnCheckedChangeListener { _, isChecked ->
            vectorPreferences.setPinUnreadRooms(isChecked)
        }
    }

    private fun setupClicks() {
        views.itemNotificationDefault.setOnClickListener {
            navigateTo(VectorSettingsDefaultNotificationFragment::class.java)
        }
        views.itemKeywordsMentions.setOnClickListener {
            navigateTo(VectorSettingsKeywordAndMentionsNotificationFragment::class.java)
        }
        views.itemNotificationOther.setOnClickListener {
            navigateTo(VectorSettingsOtherNotificationFragment::class.java)
        }
    }

    private fun restoreStates() {
        views.switchEnableInAppNotif.isChecked = vectorPreferences.areInAppNotificationsEnabled()
        views.switchShowDecrypted.isChecked = vectorPreferences.showDecryptedContentInNotifications()
        views.switchPinMissedRooms.isChecked = vectorPreferences.pinMissedNotificationsRooms()
        views.switchPinUnreadRooms.isChecked = vectorPreferences.pinUnreadRooms()
    }

    private fun navigateTo(fragmentClass: Class<out Fragment>) {
        parentFragmentManager.beginTransaction()
                .replace((view?.parent as ViewGroup).id, fragmentClass, null)
                .addToBackStack(null)
                .commit()
    }
}
