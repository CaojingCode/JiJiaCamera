package com.caojing.cameralibrary.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import com.blankj.utilcode.util.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.bean.VideoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by Caojing
 * 2019/12/221620
 * 不为往事扰，余生自愿笑
 */
fun Float.dp2px(): Float {
    val scale = Resources.getSystem().displayMetrics.density
    return this * scale + 0.5f
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun String.createOrExistsDir(): Boolean {
    var file = File(this)
    return if (file.exists()) file.isDirectory else file.mkdirs()
}

fun String.getNowString(): String {
    val simpleDateFormat = SimpleDateFormat(this, Locale.SIMPLIFIED_CHINESE)
    return simpleDateFormat.format(Date(System.currentTimeMillis()))
}

/**
 * 獲取視頻圖片
 */
fun getVideoImage(mPath: String): Bitmap {
    val media = MediaMetadataRetriever()
    media.setDataSource(mPath)
    //frameTime的单位为us微秒
    return media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
}

/**
 * 获取视频时长
 */
fun getVideoDuration(mPath: String): Int {
    val media = MediaMetadataRetriever()
    media.setDataSource(mPath)
    return Integer.parseInt(
        media.extractMetadata
            (MediaMetadataRetriever.METADATA_KEY_DURATION)
    )
}

/**
 * 获取所有的视频文件
 */
suspend fun getVideoFiles() =
    withContext(Dispatchers.IO) {
        //异步执行
        val videoBeans = mutableListOf<VideoBean>()
        val fileDir = String.format(
            Locale.getDefault(),
            "%s/JiJiaRecord/",
            Environment.getExternalStorageDirectory().absolutePath
        )
        val files = FileUtils.listFilesInDir(fileDir)
        for (i in files.indices) {
            val videoBean = VideoBean()
            videoBean.videoDuration = getVideoDuration(files[i].absolutePath)
            videoBean.videoPath = files[i].absolutePath
            videoBeans.add(videoBean)
        }
        videoBeans
    }

/**
 * 获取最新的一个视频
 */
suspend fun getLastVideo() = getVideoFiles()[0]

/**
 * 加载视频首帧图片
 */
 fun ImageView.loadVideoImage(videoPath: String) {
    Glide.with(com.blankj.utilcode.util.Utils.getApp()).setDefaultRequestOptions(
        RequestOptions()
            .frame(1)//单位微秒
            .centerCrop()
            .placeholder(R.color.white)
            .error(R.color.white)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    )
        .load(videoPath)
        .into(this)
}