package top.gochiusa.originalmedia.video.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import top.gochiusa.originalmedia.video.data.api.VideoService
import top.gochiusa.originalmedia.video.entity.Video

internal class VideoSource(
    private val videoService: VideoService,
): PagingSource<Int, Video>() {

    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val page = params.key ?: START_PAGE
                val prevPage = if (page == START_PAGE) null else page - 1
                val response = videoService.getVideos(page, 3).await()
                if (response.code == 0) {
                    error("Response code is ${response.code}!")
                }
                val nextPage: Int? = if (response.hasNext) page + 1 else null
                LoadResult.Page(response.result, prevPage, nextPage)

            }.getOrElse {
                LoadResult.Error(it)
            }
        }
    }

    companion object {
        private const val START_PAGE = 0
    }
}
