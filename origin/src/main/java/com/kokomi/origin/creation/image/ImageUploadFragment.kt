package com.kokomi.origin.creation.image

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.base.loggedUser
import com.kokomi.origin.explore.tabBarHeight
import com.kokomi.origin.navigationHeight
import com.kokomi.origin.util.find
import com.kokomi.origin.util.getInputStreamInfoFrom
import com.kokomi.origin.util.statusBarHeight
import com.kokomi.origin.util.toast
import com.kokomi.origin.weight.ProgressButton
import kotlinx.coroutines.launch

private val ACTION_PICK_IMAGE = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI

class ImageUploadFragment : BaseFragment() {

    private var imageUri: Uri? = null

    private val pickAction =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                imageUri = uri
                Glide.with(this)
                    .load(uri)
                    .into(image)
                image.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_image_upload_origin, container, false)
    }

    private lateinit var image: ImageView
    private lateinit var progressBar: ProgressButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title: EditText = view find R.id.edt_image_news_title
        image = view find R.id.iv_image_news_image
        val content: EditText = view find R.id.edt_image_news_content
        progressBar = view find R.id.btn_image_upload_progress

        with(view) {
            image.setOnClickListener {
                pickAction.launch(Intent(Intent.ACTION_PICK, ACTION_PICK_IMAGE))
            }

            viewModel<ImageUploadViewModel> {
                view.find<ImageView>(R.id.iv_image_upload_complete) {
                    setOnClickListener {
                        val uri = imageUri
                        if (uri == null) {
                            toast("未选择图片")
                            return@setOnClickListener
                        }
                        val info = requireContext() getInputStreamInfoFrom uri
                        progressBar.visibility = View.VISIBLE

                        lifecycleScope.launch {
                            uploadProgress.collect {
                                progressBar.progress = when (it) {
                                    0 -> 0F
                                    100 -> 1F
                                    else -> {
                                        it.toFloat() / 100F
                                    }
                                }
                            }
                        }

                        upload(loggedUser, title.text.toString(), content.text.toString(), info,
                            {
                                progressBar.visibility = View.GONE
                            }, {
                                progressBar.visibility = View.GONE
                                title.setText("")
                                content.setText("")
                                imageUri = null
                                image.scaleType = ImageView.ScaleType.CENTER
                                Glide.with(this)
                                    .load(R.drawable.ic_image_creation)
                                    .into(image)
                            })
                    }
                }
            }

            find<TextView>(R.id.tv_image_upload_status_bar) {
                lifecycleScope.launch {
                    tabBarHeight.collect { height = it + statusBarHeight }
                }
            }

            find<TextView>(R.id.tv_image_upload_empty_area) {
                height = (0.8f * requireActivity().window.decorView.height).toInt()
            }

            find<TextView>(R.id.tv_image_upload_navigation_bar) {
                lifecycleScope.launch {
                    navigationHeight.collect { height = it }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ImageUploadFragment()
    }
}