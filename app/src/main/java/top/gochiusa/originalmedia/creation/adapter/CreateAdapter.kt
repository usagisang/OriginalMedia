package top.gochiusa.originalmedia.creation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import top.gochiusa.originalmedia.creation.fragment.GraphicCreateFragment
import top.gochiusa.originalmedia.creation.fragment.StartFragment
import top.gochiusa.originalmedia.creation.fragment.VideoCreateFragment
import top.gochiusa.originalmedia.explore.fragment.GraphicFragment
import top.gochiusa.originalmedia.explore.fragment.VideoFragment
import top.gochiusa.originalmedia.explore.fragment.VisualWorldFragment

class CreateAdapter(
    private val mFragmentManager: FragmentManager, //声明标题文本队列
) :
    FragmentStatePagerAdapter(mFragmentManager) {


    //获取指定位置的碎片fragment
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                GraphicCreateFragment()
            }
            1 -> {
                StartFragment()
            }
            2 -> {
                VideoCreateFragment()
            }
            else -> StartFragment()
        }
    }

    //获取fragment的个数
    override fun getCount() =3


}