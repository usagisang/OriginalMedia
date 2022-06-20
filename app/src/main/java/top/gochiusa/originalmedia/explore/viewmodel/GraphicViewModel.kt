package top.gochiusa.originalmedia.explore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import top.gochiusa.originalmedia.explore.bean.Graphic
import top.gochiusa.originalmedia.explore.repository.NewsRepository

class GraphicViewModel:ViewModel(){
    private val graphicLiveData = MutableLiveData<GraphicSearchData>()

    var graphicList = ArrayList<Graphic>()

    val graphicListLiveData = Transformations.switchMap(graphicLiveData){
        NewsRepository.graphicList(it.page,it.limit)
    }


    fun getGraphicList(page: Int,limit: Int){
        graphicLiveData.value = GraphicSearchData(page, limit)
    }


    data class GraphicSearchData(var page:Int,var limit:Int)

}