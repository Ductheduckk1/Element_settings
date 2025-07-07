package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseFragment
import androidx.lifecycle.lifecycleScope
import im.vector.app.core.utils.copyToClipboard
import im.vector.app.databinding.VectorSettingsEncryptionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.extensions.getFingerprintHumanReadable
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.isVerified
import javax.inject.Inject

@AndroidEntryPoint
class VectorSettingsEncryptionFragment :
        VectorBaseFragment<VectorSettingsEncryptionBinding>() {

    @Inject lateinit var session: Session

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsEncryptionBinding {
        return VectorSettingsEncryptionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Mã hóa tin nhắn"

        views.itemDeviceName.setOnClickListener {
            copyToClipboard(requireContext(), views.summaryDeviceName.text.toString())
        }

        views.itemDeviceId.setOnClickListener {
            copyToClipboard(requireContext(), views.summaryDeviceId.text.toString())
        }

        views.itemDeviceKey.setOnClickListener {
            copyToClipboard(requireContext(), views.summaryDeviceKey.text.toString())
        }

        views.itemCrossSigning.setOnClickListener {
            lifecycleScope.launch {
                val trustResult = session.cryptoService().crossSigningService().checkUserTrust(session.myUserId)
                val canCrossSign = session.cryptoService().crossSigningService().canCrossSign()

                if (!canCrossSign || !trustResult.isVerified()) {
                    (requireActivity() as? VectorSettingsActivity)?.let {
                        it.navigator.requestSelfSessionVerification(it)
                    }
                } else {
                    copyToClipboard(requireContext(), views.summaryCrossSigning.text.toString())
                }
            }
        }

        views.switchNeverSendUnverified.setOnCheckedChangeListener { _, isChecked ->
            session.cryptoService().setGlobalBlacklistUnverifiedDevices(isChecked)
        }

        lifecycleScope.launch {
            updateCryptographyInfo()
        }
    }

    private suspend fun updateCryptographyInfo() {
        val userId = session.myUserId
        val deviceId = session.sessionParams.deviceId
        val devices = session.cryptoService().getUserDevices(userId)
        val currentDevice = devices.find { it.deviceId == deviceId }

        val deviceInfo = session.cryptoService().getCryptoDeviceInfo(userId, deviceId)
        val humanFingerprint = deviceInfo?.getFingerprintHumanReadable()

        val trustResult = session.cryptoService().crossSigningService().checkUserTrust(userId)
        val crossSigningKeys = session.cryptoService().crossSigningService().getMyCrossSigningKeys()

        withContext(Dispatchers.Main) {
            views.summaryDeviceName.text = currentDevice?.displayName() ?: "-"
            views.summaryDeviceId.text = deviceId
            views.summaryDeviceKey.text = humanFingerprint ?: "-"

            val canSign = session.cryptoService().crossSigningService().canCrossSign()
            views.summaryCrossSigning.text = when {
                canSign -> getString(im.vector.lib.strings.R.string.encryption_information_dg_xsigning_complete)
                trustResult.isVerified() -> getString(im.vector.lib.strings.R.string.encryption_information_dg_xsigning_trusted)
                crossSigningKeys != null -> getString(im.vector.lib.strings.R.string.encryption_information_dg_xsigning_not_trusted)
                else -> getString(im.vector.lib.strings.R.string.encryption_information_dg_xsigning_disabled)
            }

            views.switchNeverSendUnverified.isChecked =
                    session.cryptoService().getGlobalBlacklistUnverifiedDevices()
        }
    }
}
