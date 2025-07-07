package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.utils.toast
import im.vector.app.databinding.VectorSettingsSercurityBinding
import im.vector.app.features.notifications.NotificationDrawerManager
import im.vector.app.features.pin.PinCodeStore
import im.vector.app.features.pin.PinMode
import im.vector.app.features.pin.lockscreen.biometrics.BiometricHelper
import im.vector.app.features.pin.lockscreen.configuration.LockScreenConfiguration
import im.vector.app.features.pin.lockscreen.configuration.LockScreenMode
import im.vector.lib.strings.CommonStrings
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class VectorSettingsSecurityFragment : VectorBaseFragment<VectorSettingsSercurityBinding>() {

    @Inject lateinit var pinCodeStore: PinCodeStore
    @Inject lateinit var notificationDrawerManager: NotificationDrawerManager
    @Inject lateinit var biometricHelperFactory: BiometricHelper.BiometricHelperFactory
    @Inject lateinit var defaultLockScreenConfiguration: LockScreenConfiguration

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsSercurityBinding {
        return VectorSettingsSercurityBinding.inflate(inflater, container, false)
    }

    private val biometricHelper by lazy {
        biometricHelperFactory.create(defaultLockScreenConfiguration.copy(mode = LockScreenMode.CREATE))
    }

    private val pinActivityResultLauncher = registerStartForActivityResult {
        refreshPinCodeState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Bảo mật và riêng tư"

        views.switchPin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val hasPin = pinCodeStore.hasEncodedPin()
                    if (!hasPin) {
                        navigator.openPinCode(
                                requireContext(),
                                pinActivityResultLauncher,
                                PinMode.CREATE
                        )
                    }
                    updateBiometricSwitchState()
                }
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    pinCodeStore.deletePinCode()
                    updateBiometricSwitchState()
                }
            }
        }

        views.switchFaceId.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewLifecycleOwner.lifecycleScope.launch {
                    runCatching {
                        if (biometricHelper.hasSystemKey) {
                            biometricHelper.disableAuthentication()
                        }
                        biometricHelper.enableAuthentication(requireActivity()).collect {}
                    }.onFailure {
                        showEnableBiometricErrorMessage()
                    }
                    updateBiometricSwitchState()
                }
            } else {
                disableBiometricAuthentication()
                updateBiometricSwitchState()
            }
        }

        views.itemPrivacy.setOnClickListener {
            navigateTo(im.vector.app.features.settings.devices.VectorSettingsDevicesFragment::class.java)
        }

        views.itemEncryption.setOnClickListener {
            navigateTo(VectorSettingsEncryptionFragment::class.java)
        }

        views.itemOther.setOnClickListener {
            navigateTo(VectorSecurityOtherFragment::class.java)
        }

        views.itemDeleteAccount.setOnClickListener {
            // TODO: Mở dialog xác nhận xóa tài khoản
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPinCodeState()
    }

    private fun refreshPinCodeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            val hasPin = pinCodeStore.hasEncodedPin()
            views.switchPin.isChecked = hasPin
            updateBiometricSwitchState()
        }
    }

    private fun updateBiometricSwitchState() {
        val hasPinCode = views.switchPin.isChecked
        val canUseBiometric = biometricHelper.canUseAnySystemAuth
        val isBiometricActive = biometricHelper.isSystemAuthEnabledAndValid

        views.switchFaceId.isEnabled = hasPinCode && canUseBiometric
        views.switchFaceId.isChecked = hasPinCode && isBiometricActive
    }

    private fun disableBiometricAuthentication() {
        runCatching {
            biometricHelper.disableAuthentication()
        }.onFailure {
            Timber.e(it)
        }
    }

    private fun showEnableBiometricErrorMessage() {
        context?.toast(CommonStrings.settings_security_pin_code_use_biometrics_error)
    }

    private fun navigateTo(fragmentClass: Class<out Fragment>) {
        parentFragmentManager.beginTransaction()
                .replace((view?.parent as ViewGroup).id, fragmentClass, null)
                .addToBackStack(null)
                .commit()
    }
}
