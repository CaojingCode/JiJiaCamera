package com.caojing.cameralibrary.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.bean.VideoBean
import com.caojing.cameralibrary.util.getTimeMString
import com.caojing.cameralibrary.util.loadVideoImage
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.util.MultiTypeDelegate

/**
 * 视频列表适配器
 * Created by Caojing
 * 2019/12/23
 * 不为往事扰，余生自愿笑
 */
const val ItemTittleView: Int = 1  //标题
const val ItemImageView: Int = 0   //图片

class VideosAdapter : BaseQuickAdapter<VideoBean, BaseViewHolder>(null) {

    init {
        multiTypeDelegate = object : MultiTypeDelegate<VideoBean>() {
            override fun getItemType(t: VideoBean): Int {
                return t.viewType
            }

        }
        multiTypeDelegate.registerItemType(ItemTittleView, R.layout.item_videos_tittle)
            .registerItemType(ItemImageView, R.layout.item_videos)
    }

    override fun convert(helper: BaseViewHolder, item: VideoBean?) {
        if (item == null)
            return
        when (helper.itemViewType) {
            ItemTittleView -> {
                //item?.videoData 保存的时间戳数据
                helper.setText(R.id.tvVideoName,
                    item.videoTimestamp.let { TimeUtils.millis2String(it, "yyyyMMdd") })
            }
            ItemImageView -> {
                val imageView = helper.getView(R.id.ivVideo) as ImageView
                val ivSelect = helper.getView(R.id.ivSelect) as ImageView
                item.videoPath.let { imageView.loadVideoImage(it) }
                LogUtils.d("位置：" + item.videoAddress)
                helper.setText(R.id.timeText, item.videoDuration.getTimeMString())
                if (item.isSelect) {
                    ivSelect.setImageResource(R.drawable.pic_checked)
                } else {
                    ivSelect.setImageResource(R.drawable.pic_uncheck)
                }
                helper.addOnClickListener(R.id.ivSelect)
                helper.addOnClickListener(R.id.ivVideo)
            }
        }
    }
}