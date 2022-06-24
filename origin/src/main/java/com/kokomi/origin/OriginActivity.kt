package com.kokomi.origin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kokomi.origin.explore.ExploreFragment
import com.kokomi.origin.user.UserFragment
import com.kokomi.origin.util.clearSystemBar
import com.kokomi.origin.util.keepScreenAlive
import com.kokomi.origin.util.navigationBarHeight
import com.kokomi.origin.util.find
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val EXPLORE = "explore"
private const val CREATION = "creation"
private const val USER = "user"

internal var navigationHeight = 0

internal lateinit var appContext: Context

class OriginActivity : AppCompatActivity() {

    private var lastFragment: Fragment? = null
    private var exploreFragment: ExploreFragment? = null
    private var userFragment: UserFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_origin)

        appContext = applicationContext

        clearSystemBar(navigationBar = find(R.id.tv_origin_navigation_bar))

        find<ImageView>(R.id.iv_origin_explore) {
            setOnClickListener { changeFragment(EXPLORE) }
        }

        find<ImageView>(R.id.iv_origin_creation) {
            setOnClickListener { changeFragment(CREATION) }
        }

        find<ImageView>(R.id.iv_origin_user) {
            setOnClickListener { changeFragment(USER) }
        }

        changeFragment(EXPLORE)

        lifecycleScope.launch {
            delay(1L)
            find<LinearLayout>(R.id.ll_origin_navigation) {
                navigationHeight = height + navigationBarHeight
            }
        }
    }

    override fun onResume() {
        super.onResume()
        keepScreenAlive(true)
    }

    override fun onPause() {
        super.onPause()
        keepScreenAlive(false)
    }

    private fun changeFragment(tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        lastFragment?.run { transaction.hide(this) }
        when (tag) {
            EXPLORE -> {
                if (exploreFragment == null)
                    ExploreFragment().run {
                        transaction.add(R.id.fl_origin_fragment, this)
                        exploreFragment = this
                    }
                else transaction.show(exploreFragment!!)
                lastFragment = exploreFragment
            }
            CREATION -> {

            }
            USER -> {
                if (userFragment == null)
                    UserFragment().run {
                        transaction.add(R.id.fl_origin_fragment, this)
                        userFragment = this
                    }
                else transaction.show(userFragment!!)
                lastFragment = userFragment
            }
            else -> {}
        }
        transaction.commit()
    }

}