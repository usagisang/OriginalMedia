package com.kokomi.origin.creation.video

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
import com.kokomi.carver.view.carver.CarverActivity
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.base.loggedUser
import com.kokomi.origin.explore.tabBarHeight
import com.kokomi.origin.navigationHeight
import com.kokomi.origin.util.find
import com.kokomi.origin.util.getInputStreamInfoFrom
import com.kokomi.origin.util.statusBarHeight
import com.kokomi.origin.util.toast
import com.kokomi.origin.weight.PlayerSwipeSlider
import com.kokomi.origin.weight.ProgressButton
import kotlinx.coroutines.launch
import top.gochiusa.glplayer.GLPlayer
import top.gochiusa.glplayer.PlayerView
import top.gochiusa.glplayer.base.Player
import top.gochiusa.glplayer.entity.MediaItem
import java.io.File

private val ACTION_PICK_VIDEO = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI

class VideoUploadFragment : BaseFragment() {

    private var videoUri: Uri? = null

    private val player: Player by lazy {
        GLPlayer.Builder(requireContext())
            .setRenderFirstFrame(true)
            .setInfiniteLoop(true)
            .setPlayAfterLoading(true)
            .build()
    }

    private val pickAction =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                selectUri(result.data?.data ?: return@registerForActivityResult)
            }
        }

    private val carverAction =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.let { intent ->
                    val path = intent.getStringExtra("video_file") ?: return@let
                    selectUri(Uri.fromFile(File(path)))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var playerView: PlayerView
    private lateinit var title: EditText
    private lateinit var reChoose: ImageView
    private lateinit var reChooseText: TextView
    private lateinit var curtain: View
    private lateinit var slider: PlayerSwipeSlider
    private lateinit var progressBar: ProgressButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_video_upload_origin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerView = view find R.id.pv_video_upload_player
        title = view find R.id.edt_video_news_title
        reChoose = view find R.id.iv_video_upload_re_choose
        reChooseText = view find R.id.tv_video_upload_re_choose
        curtain = view find R.id.v_video_upload_black_curtain
        slider = view find R.id.slider_video_upload_play_progress
        progressBar = view find R.id.btn_video_upload_progress

        with(view) {
            viewModel<VideoUploadViewModel> {
                find<ImageView>(R.id.iv_video_upload_complete) {
                    setOnClickListener {
                        val uri = videoUri
                        if (uri == null) {
                            toast("没有视频")
                            return@setOnClickListener
                        }
                        val info = requireContext() getInputStreamInfoFrom uri

                        progressBar.visibility = View.VISIBLE

                        val job = lifecycleScope.launch {
                            uploadProgress.collect {
                                progressBar.progress = when(it) {
                                    0 -> 0F
                                    100 -> 1F
                                    else -> { it.toFloat() / 100F }
                                }
                            }
                        }
                        upload(loggedUser, title.text.toString(), info) {
                            job.cancel()
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            }

            reChoose.setOnClickListener { reChoose() }

            find<ImageView>(R.id.iv_video_upload_recording) {
                setOnClickListener {
                    carverAction.launch(Intent(requireContext(), CarverActivity::class.java))
                }
            }

            find<ImageView>(R.id.iv_video_upload_add) {
                setOnClickListener {
                    pickAction.launch(Intent(Intent.ACTION_PICK, ACTION_PICK_VIDEO))
                }
            }

            find<TextView>(R.id.tv_video_upload_status_bar) {
                height = statusBarHeight + tabBarHeight
            }

            find<TextView>(R.id.tv_video_upload_navigation_bar) {
                height = navigationHeight
            }
        }
    }

    private fun selectUri(uri: Uri) {
        videoUri = uri
        curtain.visibility = View.VISIBLE
        playerView.visibility = View.VISIBLE
        reChoose.visibility = View.VISIBLE
        reChooseText.visibility = View.VISIBLE
        slider.visibility = View.VISIBLE
        playerView.onResume()
        playerView.setPlayer(player)
        slider.bindPlayer(player)
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
    }

    private fun reChoose() {
        videoUri = null
        playerView.bindPlayer?.pause()
        playerView.onPause()
        curtain.visibility = View.GONE
        playerView.visibility = View.GONE
        reChoose.visibility = View.GONE
        reChooseText.visibility = View.GONE
        slider.visibility = View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance() = VideoUploadFragment()
    }

}