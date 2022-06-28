package com.kokomi.origin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kokomi.origin.base.loggedUser
import com.kokomi.origin.creation.CreationFragment
import com.kokomi.origin.datastore.loadUser
import com.kokomi.origin.explore.ExploreFragment
import com.kokomi.origin.user.UserFragment
import com.kokomi.origin.util.*
import com.kokomi.origin.util.clearSystemBar
import com.kokomi.origin.util.find
import com.kokomi.origin.util.keepScreenAlive
import com.kokomi.origin.util.navigationBarHeight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val EXPLORE = "explore"
private const val CREATION = "creation"
private const val USER = "user"

private val _navigationHeight = MutableStateFlow(0)
internal var navigationHeight: StateFlow<Int> = _navigationHeight

internal lateinit var appContext: Context

class OriginActivity : AppCompatActivity() {

    private var lastFragment: Fragment? = null
    private var exploreFragment: ExploreFragment? = null
    private var creationFragment: CreationFragment? = null
    private var userFragment: UserFragment? = null

    private lateinit var navigation: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_origin)

        navigation = this find R.id.ll_origin_navigation

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

        navigation.viewTreeObserver
            .addOnGlobalLayoutListener {
                lifecycleScope.launch {
                    _navigationHeight emit navigation.height + navigationBarHeight
                }
            }

        changeFragment(EXPLORE)

        loadUserFromDataStore()

    }

    override fun onResume() {
        super.onResume()
        keepScreenAlive(true)
        if (lastFragment is ExploreFragment) exploreFragment?.onShow()
    }

    override fun onPause() {
        super.onPause()
        keepScreenAlive(false)
        if (lastFragment is ExploreFragment) exploreFragment?.onHide()
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
                if (creationFragment == null)
                    CreationFragment().run {
                        transaction.add(R.id.fl_origin_fragment, this)
                        creationFragment = this
                    }
                else transaction.show(creationFragment!!)
                lastFragment = creationFragment
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
        if (lastFragment !is ExploreFragment) exploreFragment!!.onHide()
        else exploreFragment!!.onShow()
        transaction.commit()
    }

    private fun loadUserFromDataStore() {
        lifecycleScope.launch {
            loadUser().collect { user ->
                user?.let { loggedUser = user }
            }
        }
    }

}