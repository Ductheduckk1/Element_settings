package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSettingsMessageBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VectorSettingsMessageFragment :
        VectorBaseFragment<VectorSettingsMessageBinding>() {

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsMessageBinding {
        return VectorSettingsMessageBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Message"
        setupSwitches()
    }

    private fun setupSwitches() {
        with(views) {
            setupSwitch(switchMediaConfirm)
            setupSwitch(switchMessageBubbles)
            setupSwitch(switchReadReceipts)
            setupSwitch(switchTimestamps)
            setupSwitch(switchRemovedMessages)
            setupSwitch(switchEmojiKeyboard)
            setupSwitch(switchChatEffects)
            setupSwitch(switchLastestUserInfo)
            setupSwitch(switchJoinLeave)
            setupSwitch(switchAccountEvents)
            setupSwitch(switchTypingNotify)
            setupSwitch(switchSendEnter)
            setupSwitch(switchInlinePreview)
            setupSwitch(switchAutoplay)
            setupSwitch(switchMarkdown)
            setupSwitch(switchDirectShare)
        }
    }

    private fun setupSwitch(switch: CompoundButton) {
        switch.setOnCheckedChangeListener { _, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                // TODO: Lưu trạng thái nếu cần thiết
            }
        }
    }
}
