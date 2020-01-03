package com.caojing.cameralibrary.bean

import java.io.Serializable

/**
 *
 * Created by Caojing
 * 2019/12/231523
 * 不为往事扰，余生自愿笑
 */
data class VideoBean(
    var videoPath: String = "",  //视频路径
    var videoDuration: Long = 0,  //视频时长 单位 毫秒
    var viewType: Int = 0,       //view类型 0视频  1标题
    var videoTimestamp: Long = 0,  //拍摄时间戳
    var videoAddress: String = "",//视频拍摄位置

    var isSelect: Boolean = false,//是否选中

    var deviceType: String = ""  //设备型号

):Serializable