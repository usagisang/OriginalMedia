package top.gochiusa.originalmedia.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<VB : ViewDataBinding> : Fragment(), BaseBinding<VB> {
    var mBinding: VB? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding =  getViewBinding(inflater, container,0)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.initBinding()
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (::mBinding.isInitialized){
//            mBinding?.unbind()
//        }
        if (mBinding!= null){
            mBinding?.unbind()
        }
    }

}