package com.kokomi.origin.creation.image

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.explore.tabBarHeight
import com.kokomi.origin.navigationHeight
import com.kokomi.origin.util.copyAndLoadBitmap
import com.kokomi.origin.util.find
import com.kokomi.origin.util.statusBarHeight
import com.kokomi.origin.util.toast

private val ACTION_PICK_IMAGE = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
private const val PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE

class ImageUploadFragment : BaseFragment() {

    private val requirePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) pickAction.launch(Intent(Intent.ACTION_PICK, ACTION_PICK_IMAGE))
            else toast("没有存储权限，无法读取文件")
        }

    private val pickAction =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val file = requireActivity().copyAndLoadBitmap(uri)
                image.scaleType = ImageView.ScaleType.CENTER_CROP
                Glide.with(this)
                    .load(file)
                    .into(image)
            }
        }

    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_image_upload_origin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        image = view find R.id.iv_image_news_image
        with(view) {
            image.setOnClickListener {
                requirePermission.launch(PERMISSION)
            }

            find<TextView>(R.id.tv_image_upload_status_bar) {
                height = statusBarHeight + tabBarHeight
            }

            find<TextView>(R.id.tv_image_upload_empty_area) {
                height = (0.8f * requireActivity().window.decorView.height).toInt()
            }

            find<TextView>(R.id.tv_image_upload_navigation_bar) {
                height = navigationHeight
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ImageUploadFragment()
    }
}