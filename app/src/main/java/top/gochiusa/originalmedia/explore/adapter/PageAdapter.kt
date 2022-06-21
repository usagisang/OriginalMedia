package top.gochiusa.originalmedia.explore.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import top.gochiusa.originalmedia.explore.fragment.GraphicFragment

import top.gochiusa.originalmedia.explore.fragment.VisualWorldFragment
import top.gochiusa.originalmedia.video.fragment.VideoFragment

/**
 * viewpager加载fragment的时候使用，viewpager的pageradapter适配器
 */
class PageAdapter(
    private val mFragmentManager: FragmentManager, //声明标题文本队列
) :
    FragmentStatePagerAdapter(mFragmentManager) {


    //获取指定位置的碎片fragment
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                GraphicFragment()
            }
            1 -> {
                VisualWorldFragment()
            }
            2 -> {
                VideoFragment()
            }
            else -> GraphicFragment()
        }
    }

    //获取fragment的个数
    override fun getCount() =3


}
