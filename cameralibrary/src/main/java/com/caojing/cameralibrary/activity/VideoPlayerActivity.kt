package com.caojing.cameralibrary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.TimeUtils
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.bean.VideoBean
import com.caojing.cameralibrary.util.loadVideoImage
import com.caojing.cameralibrary.view.JiJiaStandardGSYVideoPlayer
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.android.synthetic.main.activity_video_player.*
import java.text.DateFormat
import java.text.SimpleDateFormat


/**
 *
 * Created by Caojing
 * 2020/1/21529
 * 不为往事扰，余生自愿笑
 */
class VideoPlayerActivity : GSYBaseActivityDetail<JiJiaStandardGSYVideoPlayer>() {

    var videoBean: VideoBean = VideoBean()


    /**
     * 点击了全屏
     */
    override fun clickForFullScreen() {
    }

    /**
     * 是否启动旋转横屏，true表示启动
     */
    override fun getDetailOrientationRotateAuto(): Boolean {
        return true
    }

    /**
     * 播放控件
     */
    override fun getGSYVideoPlayer(): JiJiaStandardGSYVideoPlayer {
        return videoView
    }

    /**
     * 配置播放器
     */
    override fun getGSYVideoOptionBuilder(): GSYVideoOptionBuilder {

        val imageView = ImageView(this)

        imageView.loadVideoImage(videoBean.videoPath)
        return GSYVideoOptionBuilder()
            .setThumbImageView(imageView)
            .setUrl(videoBean.videoPath)
            .setCacheWithPlay(true)
            .setVideoTitle(" ")
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setNeedShowWifiTip(false)
            .setSeekRatio(1f)
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QMUIStatusBarHelper.translucent(this)
        setContentView(R.layout.activity_video_player)
        videoBean = intent.getSerializableExtra("videoBean") as VideoBean
        initVideoBuilderMode()
        videoView.backButton.visibility = View.GONE
        var isSelect = intent.getBooleanExtra("isSelect", false)
        tvTime.text =
            "拍摄时间：${TimeUtils.millis2String(videoBean.videoTimestamp, "yyyy/MM/dd HH:mm")}"
        var videoAddress = videoBean.videoAddress
        if (videoAddress == "null") {
            videoAddress = "-"
        }
        tvAddress.text = "拍摄位置：${videoAddress}"

        var deviceType = videoBean.deviceType
        if (deviceType == "null" || deviceType.isEmpty()) {
            deviceType = "-"
        }
        tvDevType.text = "设备型号：${deviceType}"

        if (videoBean.isOnlyPlay) {
            //播放
            llPlayerBottom.visibility = View.GONE
            btnUpdate.visibility = View.GONE
        } else {
            if (isSelect){
                //上传
                llPlayerBottom.visibility = View.GONE
            }else{
                //删除
                btnUpdate.visibility = View.GONE
            }

        }

        btnUpdate.setOnClickListener {
            //上传开始
            val intent = Intent()
            intent.putExtra("videoBean", videoBean)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        llPlayerBottom.setOnClickListener {
            //删除视频
            JiJiaFragmentDialog.create()
                .setCancelOutSide(true)
                .multipleBtn()//两个按钮
                .setMessage("是否确认删除带看视频？")
                .show(supportFragmentManager)
                .setDialogCallBack(object :JiJiaFragmentDialog.DialogCallBack{
                    override fun btnOk() {
                        FileUtils.delete(videoBean.videoPath)
                        val intent = Intent()
                        intent.putExtra("videoBean", videoBean)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }

                })


//            AlertDialog.Builder(this)
//                .setMessage("是否确认删除带看视频")
//                .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->
//                    dialog.dismiss()
//                })
//                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
//                    FileUtils.delete(videoBean.videoPath)
//                    val intent = Intent()
//                    intent.putExtra("videoBean", videoBean)
//                    setResult(Activity.RESULT_OK, intent)
//                    finish()
//                }).create().show()
        }

        ivBack.setOnClickListener { finish() }

    }


}