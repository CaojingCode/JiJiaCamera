package com.caojing.jijiacamerademo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.caojing.cameralibrary.activity.JiJiaCameraActivity
import com.caojing.cameralibrary.callback.LoctionCallBack
import com.caojing.cameralibrary.util.VideoAddressAction
import com.caojing.cameralibrary.util.selectLookVideo
import com.caojing.cameralibrary.util.showToast
import com.caojing.cameralibrary.util.startRecordVideo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.startRecordVideo()
//        this.selectLookVideo()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
                var videoBean = data?.getStringExtra("videoBean")
                videoBean?.let { showToast(it) }
                tvText.text = videoBean
            }
        }
    }
}
