package top.gochiusa.originalmedia.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseFragment: Fragment() {


    fun toast(string: String){
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(),string,Toast.LENGTH_SHORT).show()
        }
    }


}
