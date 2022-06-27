package top.gochiusa.originalmedia

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.bottom_bar.*
import top.gochiusa.originalmedia.account.fragment.AccountFragment
import top.gochiusa.originalmedia.base.BaseActivity
import top.gochiusa.originalmedia.creation.fragment.CreationFragment
import top.gochiusa.originalmedia.databinding.ActivityLauncherBinding
import top.gochiusa.originalmedia.explore.fragment.ExploreFragment


class LauncherActivity : BaseActivity<ActivityLauncherBinding>() {


    private var mExploreFragment: ExploreFragment? = null
    private var mCreationFragment: CreationFragment? = null
    private var mAccountFragment: AccountFragment? = null


    override fun ActivityLauncherBinding.initBinding() {
        getPer()
        init()
        setFragment(0)
    }


    private fun init() {
        //startActivity(Intent(this, OriginActivity::class.java))

//        println("$iv_bottom_explore 为什么我我我我 ")
//        iv_bottom_explore.setOnClickListener {
//            setFragment(0)
//        }
//        iv_bottom_creation.setOnClickListener {
//            setFragment(1)
//        }
//        iv_bottom_personal.setOnClickListener {
//            setFragment(2)
//        }
    }

    private fun setFragment(index: Int) {
        //获取Fragment管理器
        val mFragmentManager: FragmentManager = supportFragmentManager
        //开启事务
        val mTransaction: FragmentTransaction = mFragmentManager.beginTransaction()
        //隐藏所有Fragment
        hideFragments(mTransaction)
        when (index) {
            0 -> {
                iv_bottom_explore.setColorFilter(Color.WHITE)
                iv_bottom_creation.setColorFilter(Color.GRAY)
                iv_bottom_personal.setColorFilter(Color.GRAY)
                if (mExploreFragment == null) {
                    mExploreFragment = ExploreFragment()
                    mTransaction.add(
                        R.id.container, mExploreFragment!!,

                        )
                } else {
                    mTransaction.show(mExploreFragment!!)
                }
            }
            2 -> {
                iv_bottom_explore.setColorFilter(Color.GRAY)
                iv_bottom_creation.setColorFilter(Color.GRAY)
                iv_bottom_personal.setColorFilter(Color.WHITE)
                if (mAccountFragment == null) {
                    mAccountFragment = AccountFragment()
                    mTransaction.add(
                        R.id.container, mAccountFragment!!,
                    )
                } else {
                    mTransaction.show(mAccountFragment!!)
                }
            }
            1 -> {
                iv_bottom_explore.setColorFilter(Color.GRAY)
                iv_bottom_creation.setColorFilter(Color.WHITE)
                iv_bottom_personal.setColorFilter(Color.GRAY)
                if (mCreationFragment == null) {
                    mCreationFragment = CreationFragment()
                    mTransaction.add(
                        R.id.container, mCreationFragment!!,
                    )
                } else {
                    mTransaction.show(mCreationFragment!!)
                }
            }
            else -> {}
        }
        //提交事务
        mTransaction.commit()
    }


    private fun hideFragments(transaction: FragmentTransaction) {
        if (mExploreFragment != null) {
            //隐藏Fragment
            transaction.hide(mExploreFragment!!)
            //将对应菜单栏设置为默认状态

        }
        if (mAccountFragment != null) {
            transaction.hide(mAccountFragment!!)

        }

        if (mCreationFragment != null) {
            transaction.hide(mCreationFragment!!)

        }
    }

    private fun getPer() {
        val permission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
        }
    }


}