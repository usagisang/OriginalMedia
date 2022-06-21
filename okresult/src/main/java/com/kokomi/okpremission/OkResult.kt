package com.kokomi.okpremission

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

object OkResult {

    /**
     * 在 [ComponentActivity] 中申请权限
     *
     * @param permissions 要申请的权限列表
     * @param onAgree 当权限被同意时回调，函数的 [String] 参数为申请成功的权限
     * @param onRefuse 当权限被拒绝时回调，函数的 [String] 参数为申请失败的权限
     * */
    fun ComponentActivity.requirePermission(
        permissions: Array<String>,
        onAgree: (String) -> Unit,
        onRefuse: (String) -> Unit
    ) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            results.map { result ->
                if (result.value) onAgree(result.key) else onRefuse(result.key)
            }
        }.launch(permissions)
    }

    /**
     * 在 [Fragment] 中申请权限
     *
     * @param permissions 要申请的权限列表
     * @param onAgree 当权限被同意时回调，函数的 [String] 参数为申请成功的权限
     * @param onRefuse 当权限被拒绝时回调，函数的 [String] 参数为申请失败的权限
     * */
    fun Fragment.requirePermission(
        permissions: Array<String>,
        onAgree: (String) -> Unit,
        onRefuse: (String) -> Unit
    ) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            results.map { result ->
                if (result.value) onAgree(result.key) else onRefuse(result.key)
            }
        }.launch(permissions)
    }

    /**
     * 在 [ComponentActivity] 中开启新的 Activity 并截取返回的结果
     *
     * @param intent [Intent] 指定要打开的活动
     * @param operation 当打开的活动结束时，回调此函数，函数传入的 [Int] 参数为 resultCode
     * */
    fun ComponentActivity.startActivityForResult(
        intent: Intent,
        operation: (Int, Intent?) -> Unit
    ) {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            operation(result.resultCode, result.data)
        }.launch(intent)
    }

    /**
     * 在 [Fragment] 中开启新的 Activity 并截取返回的结果
     *
     * @param intent [Intent] 指定要打开的活动
     * @param operation 当打开的活动结束时，回调此函数，函数传入的 [Int] 参数为 resultCode
     * */
    fun Fragment.startActivityForResult(
        intent: Intent,
        operation: (Int, Intent?) -> Unit
    ) {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            operation(result.resultCode, result.data)
        }.launch(intent)
    }

}