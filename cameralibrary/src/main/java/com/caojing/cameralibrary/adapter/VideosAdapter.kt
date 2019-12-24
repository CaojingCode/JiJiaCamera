package com.caojing.cameralibrary.adapter

import android.widget.ImageView
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.bean.VideoBean
import com.caojing.cameralibrary.util.loadVideoImage
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 *
 * Created by Caojing
 * 2019/12/231134
 * 不为往事扰，余生自愿笑
 */
class VideosAdapter : BaseQuickAdapter<VideoBean, BaseViewHolder>(R.layout.item_videos) {
    override fun convert(helper: BaseViewHolder, item: VideoBean?) {

        val imageView = helper.getView(R.id.ivVideo) as ImageView
        item?.videoPath?.let { imageView.loadVideoImage(it) }
    }
}