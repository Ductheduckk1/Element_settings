package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.utils.copyToClipboard
import im.vector.app.databinding.VectorSettingsAdvancedSettingsBinding
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.home.NightlyProxy
import im.vector.app.features.rageshake.RageShake
import im.vector.lib.strings.CommonStrings
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject

@AndroidEntryPoint
class VectorSettingsAdvancedSettingsFragment :
        VectorBaseFragment<VectorSettingsAdvancedSettingsBinding>() {

    @Inject lateinit var nightlyProxy: NightlyProxy
    @Inject lateinit var vectorPreferences: VectorPreferences
    @Inject lateinit var session: Session

    private var rageshake: RageShake? = null

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsAdvancedSettingsBinding {
        return VectorSettingsAdvancedSettingsBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Gán analytics screen name tại đây, sẽ tự trigger trong onResume của VectorBaseFragment
        analyticsScreenName = MobileScreen.ScreenName.SettingsAdvanced
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Cài đặt nâng cao"
        setupRageShakeSection()
        setupDevToolsSection()
    }

    override fun onResume() {
        super.onResume()
        rageshake = vectorBaseActivity.rageShake
        rageshake?.interceptor = {
            vectorBaseActivity.showSnackbar(getString(CommonStrings.rageshake_detected))
        }
    }

    override fun onPause() {
        super.onPause()
        rageshake?.interceptor = null
        rageshake = null
    }

    private fun setupDevToolsSection() {
        views.itemAccessToken.setOnClickListener {
            copyToClipboard(requireContext(), session.sessionParams.credentials.accessToken)
        }

        if (session.cryptoService().supportKeyRequestInspection()) {
            views.itemKeyRequests.visibility = View.VISIBLE
            views.itemKeyRequests.setOnClickListener {
                // TODO: navigate to KeyRequestsFragment
            }
        } else {
            views.itemKeyRequests.visibility = View.GONE
        }
    }

    private fun setupRageShakeSection() {
        val isAvailable = RageShake.isAvailable(requireContext())
        if (!isAvailable) {
            views.groupRageshake.visibility = View.GONE
            return
        }

        views.switchRageshake.isChecked = vectorPreferences.isUseRageShakeEnabled()

        views.switchRageshake.setOnCheckedChangeListener { _, isChecked ->
            vectorPreferences.setUseRageShake(isChecked)
            if (isChecked) rageshake?.start() else rageshake?.stop()
        }

        val threshold = vectorPreferences.getRageShakeDetectionThreshold()
        views.seekbarRageshake.progress = threshold

        views.seekbarRageshake.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                vectorPreferences.setRageShakeDetectionThreshold(progress)
                rageshake?.setSensitivity(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
