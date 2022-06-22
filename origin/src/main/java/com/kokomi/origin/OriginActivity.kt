package com.kokomi.origin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kokomi.origin.explore.ExploreFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val EXPLORE = "explore"
private const val CREATION = "creation"
private const val USER = "user"

internal var navigationHeight = 0

class OriginActivity : AppCompatActivity() {

    private var lastFragment: Fragment? = null
    private var exploreFragment: ExploreFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_origin)

        clearSystemWindows()

        view<ImageView>(R.id.iv_origin_explore) {
            setOnClickListener { changeFragment(EXPLORE) }
        }

        view<ImageView>(R.id.iv_origin_creation) {
            setOnClickListener { changeFragment(CREATION) }
        }

        view<ImageView>(R.id.iv_origin_user) {
            setOnClickListener { changeFragment(USER) }
        }

        changeFragment(EXPLORE)

        lifecycleScope.launch {
            delay(5L)
            view<LinearLayout>(R.id.ll_origin_navigation) {
                navigationHeight = height
            }
        }
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

            }
            else -> {}
        }
        transaction.commit()
    }

}