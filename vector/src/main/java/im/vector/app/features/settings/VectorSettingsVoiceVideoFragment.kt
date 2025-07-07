package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSettingsVoiceVideoBinding
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject

@AndroidEntryPoint
class VectorSettingsVoiceVideoFragment : VectorBaseFragment<VectorSettingsVoiceVideoBinding>() {

    @Inject lateinit var session: Session

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsVoiceVideoBinding {
        return VectorSettingsVoiceVideoBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Âm thanh và hình ảnh"
        setupSwitches()
    }

    private fun setupSwitches() {
        setupSwitch(views.switchFallbackCallSupport, "fallback_call_support")
        setupSwitch(views.switchPreventAccidentalCall, "prevent_accidental_call")
    }

    private fun setupSwitch(switch: CompoundButton, key: String) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch {
                val prefs = requireContext().getSharedPreferences("voice_video_settings", 0)
                prefs.edit { putBoolean(key, isChecked) }
            }
        }

        val prefs = requireContext().getSharedPreferences("voice_video_settings", 0)
        switch.isChecked = prefs.getBoolean(key, false)
    }
}
