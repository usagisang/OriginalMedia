package com.kokomi.origin.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import com.kokomi.origin.R
import com.kokomi.origin.base.BaseFragment
import com.kokomi.origin.base.ModelFragment
import com.kokomi.origin.navigationHeight
import com.kokomi.origin.util.find

class UserFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_user_origin, container, false).apply {
            viewModel<UserViewModel> {
                requireContext().loadUserFromDataStore()

                find<ComposeView>(R.id.compose_user_content) {
                    setContent { UserContentView(requireContext(), this@viewModel) }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(view) {
            find<TextView>(R.id.tv_user_navigation) {
                height = navigationHeight
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ModelFragment()
    }

}