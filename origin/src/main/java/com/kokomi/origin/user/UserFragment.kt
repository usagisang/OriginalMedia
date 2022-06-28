package com.kokomi.origin.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.base.ModelFragment
import com.kokomi.origin.navigationHeight
import com.kokomi.origin.util.find
import kotlinx.coroutines.launch

class UserFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_user_origin, container, false).apply {
            viewModel<UserViewModel> {
                find<ComposeView>(R.id.compose_user_content) {
                    setContent { UserContentView(requireContext(), this@viewModel) }
                }

                loadUser()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            find<TextView>(R.id.tv_user_navigation) {
                lifecycleScope.launch {
                    navigationHeight.collect { height = it }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ModelFragment()
    }

}