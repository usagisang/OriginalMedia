package top.gochiusa.originalmedia.account.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_account.*
import top.gochiusa.originalmedia.R
import top.gochiusa.originalmedia.account.viewmodel.AccountViewMode
import top.gochiusa.originalmedia.base.BaseFragment
import top.gochiusa.originalmedia.databinding.FragmentAccountBinding
import top.gochiusa.originalmedia.util.Constant


class AccountFragment : BaseFragment() {
    private val mVpAccount by lazy { ViewModelProvider(this)[AccountViewMode::class.java] }
    private lateinit var mBinding: FragmentAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        return mBinding.root;
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initData()

    }

    private fun initListener() {
        btn_login.setOnClickListener {
            login()
        }

    }


    private fun initData() {
        mBinding.user = mVpAccount
        mBinding.lifecycleOwner = this
        mVpAccount.userExposeLiveData.observe(viewLifecycleOwner, Observer {
            val user = it.getOrNull()

            toast("$user")
            if (user?.code == 1) {
                Toast.makeText(requireContext(), "登录成功！", Toast.LENGTH_SHORT).show()
                group_login_after.visibility = Group.VISIBLE
                group_login_before.visibility = Group.INVISIBLE
                tv_name.text = user.username
                Constant.LOGIN_USER_ID = user?.userId
            } else {
                Toast.makeText(requireContext(), "登录失败！", Toast.LENGTH_SHORT).show()
                group_login_after.visibility = Group.INVISIBLE
                group_login_before.visibility = Group.VISIBLE
            }
        })
    }

    private fun login() {
        mVpAccount.getLogin(ed_id.text.toString(), ed_password.text.toString())


    }
}