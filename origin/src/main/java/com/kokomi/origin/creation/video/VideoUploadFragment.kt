package com.kokomi.origin.creation.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment

class VideoUploadFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_video_upload_origin, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = VideoUploadFragment()
    }
}