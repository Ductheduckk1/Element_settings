package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSettingsOtherSettingsBinding
import im.vector.app.features.settings.labs.VectorSettingsLabsFragment

@AndroidEntryPoint
class VectorSettingsOtherSettingsFragment :
        VectorBaseFragment<VectorSettingsOtherSettingsBinding>() {

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsOtherSettingsBinding {
        return VectorSettingsOtherSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Cài đặt khác"

        views.itemRoomDirectory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                    .replace(id, VectorSettingsRoomFragment())
                    .addToBackStack(null)
                    .commit()
        }

        views.itemAdvancedSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                    .replace(id, VectorSettingsAdvancedSettingsFragment())
                    .addToBackStack(null)
                    .commit()
        }

        views.itemLab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                    .replace(id, VectorSettingsLabsFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }
}
