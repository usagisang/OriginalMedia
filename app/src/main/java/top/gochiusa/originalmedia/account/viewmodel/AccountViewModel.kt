package top.gochiusa.originalmedia.account.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import top.gochiusa.originalmedia.account.bean.User
import top.gochiusa.originalmedia.account.repository.AccountRepository

class AccountViewMode: ViewModel() {
    private val loginLiveData = MutableLiveData<LoginData>()

    var user :User? =null

    val userExposeLiveData = Transformations.switchMap(loginLiveData){
        AccountRepository.getLogin(it.username,it.password)
    }

    fun getLogin( username:String, password:String){
        loginLiveData.value = LoginData(username,password)
    }

    data class LoginData(var username:String,var password:String)
}