package top.gochiusa.originalmedia.explore.bean

data class GraphicResponse(
    val code: Int,
    val data: List<Data>,
    val msg: String
)

data class Data(
    val digest: String,
    val imgList: List<String>,
    val newsId: String,
    val postTime: String,
    val source: String,
    val title: String,
)
