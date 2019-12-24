package com.caojing.cameralibrary.activity

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.adapter.VideosAdapter
import com.caojing.cameralibrary.bean.VideoBean
import com.caojing.cameralibrary.util.*
import kotlinx.android.synthetic.main.activity_videos.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class VideosActivity : AppCompatActivity() {

    var adapter = VideosAdapter()

    var files = mutableListOf<VideoBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos)
        rlVideos.layoutManager = GridLayoutManager(this, 4)
        rlVideos.addItemDecoration(DividerDecoration(ConvertUtils.dp2px(5f)))
        rlVideos.adapter = adapter
        GlobalScope.launch(Dispatchers.Main) {
            //等待异步执行结果
            files = getVideoFiles()
            pbVideos.visibility= View.GONE
            showToast("测试${files.size}")
            adapter.setNewData(files)
        }
    }

}

