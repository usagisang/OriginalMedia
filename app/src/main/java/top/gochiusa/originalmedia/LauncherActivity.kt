package top.gochiusa.originalmedia


import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import top.gochiusa.originalmedia.base.BaseActivity
import top.gochiusa.originalmedia.databinding.ActivityLauncherBinding
import top.gochiusa.originalmedia.explore.adapter.PageAdapter

class LauncherActivity : BaseActivity<ActivityLauncherBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun ActivityLauncherBinding.initBinding() {
        val navController = Navigation.findNavController(this@LauncherActivity, R.id.nav_host)
        val navigationView = mBinding.navView
        NavigationUI.setupWithNavController(navigationView, navController);


    }
















    private fun changeTabSize(){
        //字体大小目前变化不了
/*
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.apply {
                        customView?.findViewById<TextView>(R.id.tv_top_item)?.isSelected = true
                        val tv = customView?.findViewById<TextView>(R.id.tv_top_item)
                        tv?.apply {
                            typeface = Typeface.defaultFromStyle(Typeface.BOLD)//加粗
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 22F)//直接用setTextSize(22)也一样
                            alpha = 0.9f//透明度
                            invalidate()

                        }

                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                    tab?.apply {
                        customView?.findViewById<TextView>(R.id.tv_top_item)?.isSelected = true
                        val tv = customView?.findViewById<TextView>(R.id.tv_top_item)
                        tv?.apply {
                            typeface = Typeface.defaultFromStyle(Typeface.BOLD)//加粗
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                            alpha = 0.6f;

                            invalidate()

                        }

                    }

                }

                @RequiresApi(Build.VERSION_CODES.M)
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })
*/
    }


}