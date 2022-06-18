package com.kokomi.carver.core.camerax

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ZoomState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kokomi.carver.checkMainThread
import com.kokomi.carver.core.Captor

/**
 * CameraX [Captor] 实现的抽象层
 * */
abstract class CameraXCaptor : Captor<PreviewView, CameraXConfiguration, ZoomState>() {

    protected abstract var previewView: PreviewView?

    protected abstract var cameraProvider: ProcessCameraProvider?

    protected abstract var camera: Camera?

    final override fun shutdown() {
        checkMainThread()
        // 解绑所有绑定的使用案例，然后销毁
        cameraProvider?.unbindAll()
        cameraProvider?.shutdown()
    }

    final override fun onConfigurationChanged(newConfig: CameraXConfiguration) {
        checkMainThread()
        // 更新配置，然后重新构建
        config = newConfig
        prepareInternal()
    }

    final override fun bindPreview(preview: PreviewView) {
        checkMainThread()
        previewView = preview
    }

    final override fun prepare() {
        checkMainThread()
        prepareInternal()
    }

    final override fun changeLensFacing() {
        checkMainThread()
        val facing = if (config.lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        config = config.copy(lensFacing = facing)
        onConfigurationChanged(config)
    }

    final override fun zoom(): LiveData<ZoomState> {
        checkMainThread()
        return camera?.cameraInfo?.zoomState ?: MutableLiveData()
    }

    final override fun zoom(zoom: Float) {
        checkMainThread()
        camera?.run { cameraControl.setLinearZoom(zoom) }
    }

    final override fun focus(x: Float, y: Float) {
        // 不检查线程，因为在获取计量点工厂时会检查
        val preview = previewView ?: return
        val control = camera?.cameraControl ?: return
        val meteringPoint = preview.meteringPointFactory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(meteringPoint)
            .disableAutoCancel()
            .build()
        control.startFocusAndMetering(action)
    }

    final override fun cancelFocus() {
        val control = camera?.cameraControl ?: return
        control.cancelFocusAndMetering()
    }

    /**
     * CameraX 的准备实现
     * */
    protected abstract fun prepareInternal()

}