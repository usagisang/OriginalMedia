package com.kokomi.origin.explore.flow

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kokomi.origin.entity.News
import com.kokomi.origin.network.NewsApi
import com.kokomi.origin.util.*
import kotlinx.coroutines.flow.*

internal class ImageFlowViewModel : NewsFlowViewModel() {
    override suspend fun load(page: Int) = NewsApi.imageNews(page)
}

internal class MixFlowViewModel : NewsFlowViewModel() {
    override suspend fun load(page: Int) = NewsApi.mixNews()
}

internal class VideoFlowViewModel : NewsFlowViewModel() {
    override suspend fun load(page: Int) = NewsApi.videoNews(page)
}

private const val TAG = "NewsFlowViewModel"

abstract class NewsFlowViewModel : ViewModel() {

    private val _news = MutableStateFlow(Pair(mutableListOf<News>(), 0))
    internal val news: StateFlow<Pair<List<News>, Int>> = _news

    private val _hasNext = MutableStateFlow(true)

    private var page = 0

    private var loading = false

    protected abstract suspend fun load(page: Int): Flow<Pair<Boolean, List<News>>>

    internal suspend fun loadMore() {
        if (loading) return
        if (!_hasNext.value) return
        Log.e(TAG, "loading = true1")
        loading = true
        load(page).catch {
            Log.e(TAG, "loading = false1")
            it.printStackTrace()
            toastNetworkError()
            loading = false
        }.collect {
            Log.e(TAG, "loading = false2")
            _hasNext emit it.first
            val pair = _news.value
            _news emit Pair(pair.first.apply { addAll(it.second) }, ++page)
            loading = false
        }
    }

    /**
     * 刷新资讯流，内部会在请求成功时先清空现有的资讯列表然后再更新请求道的数据
     *
     * @param onFinish 当刷新完成时回调，如果刷新成功，则传入 true ，
     * 如果刷新不成功（捕获到异常或正在加载），则返回 false ，捕获到异常时，
     * 内部已经使用 toast 提醒用户，因此不需要重复提示
     * */
    internal suspend fun refresh(onFinish: (Boolean) -> Unit) {
        while (loading) io { }
        Log.e(TAG, "loading = true2")
        loading = true
        _news emit Pair(_news.value.first.apply { clear() }, -1)
        page = -1
        load(0).catch {
            it.printStackTrace()
            toastNetworkError()
            onFinish(false)
            Log.e(TAG, "loading = false3")
            loading = false
        }.collect {
            onFinish(true)
            _hasNext emit it.first
            val pair = _news.value
            page = 0
            _news emit Pair(pair.first.apply { addAll(it.second) }, page++)
            Log.e(TAG, "loading = false4")
            loading = false
        }
    }

}