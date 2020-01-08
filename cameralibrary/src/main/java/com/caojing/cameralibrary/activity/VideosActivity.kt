package com.caojing.cameralibrary.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.*
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.adapter.ItemTittleView
import com.caojing.cameralibrary.adapter.VideosAdapter
import com.caojing.cameralibrary.bean.VideoBean
import com.caojing.cameralibrary.util.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import kotlinx.android.synthetic.main.activity_videos.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VideosActivity : AppCompatActivity(), BaseQuickAdapter.OnItemChildClickListener {

    var videoAdapter = VideosAdapter()

    var files = mutableListOf<VideoBean>()

    //是否是进来选择视频的
    var isSelect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QMUIStatusBarHelper.translucent(this)
        setContentView(R.layout.activity_videos)
        isSelect = intent.getBooleanExtra("isSelect", false)
        if (isSelect) {
            //上传
            tvBottom.visibility=View.GONE
        } else {
            //删除
            btnUpdate.visibility=View.GONE
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
        btnUpdate.setOnClickListener {
            //上传
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
            val intent = Intent()
            intent.putExtra("videoBean", GsonUtils.toJson(videoSelectList[0]))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

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
            //删除
            AlertDialog.Builder(this)
                .setMessage("是否确认删除带看视频")
                .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->

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
                    newList.updateTxt()
                }).create().show()
        }

        ivBack.setOnClickListener { finish() }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view?.id) {
            R.id.ivVideo -> {
                //跳转到视频播放页面
                val intent = Intent(this, VideoPlayerActivity::class.java)
                intent.putExtra("isSelect",isSelect)
                intent.putExtra("videoBean", videoAdapter.data[position])
                startActivityForResult(intent, 2020)
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

//    override fun onResume() {
//        super.onResume()
//        GlobalScope.launch(Dispatchers.Main) {
//            //等待异步执行结果
//            files = getVideoFiles()
//            pbVideos.visibility = View.GONE
//            videoAdapter.setNewData(files)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2020) {
                if (isSelect){
                    //上传
                    val videoBean = data?.getSerializableExtra("videoBean") as VideoBean
                    val intent = Intent()
                    intent.putExtra("videoBean", GsonUtils.toJson(videoBean))
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else{
                    //删除
                    val videoBean = data?.getSerializableExtra("videoBean")
                    if (videoBean != null) {
                        val position = videoAdapter.data.indexOf(videoBean)
                        videoAdapter.remove(position)
                        videoAdapter.data.updateTxt()

                    }
                }
            }
        }
    }
}

