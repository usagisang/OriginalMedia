package com.kokomi.origin.explore.flow

import androidx.lifecycle.ViewModel
import com.kokomi.origin.entity.News
import com.kokomi.origin.network.NewsApi
import com.kokomi.origin.util.toastNetworkError
import com.kokomi.origin.util.emit
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

abstract class NewsFlowViewModel : ViewModel() {

    private val _news = MutableStateFlow(Pair(mutableListOf<News>(), 0))
    internal val news: StateFlow<Pair<List<News>, Int>> = _news

    private val _hasNext = MutableStateFlow(true)

    private var page = 0

    private var loading = false

    private var refreshing = false

    protected abstract suspend fun load(page: Int): Flow<Pair<Boolean, List<News>>>

    internal suspend fun loadMore() {
        if (loading) return
        loading = true
        if (!_hasNext.value) return
        var p = page
        load(page).catch {
            it.printStackTrace()
            toastNetworkError()
        }.collect {
            if (p == page) {
                _hasNext emit it.first
                val pair = _news.value
                _news emit Pair(pair.first.apply { addAll(it.second) }, ++p)
                page = p
            }
            loading = false
        }
    }

    /**
     * 刷新资讯流，内部会在请求成功时先清空现有的资讯列表然后再更新请求道的数据
     *
     * @param onFinish 当刷新完成时回调，如果刷新成功，则传入 true ，
     * 如果刷新不成功（捕获到异常），则返回 false ，捕获到异常时，
     * 内部已经使用 toast 提醒用户，因此不需要重复提示
     * */
    internal suspend fun refresh(onFinish: (Boolean) -> Unit) {
        page = -1
        if (refreshing) return
        refreshing = true
        load(0).catch {
            it.printStackTrace()
            toastNetworkError()
            onFinish(false)
        }.collect {
            _news emit Pair(_news.value.first.apply { clear() }, -1)
            _hasNext emit it.first
            val pair = _news.value
            page++
            _news emit Pair(pair.first.apply { addAll(it.second) }, page)
            refreshing = false
            onFinish(true)
        }
    }

}