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


data class GraphicResult(
    val code: Int,
    val hasNext: Boolean,
    val limit: Int,
    val page: Int,
    val result: List<Graphic>
)

data class Graphic(
    val content: String,
    val images: String,
    val title: String,
    val uploadTime: String,
    val userId: Int
)