package com.kokomi.origin.explore

import androidx.lifecycle.ViewModel
import com.kokomi.origin.entity.News
import com.kokomi.origin.network.NewsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch

class ExploreViewModel : ViewModel() {

    private val _imageNews = MutableStateFlow(mutableListOf<News>())
    internal val imageNews: StateFlow<List<News>> = _imageNews

    private val _mixNews = MutableStateFlow(mutableListOf<News>())
    internal val mixNews: StateFlow<List<News>> = _mixNews

    private val _videoNews = MutableStateFlow(mutableListOf<News>())
    internal val videoNews: StateFlow<List<News>> = _videoNews

    private val _imageHasNext = MutableStateFlow(true)
    internal val imageHasNext = _imageHasNext

    private val _videoHasNext = MutableStateFlow(true)
    internal val videoHasNext = _videoHasNext

    private var imagePage = 0
    private var videoPage = 0

    internal suspend fun loadMoreImageNews() {
        if (!_imageHasNext.value) return
        NewsApi.imageNews(imagePage)
            .catch {
                it.printStackTrace()
            }.collect {
                _imageHasNext.value = it.first
                _imageNews.value = _imageNews.value.apply {
                    addAll(it.second)
                }
            }
    }

    internal suspend fun loadMoreMixNews() {
        NewsApi.mixNews()
            .catch {
                it.printStackTrace()
            }.collect {
                _mixNews.value = _mixNews.value.apply {
                    addAll(it)
                }
            }
    }

    internal suspend fun loadMoreVideoNews() {
        if (!_videoHasNext.value) return
        NewsApi.videoNews(videoPage)
            .catch {
                it.printStackTrace()
            }.collect {
                _videoHasNext.value = it.first
                _videoNews.value = _videoNews.value.apply {
                    addAll(it.second)
                }
            }
    }

}