package im.vector.app.features.settings.notifications.other

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSettingsNotificationOtherBinding
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.pushrules.RuleIds
import org.matrix.android.sdk.api.session.pushrules.RuleKind
import org.matrix.android.sdk.api.session.pushrules.rest.PushRule
import javax.inject.Inject

@AndroidEntryPoint
class VectorSettingsOtherNotificationFragment :
        VectorBaseFragment<VectorSettingsNotificationOtherBinding>() {

    @Inject lateinit var session: Session
    private val pushRuleService get() = session.pushRuleService()

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsNotificationOtherBinding {
        return VectorSettingsNotificationOtherBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Khác"
        setupSwitches()
        loadStates()
    }

    private fun setupSwitches() {
        setupSwitch(views.switchRoomInvite, RuleIds.RULE_ID_INVITE_ME)
        setupSwitch(views.switchCallInvite, RuleIds.RULE_ID_CALL)
        setupSwitch(views.switchBotMessages, RuleIds.RULE_ID_SUPPRESS_BOTS_NOTIFICATIONS)
        setupSwitch(views.switchRoomUpgrade, RuleIds.RULE_ID_TOMBSTONE)
    }

    private fun setupSwitch(switch: CompoundButton, ruleId: String) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                val rule: PushRule = pushRuleService
                        .getPushRules()
                        .override
                        ?.firstOrNull { it.ruleId == ruleId }
                        ?: return@launch
                pushRuleService.updatePushRuleEnableStatus(RuleKind.OVERRIDE, rule, isChecked)
            }
        }
    }

    private fun loadStates() {
        lifecycleScope.launch {
            val rules = pushRuleService.getPushRules().override.orEmpty()
            views.switchRoomInvite.isChecked = rules.firstOrNull { it.ruleId == RuleIds.RULE_ID_INVITE_ME }?.enabled ?: false
            views.switchCallInvite.isChecked = rules.firstOrNull { it.ruleId == RuleIds.RULE_ID_CALL }?.enabled ?: false
            views.switchBotMessages.isChecked = rules.firstOrNull { it.ruleId == RuleIds.RULE_ID_SUPPRESS_BOTS_NOTIFICATIONS }?.enabled ?: false
            views.switchRoomUpgrade.isChecked = rules.firstOrNull { it.ruleId == RuleIds.RULE_ID_TOMBSTONE }?.enabled ?: false
        }
    }
}
