package top.gochiusa.originalmedia.explore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import top.gochiusa.originalmedia.explore.bean.Data
import top.gochiusa.originalmedia.explore.repository.NewsRepository

class GraphicViewModel:ViewModel(){
    private val graphicLiveData = MutableLiveData<GraphicSearchData>()

    val graphicList = ArrayList<Data>()

    val graphicListLiveData = Transformations.switchMap(graphicLiveData){
        NewsRepository.graphicList(it.typeId,it.page)
    }


    fun getGraphicList(typeId: String,page: String){
        graphicLiveData.value = GraphicSearchData(typeId, page)
    }


    data class GraphicSearchData(var typeId:String,var page:String)

}