package im.vector.app.features.settings.labs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSettingsLabsBinding
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs
import im.vector.app.features.VectorFeatures
import im.vector.app.features.home.room.threads.ThreadsManager
import im.vector.app.features.settings.VectorPreferences
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.settings.LightweightSettingsStorage
import javax.inject.Inject

@AndroidEntryPoint
class VectorSettingsLabsFragment :
        VectorBaseFragment<VectorSettingsLabsBinding>(),
        MavericksView {

    private val viewModel: VectorSettingsLabsViewModel by fragmentViewModel()

    @Inject lateinit var vectorPreferences: VectorPreferences
    @Inject lateinit var lightweightSettingsStorage: LightweightSettingsStorage
    @Inject lateinit var threadsManager: ThreadsManager
    @Inject lateinit var vectorFeatures: VectorFeatures
    @Inject lateinit var session: Session

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsLabsBinding {
        return VectorSettingsLabsBinding.inflate(inflater, container, false)
    }

    override fun invalidate() {
        // Không cần xử lý nếu chỉ dùng sự kiện
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Labs"
        bindSettings()
    }

    private fun bindSettings() {
        views.apply {
            switchSwipeReply.isChecked = vectorPreferences.swipeToReplyIsEnabled()
            switchSwipeReply.setOnCheckedChangeListener { _, isChecked ->
                vectorPreferences.setSwipeToReply(isChecked)
            }

            switchLatex.isChecked = vectorPreferences.isLatexEnabled()
            switchLatex.setOnCheckedChangeListener { _, isChecked ->
                vectorPreferences.setLatexEnabled(isChecked)
                restartApp()
            }

            switchThreaded.isChecked = vectorPreferences.areThreadMessagesEnabled()
            switchThreaded.setOnCheckedChangeListener { _, isChecked ->
                onThreadsPreferenceClickedInterceptor(isChecked)
            }

            switchAutoReport.isChecked = vectorPreferences.labsAutoReportUISI()
            switchAutoReport.setOnCheckedChangeListener { _, isChecked ->
                vectorPreferences.setLabsAutoReportUISI(isChecked)
            }

            switchLocation.isChecked = vectorPreferences.isLiveLocationEnabled()
            switchLocation.setOnCheckedChangeListener { _, isChecked ->
                vectorPreferences.setLiveLocationSharingEnabled(isChecked)
            }

            switchLaoapp.isChecked = vectorPreferences.isLaoAppCallShortcutEnabled()
            switchLaoapp.setOnCheckedChangeListener { _, isChecked ->
                vectorPreferences.setLaoAppCallShortcutEnabled(isChecked)
            }

            switchDeferredDm.isChecked = vectorPreferences.isDeferredDmEnabled()
            switchDeferredDm.setOnCheckedChangeListener { _, isChecked ->
                vectorPreferences.setDeferredDmEnabled(isChecked)
            }

            switchRichText.isChecked = vectorPreferences.isRichTextEditorEnabled()
            switchRichText.setOnCheckedChangeListener { _, isChecked ->
                vectorPreferences.setRichTextEditorEnabled(isChecked)
            }

            switchSessionManager.isChecked = vectorPreferences.isNewSessionManagerEnabled()
            switchSessionManager.setOnCheckedChangeListener { _, isChecked ->
                vectorPreferences.setNewSessionManagerEnabled(isChecked)
            }

            switchClientInfo.isChecked = vectorPreferences.isClientInfoRecordingEnabled()
            switchClientInfo.setOnCheckedChangeListener { _, isChecked ->
                lifecycleScope.launch {
                    if (isChecked) {
                        viewModel.handle(VectorSettingsLabsAction.UpdateClientInfo)
                    } else {
                        viewModel.handle(VectorSettingsLabsAction.DeleteRecordedClientInfo)
                    }
                }
            }
        }
    }

    private fun onThreadsPreferenceClickedInterceptor(userChecked: Boolean) {
        if (!isThreadsSupported() && userChecked) {
            MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Enable Threads")
                    .setMessage(threadsManager.getLabsEnableThreadsMessage())
                    .setPositiveButton("Try it out") { _, _ -> onThreadsPreferenceClicked() }
                    .setNegativeButton("Not now") { _, _ -> views.switchThreaded.isChecked = false }
                    .show()
                    .findViewById<TextView>(android.R.id.message)
                    ?.apply { linksClickable = true }
        } else {
            onThreadsPreferenceClicked()
        }
    }

    private fun onThreadsPreferenceClicked() {
        vectorPreferences.setThreadFlagChangedManually()
        vectorPreferences.setShouldMigrateThreads(!vectorPreferences.areThreadMessagesEnabled())
        lightweightSettingsStorage.setThreadMessagesEnabled(vectorPreferences.areThreadMessagesEnabled())
        restartApp()
    }

    private fun restartApp() {
        MainActivity.restartApp(requireActivity(), MainActivityArgs(clearCache = true))
    }

    private fun isThreadsSupported(): Boolean {
        return session.homeServerCapabilitiesService().getHomeServerCapabilities().canUseThreading
    }
}
