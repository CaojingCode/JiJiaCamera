package com.caojing.cameralibrary.activity

import android.graphics.Bitmap
import android.graphics.PointF
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.util.createOrExistsDir
import com.caojing.cameralibrary.util.getNowString
import com.caojing.cameralibrary.util.showToast
import com.caojing.cameralibrary.view.RecordButtonCallBack
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import kotlinx.android.synthetic.main.camera_layot.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by Caojing
 * 2019/12/211731
 * 不为往事扰，余生自愿笑
 */
class JiJiaCameraActivity : AppCompatActivity() {

    private var fragments = mutableListOf<Fragment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_layot)
    }


    fun initView() {
        cameraKitView.setLifecycleOwner(this)
        cameraKitView.mode = Mode.VIDEO
//        cameraKitView.audioBitRate
        cameraKitView.playSounds = true //快门，对焦声音
        cameraKitView.flash = Flash.AUTO //闪光灯自动开启
        cameraKitView.mapGesture(Gesture.PINCH, GestureAction.ZOOM) //手势缩放
        cameraKitView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS) // 点击获取焦点
//        cameraKitView.setLocation() //视频拍摄位置记录
        cameraKitView.addCameraListener(object : CameraListener() {

            override fun onVideoTaken(result: VideoResult) {
                //视频结果
                setExif(result.file.path)
                var bitmap = getImage(result.file.path)

                ivBg.setImageBitmap(bitmap)
                val scaleIaAimation = ScaleAnimation(
                    1f, (ivVideo.width.toFloat() / ivBg.width.toFloat()),
                    1f, ivVideo.height.toFloat() / ivBg.height.toFloat()
                )

                val animation = TranslateAnimation(
                    Animation.ABSOLUTE, 0f,
                    Animation.ABSOLUTE, ivVideo.x,
                    Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, ivVideo.y
                )
                var animSet = AnimationSet(false)
                animSet.fillAfter = true
                animSet.duration = 300
                animSet.addAnimation(scaleIaAimation)
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
                showToast(orientation.toString())
            }

            //自动对焦开始
            override fun onAutoFocusStart(point: PointF) {
                super.onAutoFocusStart(point)
            }

            //自动对焦结束
            override fun onAutoFocusEnd(successful: Boolean, point: PointF) {
                super.onAutoFocusEnd(successful, point)
            }
        })

        btnVideo.callBack = object : RecordButtonCallBack {
            override fun startRecord() {
                if (cameraKitView.isTakingVideo) {
                    return
                }
                var path = getTempFilePath()
                cameraKitView.takeVideo(File(path))
            }

            override fun recordFinsh() {
                cameraKitView.stopVideo()
            }

        }

    }

    /**
     * 獲取視頻圖片
     */
    fun getImage(mPath: String): Bitmap {
        var media = MediaMetadataRetriever()
        media.setDataSource(mPath)
        //frameTime的单位为us微秒
        return media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
    }

    /**
     * 创建文件路径
     */
    private fun getTempFilePath(): String {
        val fileDir = String.format(
            Locale.getDefault(),
            "%s/JiJiaRecord/",
            Environment.getExternalStorageDirectory().absolutePath
        )
        if (!fileDir.createOrExistsDir()) {
            Log.e("文件夹创建失败：%s", fileDir)
        }
        val fileName = String.format(
            Locale.getDefault(), "record_%s","yyyyMMddHHmmss".getNowString()
        )
        return String.format(Locale.getDefault(), "%s%s.mp4", fileDir, fileName)
    }

    /**
     * 设置exif信息
     */
    fun setExif(filepath: String) {
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(filepath)     //根据图片的路径获取图片的Exif
        } catch (ex: IOException) {
            Log.e("Mine", "cannot read exif", ex)
        }
        if (exif == null)
            return
        exif.setAttribute(
            ExifInterface.TAG_DATETIME,
            "yyyy-MM-dd HH:mm:ss".getNowString()
        )              //把时间写进exif
        exif.setAttribute(ExifInterface.TAG_MODEL, android.os.Build.MODEL)             //设备型号
        try {
            exif.saveAttributes()         //最后保存起来
        } catch (e: IOException) {
            Log.e("Mine", "cannot save exif", e)
        }

    }

}