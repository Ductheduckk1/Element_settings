package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSecurityOtherFragmentBinding
import javax.inject.Inject
import org.matrix.android.sdk.api.session.Session

@AndroidEntryPoint
class VectorSecurityOtherFragment :
        VectorBaseFragment<VectorSecurityOtherFragmentBinding>() {

    @Inject lateinit var session: Session
    @Inject lateinit var vectorPreferences: VectorPreferences

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSecurityOtherFragmentBinding {
        return VectorSecurityOtherFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Khác"

        views.switchIncognitoKeyboard.setOnCheckedChangeListener { _, _ ->
            // TODO: Lưu vào vectorPreferences nếu cần
        }

        views.switchPreventScreenshot.setOnCheckedChangeListener { _, _ ->
            // TODO: Lưu vào vectorPreferences nếu cần
        }
    }
}
