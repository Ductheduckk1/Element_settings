package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.resources.BuildMeta
import im.vector.app.core.utils.copyToClipboard
import im.vector.app.core.utils.openAppSettingsPage
import im.vector.app.core.utils.openUrlInChromeCustomTab
import im.vector.app.databinding.VectorSettingsHelpAboutBinding
import im.vector.app.features.analytics.plan.MobileScreen
import im.vector.app.features.version.VersionProvider
import im.vector.lib.strings.CommonStrings
import org.matrix.android.sdk.api.Matrix
import javax.inject.Inject

@AndroidEntryPoint
class VectorSettingsHelpAboutFragment :
        VectorBaseFragment<VectorSettingsHelpAboutBinding>() {

    @Inject lateinit var versionProvider: VersionProvider
    @Inject lateinit var buildMeta: BuildMeta

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsHelpAboutBinding {
        return VectorSettingsHelpAboutBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? VectorBaseActivity<*>)?.analyticsScreenName = MobileScreen.ScreenName.SettingsHelp
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Help & About"

        // Help
        views.itemHelp.setOnClickListener {
            openUrlInChromeCustomTab(requireContext(), null, VectorSettingsUrls.HELP)
        }

        // App info
        views.itemAppInfo.setOnClickListener {
            activity?.let { openAppSettingsPage(it) }
        }

        // App version
        views.appVersion.text = getString(CommonStrings.settings_version)
        views.summaryAppVersion.text = buildString {
            append(versionProvider.getVersion(longFormat = false))
            if (buildMeta.isDebug) {
                append(" ")
                append(buildMeta.gitBranchName)
                append(" ")
                append(buildMeta.gitRevision)
            }
        }
        views.summaryAppVersion.setOnClickListener {
            copyToClipboard(requireContext(), views.summaryAppVersion.text.toString())
        }

        // SDK version
        views.SDKVersion.text = getString(CommonStrings.settings_sdk_version)
        views.summarySdkVersion.text = Matrix.getSdkVersion()
        views.summarySdkVersion.setOnClickListener {
            copyToClipboard(requireContext(), views.summarySdkVersion.text.toString())
        }

        // Crypto version
        views.titleCryptoVersion.text = getString(CommonStrings.settings_crypto_version)
        views.summaryVersionCrypto.text = Matrix.getCryptoVersion(true)
    }
}
