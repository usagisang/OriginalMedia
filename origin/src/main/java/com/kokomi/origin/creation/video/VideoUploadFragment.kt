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
                        upload(loggedUser, title.text.toString(), info)
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
        playerView.onResume()
        playerView.setPlayer(player)
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
    }

    companion object {
        @JvmStatic
        fun newInstance() = VideoUploadFragment()
    }

}