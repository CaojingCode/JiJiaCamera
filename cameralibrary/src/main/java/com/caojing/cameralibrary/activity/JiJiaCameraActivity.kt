package com.caojing.cameralibrary.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PointF
import android.location.LocationManager
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import com.blankj.utilcode.util.*
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.bean.VideoBean
import com.caojing.cameralibrary.util.*
import com.caojing.cameralibrary.view.RecordButtonCallBack
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.camera_layot.*
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.util.*


/**
 *
 * Created by Caojing
 * 2019/12/211731
 * 不为往事扰，余生自愿笑
 */
@ExperimentalCoroutinesApi
class JiJiaCameraActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    var videoAddress: String? = null

    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == VideoAddressAction) {
                //接收视频拍摄地址，
                videoAddress = intent.getStringExtra(VideoAddress)
            }
        }
    }

    /**
     * 更新视频背景
     */
    private fun updateVideoBg() {
        launch(Dispatchers.Main) {
            val files = getVideoFiles()
            if (files.size > 1) {
                val file = files[1]
                ivVideo.loadVideoImage(file.videoPath)
            } else {
                ivVideo.loadVideoImage("")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QMUIStatusBarHelper.translucent(this)
        setContentView(R.layout.camera_layot)
        initView()
        var intentFilter = IntentFilter()
        intentFilter.addAction(VideoAddressAction)
        registerReceiver(broadcastReceiver, intentFilter)

        updateVideoBg()

        ivBack.setOnClickListener { finish() }
        ivTopBack.setOnClickListener { finish() }
    }

    private fun initView() {
        cameraKitView.setLifecycleOwner(this)
        cameraKitView.mode = Mode.VIDEO
//        cameraKitView.audioBitRate
        cameraKitView.playSounds = false //快门，对焦声音
        cameraKitView.flash = Flash.AUTO //闪光灯自动开启
        cameraKitView.mapGesture(Gesture.PINCH, GestureAction.ZOOM) //手势缩放
        cameraKitView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS) // 点击获取焦点

        cameraKitView.addCameraListener(object : CameraListener() {

            override fun onVideoTaken(result: VideoResult) {
                //获取视频文件信息，从信息文件中取到所有视频的信息json字符串
                val videoInfoList = getVideoInfoList()

                //将本次拍摄的视频相关信息添加到信息文件转换的集合中
                val videoBean = VideoBean()
                videoBean.videoAddress = videoAddress.toString()
                videoBean.videoTimestamp = TimeUtils.getNowMills()
                videoBean.videoPath = result.file.path
                videoBean.videoDuration = getVideoDuration(result.file.path)
                videoBean.deviceType = DeviceUtils.getModel()
                videoInfoList.add(videoBean)
                //将所有的视频信息集合转换成json字符串
                val videoJson = GsonUtils.toJson(videoInfoList)
                //将json 字符串写到文件中并保存
                FileIOUtils.writeFileFromString(getTempStringPath(), videoJson)

                //根据文件路径转换成bitmap
                val bitmap = getVideoImage(result.file.path)
                ivBg.setImageBitmap(bitmap)
                val scaleIaAnimation = ScaleAnimation(
                    1f, (ivVideo.width.toFloat() / ivBg.width.toFloat()),
                    1f, ivVideo.height.toFloat() / ivBg.height.toFloat()
                )

                val animation = TranslateAnimation(
                    Animation.ABSOLUTE, 0f,
                    Animation.ABSOLUTE, ivVideo.x,
                    Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, ivVideo.y
                )
                val animSet = AnimationSet(false)
                animSet.fillAfter = true
                animSet.duration = 300
                animSet.addAnimation(scaleIaAnimation)
                animSet.addAnimation(animation)
                ivBg.startAnimation(animSet)
                animSet.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        ivVideo.setImageBitmap(bitmap)
                    }

                    override fun onAnimationStart(animation: Animation?) {

                    }

                })
            }


            //摄像头旋转要执行的操作，逆时针
            override fun onOrientationChanged(orientation: Int) {
//                showToast("摄像头旋转了$orientation 度")
            }

            //自动对焦开始
//            override fun onAutoFocusStart(point: PointF) {
//                super.onAutoFocusStart(point)
//            }
//
//            //自动对焦结束
//            override fun onAutoFocusEnd(successful: Boolean, point: PointF) {
//                super.onAutoFocusEnd(successful, point)
//            }
        })

        btnVideo.callBack = object : RecordButtonCallBack {
            override fun updateTime(time: Int) {
                var longTime = time * 1000.toLong()
                if (longTime >= 1000) {
                    longTime -= 1000
                }
                tvTime.text = longTime.getTimeMString()
                if (time == 60) {
                    if (isFastClick()) {
                        JiJiaFragmentDialog.create().setCancelOutSide(true)
                            .singleBtn()//两个按钮
                            .setMessage("最长可录制60秒视频")
                            .show(supportFragmentManager)
                    }
                }
            }

            override fun startRecord() {
                if (cameraKitView.isTakingVideo) {
                    return
                }
                val path = getTempFilePath()
                cameraKitView.takeVideo(File(path))
                tvTime.visibility = View.VISIBLE
            }

            override fun recordFinsh() {
                cameraKitView.stopVideo()
                tvTime.visibility = View.GONE
            }

        }

        ivVideo.setOnClickListener {
            startActivity(Intent(this, VideosActivity::class.java))
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cancel()//取消协程
        unregisterReceiver(broadcastReceiver)
    }


    override fun onResume() {
        super.onResume()
        updateVideoBg()
    }
}