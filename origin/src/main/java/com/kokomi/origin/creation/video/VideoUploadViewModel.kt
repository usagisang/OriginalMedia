package com.kokomi.origin.creation.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokomi.origin.entity.User
import com.kokomi.origin.util.emit
import com.kokomi.origin.util.toast
import com.kokomi.uploader.Uploader
import com.kokomi.uploader.entity.ReleaseInfo
import com.kokomi.uploader.listener.UploaderListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream

class VideoUploadViewModel : ViewModel() {

    private val _uploadProgress = MutableStateFlow(-1)
    internal val uploadProgress: StateFlow<Int> = _uploadProgress

    private var isUploading = false

    internal fun upload(
        user: User?,
        title: String,
        inputStreamInfo: Pair<InputStream, String>
    ) {
        if (user == null) {
            toast("请先登录")
            return
        }
        if (title.isBlank()) {
            toast("标题不能为空")
            return
        }
        if (title.length > 40) {
            toast("正文太长了，最长 40 字")
            return
        }
        if (isUploading) {
            toast("正在上传中...")
            return
        }
        isUploading = true
        viewModelScope.launch {
            Uploader.uploadVideo(
                ReleaseInfo(user.userId, title, "", inputStreamInfo = inputStreamInfo),
                object : UploaderListener {
                    override fun onUploading(name: String, progress: Int) {
                        viewModelScope.launch { _uploadProgress emit progress }
                    }
                }
            ).collect {
                toast("上传成功")
                isUploading = false
            }
        }
    }

}