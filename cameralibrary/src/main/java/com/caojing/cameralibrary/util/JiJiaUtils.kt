package com.caojing.cameralibrary.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.widget.Toast
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

fun Context.showToast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}

fun String.createOrExistsDir():Boolean{
    var file=File(this)
    return if (file.exists()) file.isDirectory else file.mkdirs()
}

fun String.getNowString(): String {
    val simpleDateFormat = SimpleDateFormat(this,Locale.SIMPLIFIED_CHINESE)
    return simpleDateFormat.format(Date(System.currentTimeMillis()))
}