package com.kokomi.origin.explore.flow

import androidx.lifecycle.ViewModel
import com.kokomi.origin.entity.News
import com.kokomi.origin.network.NewsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch

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

    private val _news = MutableStateFlow(Pair(mutableListOf<News>(), false))
    internal val news: StateFlow<Pair<List<News>, Boolean>> = _news

    private val _hasNext = MutableStateFlow(true)

    private var page = 0

    private var loading = false

    protected abstract suspend fun load(page: Int): Flow<Pair<Boolean, List<News>>>

    internal suspend fun loadMore() {
        if (loading) return
        loading = true
        if (!_hasNext.value) return
        load(page).catch { it.printStackTrace() }
            .collect {
                _hasNext.emit(it.first)
                val pair = _news.value
                _news.emit(Pair(pair.first.apply { addAll(it.second) }, !pair.second))
                page++
                loading = false
            }
    }

}