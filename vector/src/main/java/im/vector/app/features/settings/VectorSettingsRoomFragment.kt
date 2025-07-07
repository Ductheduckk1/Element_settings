package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSettingsRoomDirectoryBinding

@AndroidEntryPoint
class VectorSettingsRoomFragment :
        VectorBaseFragment<VectorSettingsRoomDirectoryBinding>() {

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsRoomDirectoryBinding {
        return VectorSettingsRoomDirectoryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Room Directory"
        setupSwitch()
    }

    private fun setupSwitch() {
        views.switchShowExplicitRooms.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            // TODO: Lưu trạng thái vào SharedPreferences hoặc ViewModel nếu cần
        }
    }
}
