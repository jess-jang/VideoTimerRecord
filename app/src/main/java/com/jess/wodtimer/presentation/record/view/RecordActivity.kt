package com.jess.wodtimer.presentation.record.view

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.jess.wodtimer.R
import com.jess.wodtimer.common.base.BaseActivity
import com.jess.wodtimer.common.extension.setMargin
import com.jess.wodtimer.common.extension.setMarginBottom
import com.jess.wodtimer.common.manager.CameraManager
import com.jess.wodtimer.common.manager.PermissionManager
import com.jess.wodtimer.common.util.DeviceUtils
import com.jess.wodtimer.databinding.RecordActivityBinding
import com.jess.wodtimer.presentation.record.viewmodel.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.record_activity.*
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author jess
 * @since 2020.08.11
 */
@AndroidEntryPoint
class RecordActivity : BaseActivity<RecordActivityBinding, RecordViewModel>(),
    View.OnClickListener {

    override val layoutRes get() = R.layout.record_activity
    override val viewModelClass get() = RecordViewModel::class

    private val cameraManager: CameraManager by lazy {
        CameraManager(this, camera)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setOrientationMargin()
    }

    override fun initLayout() {
        arrayOf(cl_record, cl_stop, iv_gallery, iv_setting).forEach {
            it.setOnClickListener(this)
        }
    }

    override fun onCreated(savedInstanceState: Bundle?) {
        initObserve()
        checkPermission()
    }

    private fun initObserve() {

    }

    override fun onResume() {
        super.onResume()
        setOrientationMargin()
        cameraManager.onResume()
    }

    override fun onPause() {
        cameraManager.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        cameraManager.onDestroy()
        super.onDestroy()
    }

    /**
     * 가로, 세로에 따른 마진 설정
     */
    private fun setOrientationMargin() {
        // 네비게이션 바 높이 만큼 마진 설정
        if (DeviceUtils.isOrientationPortrait(this)) {
            // 세로
            cl_bottom.setMargin(bottom = DeviceUtils.getNavigationBarHeight(this))
        } else {
            // 가로
            cl_bottom.setMargin(right = DeviceUtils.getNavigationBarHeight(this))
        }
    }

    /**
     * 퍼미션 체크
     */
    private fun checkPermission(listener: (() -> Unit)? = null) {
        // 권한 요청
        PermissionManager.request(
            this,
            Manifest.permission.CAMERA,
            getString(R.string.permission_should_allow_camera)
        ) {
            cameraManager.init()
            listener?.invoke()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cl_record -> {
                // 녹화
                checkPermission {
                    vm.onRecord()
                }
            }

            R.id.cl_stop -> {
                // 정지
                vm.onStop()
            }

            R.id.iv_gallery -> {
                // 갤러리
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("content://media/internal/images/media")
                    )
                )
            }

            R.id.iv_setting -> {
                // 설정
                SettingBottomDialog(
                    this
                ).run {
                    show(vm.title.value) { title ->
                        vm.setTitle(title)
                    }
                }
            }

        }
    }
}
