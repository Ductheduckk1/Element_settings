package im.vector.app.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.VectorSettingsContactBinding

@AndroidEntryPoint
class VectorSettingsContactFragment :
        VectorBaseFragment<VectorSettingsContactBinding>() {

    override fun getBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): VectorSettingsContactBinding {
        return VectorSettingsContactBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Danh bạ"
        setupSwitch()
        setupCountrySelector()
    }

    private fun setupSwitch() {
        views.switchDiscoverContacts.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            // TODO: Lưu hoặc cập nhật trạng thái tìm danh bạ
            // Ví dụ: viewModel.setContactDiscoveryEnabled(isChecked)
        }
    }

    private fun setupCountrySelector() {
        views.layoutContactCountry.setOnClickListener {
            // TODO: Mở dialog hoặc activity chọn quốc gia
            // Sau khi chọn xong, cập nhật UI:
            // views.tvCountry.text = "Nhật Bản" hoặc quốc gia tương ứng
        }
    }
}
