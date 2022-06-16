package top.gochiusa.originalmedia.base

interface BaseView<RESPONSE> {
    /**
     * 获取数据失败
     */
    fun  onError(message: String?)

    /**
     * 初始化数据或者刷新数据成功
     */
    fun  loadSuccess(reponse: RESPONSE?)

    /**
     * 加载更多成功
     */
    fun  loadMore(response: RESPONSE?)
}