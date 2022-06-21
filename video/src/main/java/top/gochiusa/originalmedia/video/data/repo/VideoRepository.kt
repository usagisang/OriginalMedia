package top.gochiusa.originalmedia.video.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import top.gochiusa.originalmedia.video.data.VideoSource
import top.gochiusa.originalmedia.video.data.api.VideoService
import top.gochiusa.originalmedia.video.data.net.ServiceCreator
import top.gochiusa.originalmedia.video.entity.Video

class VideoRepository {
    private val videoService = ServiceCreator.create(VideoService::class.java)
    private val pager: Pager<Int, Video> by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { VideoSource(videoService) }
        )
    }

    fun getPagingVideoFlow(): Flow<PagingData<Video>> {
        return pager.flow
    }
}