package top.gochiusa.originalmedia.base

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import java.lang.reflect.ParameterizedType


abstract class BaseActivity<VB : ViewDataBinding>  : AppCompatActivity() , BaseBinding<VB>{
    internal val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        getViewBinding(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        mBinding.initBinding()
        ImmersionBar.with(this)
            .init()
    }


    /**
     * 沉浸式处理
     */
    private fun immersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                //清除透明状态栏标识
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                //添加绘制状态栏标识
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                // 添加透明导航标识
                addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                //设置状态栏的颜色为透明
                statusBarColor = Color.TRANSPARENT
            }
            findViewById<ViewGroup>(android.R.id.content).apply {
                //遍历根布局的子布局
                for (index in 0 until childCount) {
                    val child = getChildAt(index) as? ViewGroup
                    //让布局根据系统窗口来调整自己的布局
                    child?.let {
                        it.fitsSystemWindows = true
                        it.clipToPadding = true
                    }
                }
            }

        } else {
            //Android4.4这个设置就能实现沉浸式效果
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}

inline fun <VB: ViewBinding> Any.getViewBinding(inflater: LayoutInflater, position:Int = 0):VB{
    val vbClass =  (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
    val inflate = vbClass[position].getDeclaredMethod("inflate", LayoutInflater::class.java)
    return  inflate.invoke(null, inflater) as VB
}

inline fun <VB: ViewBinding> Any.getViewBinding(
    inflater: LayoutInflater, container: ViewGroup?,
    position:Int = 0):VB{
    val vbClass =  (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
    val inflate = vbClass[position].getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    return inflate.invoke(null, inflater, container, false) as VB
}