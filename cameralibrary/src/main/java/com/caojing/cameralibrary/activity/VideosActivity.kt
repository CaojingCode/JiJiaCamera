package com.caojing.cameralibrary.activity

import android.app.Activity
import android.content.Intent
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.*
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.adapter.ItemImageView
import com.caojing.cameralibrary.adapter.ItemTittleView
import com.caojing.cameralibrary.adapter.VideosAdapter
import com.caojing.cameralibrary.bean.VideoBean
import com.caojing.cameralibrary.util.*
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_videos.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*

class VideosActivity : AppCompatActivity(), BaseQuickAdapter.OnItemChildClickListener {

    var videoAdapter = VideosAdapter()

    var files = mutableListOf<VideoBean>()

    //是否是进来选择视频的
    var isSelect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos)
        isSelect = intent.getBooleanExtra("isSelect", false)
        if (isSelect) {
            tvBottom.text = "上传"
        } else {
            tvBottom.text = "删除"
        }
        rlVideos.layoutManager = GridLayoutManager(this, 4)
        rlVideos.addItemDecoration(DividerDecoration(ConvertUtils.dp2px(5f)))
        rlVideos.adapter = videoAdapter
        videoAdapter.setSpanSizeLookup { gridLayoutManager, position ->
            when (videoAdapter.data[position].viewType) {
                ItemTittleView -> 4
                else -> 1
            }
        }
        GlobalScope.launch(Dispatchers.Main) {
            //等待异步执行结果
            files = getVideoFiles()
            pbVideos.visibility = View.GONE
            videoAdapter.setNewData(files)
        }
        videoAdapter.onItemChildClickListener = this
        llBottom.setOnClickListener {
            //删除或者上传
            //获取选中的视频
            val videoSelectList = mutableListOf<VideoBean>()
            for (i in videoAdapter.data.indices) {
                if (videoAdapter.data[i].isSelect) {
                    videoSelectList.add(videoAdapter.data[i])
                }
            }
            if (videoSelectList.size <= 0) {
                showToast("请最少选中一个")
                return@setOnClickListener
            }
            if (isSelect) {
                //上传
                val intent = Intent()
                intent.putExtra("videoBean", GsonUtils.toJson(videoSelectList[0]))
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                //删除
                //删除选中的视频，真实位置，
                for (i in videoSelectList.indices) {
                    val videoBean = videoSelectList[i]
                    FileUtils.delete(videoBean.videoPath)

                }
                //获取两个集合的差集,更新适配器
                @Suppress("UNCHECKED_CAST")
                val newList: MutableList<VideoBean> =
                    CollectionUtils.subtract(
                        videoAdapter.data,
                        videoSelectList
                    ) as MutableList<VideoBean>
                videoAdapter.setNewData(newList)
            }
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view?.id) {
            R.id.ivVideo -> {
                //跳转到视频播放页面
            }
            R.id.ivSelect -> {
                //如果是选择视频上传，则只能单选上传，如果是从相机页面进入的则可多选删除
                val dataList = videoAdapter.data
                if (isSelect) {
                    for (i in dataList.indices) {
                        dataList[i].isSelect = i == position
                    }
                } else {
                    dataList[position].isSelect = !dataList[position].isSelect
                }
                videoAdapter.notifyDataSetChanged()
            }
        }
    }

}

