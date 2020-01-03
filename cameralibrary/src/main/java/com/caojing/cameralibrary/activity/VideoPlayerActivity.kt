package com.caojing.cameralibrary.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.FileUtils
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.bean.VideoBean
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.android.synthetic.main.activity_video_player.*


/**
 *
 * Created by Caojing
 * 2020/1/21529
 * 不为往事扰，余生自愿笑
 */
class VideoPlayerActivity : GSYBaseActivityDetail<StandardGSYVideoPlayer>() {

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
    override fun getGSYVideoPlayer(): StandardGSYVideoPlayer {
        return videoView
    }

    /**
     * 配置播放器
     */
    override fun getGSYVideoOptionBuilder(): GSYVideoOptionBuilder {

        val imageView = ImageView(this)
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

        tvTime.text = "拍摄时间：${videoBean.videoTimestamp}"
        tvAddress.text = "拍摄位置：${videoBean.videoAddress}"
        tvDevType.text = "设备型号：${videoBean.deviceType}"

        llPlayerBottom.setOnClickListener {
            //删除视频
            AlertDialog.Builder(this)
                .setMessage("是否确认删除带看视频")
                .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                    FileUtils.delete(videoBean.videoPath)
                    finish()
                }).create().show()
        }

        ivBack.setOnClickListener { finish() }
    }


}