package com.kokomi.origin.creation.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.explore.tabBarHeight
import com.kokomi.origin.navigationHeight
import com.kokomi.origin.util.find
import com.kokomi.origin.util.navigationBarHeight
import com.kokomi.origin.util.statusBarHeight

class CreationStartFragment(
    private val changePager: (Int) -> Unit
) : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_creation_start_origin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            find<ComposeView>(R.id.compose_creation_start_content) {
                setContent {
                    CreationStartContentView(
                        statusBarHeight,
                        { changePager(0) },
                        { changePager(2) }
                    )
                }
            }

            find<TextView>(R.id.tv_creation_start_status_bar) {
                height = statusBarHeight
            }

            find<TextView>(R.id.tv_creation_start_navigation_bar) {
                height = navigationBarHeight + statusBarHeight
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(changePager: (Int) -> Unit) = CreationStartFragment(changePager)
    }
}