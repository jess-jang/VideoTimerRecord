package com.jess.wodtimer.presentation.record

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.jess.wodtimer.R
import com.jess.wodtimer.common.base.BaseActivity
import com.jess.wodtimer.common.manager.CameraManager
import com.jess.wodtimer.common.manager.PermissionManager
import com.jess.wodtimer.common.util.DeviceUtils
import com.jess.wodtimer.databinding.RecordActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.record_activity.*
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

    private val cameraExecutor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    override fun initLayout() {
        arrayOf(cl_record, iv_gallery, iv_setting).forEach {
            it.setOnClickListener(this)
        }
    }

    override fun onCreated(savedInstanceState: Bundle?) {
        initObserve()
        checkPermission()
    }

    private fun initObserve() {
        vm.isPlay.observe(this, Observer {

        })
    }

    override fun onResume() {
        super.onResume()
        DeviceUtils.setImmersiveMode(this)
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
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

            }

        }
    }
}
