package com.caojing.cameralibrary.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.caojing.cameralibrary.R
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import moe.codeest.enviews.ENPlayView

/**
 *
 * Created by Caojing
 * 2020/1/61415
 * 不为往事扰，余生自愿笑
 */
class JiJiaStandardGSYVideoPlayer(context: Context, attrs: AttributeSet?) :
    StandardGSYVideoPlayer(context, attrs) {
    constructor(context: Context) : this(context, null)

    override fun getLayoutId(): Int {
        return R.layout.jijia_play_video_layout
    }


    lateinit var startBottom: JiJiaPlayView

    override fun init(context: Context?) {
        super.init(context)
        startBottom = findViewById(R.id.startBottom)
        startBottom.setOnClickListener {
            clickStartIcon()
        }
    }

    override fun updateStartImage() {
        if (mStartButton is ENPlayView) {
            val enPlayView = mStartButton as ENPlayView
            enPlayView.setDuration(500)
            startBottom.setDuration(500)
            when (mCurrentState) {
                GSYVideoView.CURRENT_STATE_PLAYING ->{
                    enPlayView.play()
                    startBottom.play()
                }
                GSYVideoView.CURRENT_STATE_ERROR ->{
                    enPlayView.pause()
                    startBottom.pause()
                }
                else ->{
                    enPlayView.pause()
                    startBottom.pause()
                }
            }
        } else if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                GSYVideoView.CURRENT_STATE_PLAYING -> imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_pause_selector)
                GSYVideoView.CURRENT_STATE_ERROR -> imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_error_selector)
                else -> imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_play_selector)
            }
        }
    }

    override fun showVolumeDialog(deltaY: Float, volumePercent: Int) {
        super.showVolumeDialog(deltaY, volumePercent)
    }

    override fun dismissVolumeDialog() {
        super.dismissVolumeDialog()
    }
}