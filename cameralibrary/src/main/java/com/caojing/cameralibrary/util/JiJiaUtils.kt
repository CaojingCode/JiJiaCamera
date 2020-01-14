package com.caojing.cameralibrary.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.activity.JiJiaCameraActivity
import com.caojing.cameralibrary.activity.VideoPlayerActivity
import com.caojing.cameralibrary.activity.VideosActivity
import com.caojing.cameralibrary.adapter.ItemTittleView
import com.caojing.cameralibrary.bean.VideoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by Caojing
 * 2019/12/221620
 * 不为往事扰，余生自愿笑
 */
const val VideoAddress = "VideoAddress"
const val VideoAddressAction = "VideoAddressAction"
const val SelectPathKey="selectPath"

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
fun getVideoDuration(mPath: String): Long {
    val media = MediaMetadataRetriever()
    media.setDataSource(mPath)
    return media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
}

/**
 * 获取所有的视频文件
 */
suspend fun getVideoFiles() =
    withContext(Dispatchers.IO) {
        //异步执行
        val videoBeans = getVideoInfoList()
        videoBeans.sort()
//        val fileDir = String.format(
//            Locale.getDefault(),
//            "%s/JiJiaRecord/",
//            Environment.getExternalStorageDirectory().absolutePath
//        )
//        val files = FileUtils.listFilesInDir(fileDir)
//        for (i in files.indices) {
//            val videoBean = VideoBean()
//            videoBean.videoDuration = getVideoDuration(files[i].absolutePath)
//            videoBean.videoPath = files[i].absolutePath
//            var mills = getExif(ExifInterface.TAG_DATETIME, videoBean.videoPath)
//            videoBean.videoTimestamp = mills
//            val videoAddress = getExif(ExifInterface.TAG_SUBJECT_LOCATION, videoBean.videoPath)
//            videoBean.videoAddress = videoAddress
//            videoBeans.add(videoBean)
//        }

//        var groupTime = ""
//        for (i in videoBeans.indices) {
//            val newGroupTime = TimeUtils.millis2String(videoBeans[i].videoTimestamp, "yyyyMMdd")
//            if (groupTime != newGroupTime) {
//                groupTime = newGroupTime
//                videoBeans.add(
//                    i,
//                    VideoBean(
//                        viewType = ItemTittleView,
//                        videoTimestamp = videoBeans[i].videoTimestamp
//                    )
//                )
//            }
//        }
        val size = videoBeans.size -1
        for (i in size downTo 0) {
            if(i == 0){
                videoBeans.add(
                    i,
                    VideoBean(
                        viewType = ItemTittleView,
                        videoTimestamp = videoBeans[i].videoTimestamp
                    )
                )
            } else {
                val groupTime = TimeUtils.millis2String(videoBeans[i - 1].videoTimestamp, "yyyyMMdd")
                val newGroupTime = TimeUtils.millis2String(videoBeans[i].videoTimestamp, "yyyyMMdd")
                if (groupTime != newGroupTime) {
                    videoBeans.add(
                        i,
                        VideoBean(
                            viewType = ItemTittleView,
                            videoTimestamp = videoBeans[i].videoTimestamp
                        )
                    )
                }
            }
        }
        videoBeans
    }


/**
 * 获取exif信息
 */
private fun getExif(tag: String, filePath: String): String {
    var exif: ExifInterface? = null
    try {
        exif = ExifInterface(filePath)     //根据图片的路径获取图片的Exif
    } catch (ex: IOException) {
        Log.e("Mine", "cannot read exif", ex)
    }
    if (exif == null)
        return ""
    var exifStr = exif.getAttribute(tag)
    if (exifStr == null)
        exifStr = ""
    return exifStr
}

/**
 * 加载视频首帧图片
 */
fun ImageView.loadVideoImage(videoPath: String) {
    Glide.with(Utils.getApp()).setDefaultRequestOptions(
        RequestOptions()
            .frame(1)//单位微秒
            .centerCrop()
            .placeholder(R.drawable.defalt_img)
            .error(R.drawable.defalt_img)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    )
        .load(videoPath)
        .into(this)
}

//保存视频文件信息的txt文件
fun getTempStringPath(): String {
    val fileDir = String.format(
        Locale.getDefault(),
        "%s/JiJiaRecord/",
        Environment.getExternalStorageDirectory().absolutePath
    )
    if (!fileDir.createOrExistsDir()) {
        Log.e("文件夹创建失败：%s", fileDir)
    }
    val fileName = "videosPath"
    return String.format(Locale.getDefault(), "%s%s.txt", fileDir, fileName)
}


/**
 * 创建文件路径
 */
fun Context.getTempFilePath(): String {


    val fileDir = String.format(
        Locale.getDefault(),
        "%s/JiJiaRecord/",
        Environment.getExternalStorageDirectory().absolutePath
    )
    if (!fileDir.createOrExistsDir()) {
        Log.e("文件夹创建失败：%s", fileDir)
    }
    val fileName = String.format(
        Locale.getDefault(), "record_%s", "yyyyMMddHHmmss".getNowString()
    )
    return String.format(Locale.getDefault(), "%s%s.mp4", fileDir, fileName)
}

/**
 *  获取视频文件信息，从信息文件中取到所有视频的信息json字符串,将字符串转换成集合
 */
fun getVideoInfoList(): MutableList<VideoBean> {
    val videoInfoPath = getTempStringPath()

    val videoInfo = FileIOUtils.readFile2String(videoInfoPath)

    var videos = GsonUtils.fromJson(videoInfo, Array<VideoBean>::class.java)
    if (videos == null)
        videos = emptyArray()
    //过滤掉不存在的文件
    val videoList = mutableListOf<VideoBean>()
    for (i in videos.indices) {
        if (FileUtils.isFileExists(FileUtils.getFileByPath(videos[i].videoPath))) {
            videoList.add(videos[i])
        }
    }
//    return videos.toMutableList()
    return videoList
}

fun getVideoList() {
    val fileDir = String.format(
        Locale.getDefault(),
        "%s/JiJiaRecord/",
        Environment.getExternalStorageDirectory().absolutePath
    )
    val files = FileUtils.listFilesInDir(fileDir)

}

/**
 * 将毫秒转换成友好型时间 00:03
 */
fun Long.getTimeMString(): String {
    var second = this / 1000
    var minute = (second / 60).toInt()
    second -= minute * 60
    var secondStr: String
    var minuteStr: String
    if (second < 10) {
        secondStr = "0$second"
    } else {
        secondStr = second.toString()
    }
    if (minute < 10) {
        minuteStr = "0$minute"
    } else {
        minuteStr = minute.toString()
    }
    return "$minuteStr:$secondStr"
}

/**
 * 打开相机
 */
@ExperimentalCoroutinesApi
fun Activity.startRecordVideo() {
    val intent = Intent(this, JiJiaCameraActivity::class.java)
    this.startActivity(intent)
}

/**
 * 跳转到选择视频页面
 */
fun Activity.selectLookVideo(selectPath :String="") {
    val intent = Intent(this, VideosActivity::class.java)
    intent.putExtra("isSelect", true)
    intent.putExtra(SelectPathKey,selectPath)
    this.startActivityForResult(intent, 1000)
}

/**
 * 跳转到视频播放页面
 */
fun Activity.startVideoPlayer(videoBean: VideoBean) {
    val intent = Intent(this, VideoPlayerActivity::class.java)
    intent.putExtra("videoBean", videoBean)
    this.startActivity(intent)
}

/**
 * 根据最新的视频文件更新txt文件信息
 */
fun MutableList<VideoBean>.updateTxt(){
    val arrayList= mutableListOf<VideoBean>()
    for (i in this.indices){
        val path= this[i].videoPath
        if (path.isNotEmpty()){
            arrayList.add(this[i])
        }
    }
    //将所有的视频信息集合转换成json字符串
    val videoJson = GsonUtils.toJson(arrayList)
    //将json 字符串写到文件中并保存
    FileIOUtils.writeFileFromString(getTempStringPath(), videoJson)
}

private const val  MIN_CLICK_DELAY_TIME = 1000
private  var  lastClickTime:Long=0

fun isFastClick() :Boolean{
    var flag = false
    val curClickTime = System.currentTimeMillis()
    if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
        flag = true
    }
    lastClickTime = curClickTime
    return flag
}
