package com.caojing.cameralibrary.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.caojing.cameralibrary.R
import kotlinx.android.synthetic.main.jijia_fragment_dialog.view.*

/**
 * Created by Caojing on 2019/7/3.
 *  你不是一个人在战斗
 */
class JiJiaFragmentDialog : DialogFragment() {

    private val TAG = "JiJiaFragmentDialog"

    var cancelOutside: Boolean = false

    lateinit var priceCallBack: DialogCallBack

    var btnNum = 1  //几个按钮的对话框

    var message: String = ""


    fun setCancelOutSide(cancelOutside: Boolean): JiJiaFragmentDialog {
        this.cancelOutside = cancelOutside
        return this
    }

    companion object {
        fun create(): JiJiaFragmentDialog {
            return JiJiaFragmentDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(cancelOutside)

        val v = inflater.inflate(R.layout.jijia_fragment_dialog, container, false)

        if (btnNum == 1) {
            v.tvOneBtn?.visibility = View.VISIBLE
            v.llTwoBtn?.visibility = View.GONE
        } else {
            v.tvOneBtn?.visibility = View.GONE
            v.llTwoBtn?.visibility = View.VISIBLE
        }

        v.tvMessage.text = message
        v.tvOneBtn.setOnClickListener {
            //我知道了，一个按钮的对话框
            dismiss()
        }
        v.tvBtnCancel.setOnClickListener {
            //取消，两个按钮的
            dismiss()
        }
        v.tvBtnOK.setOnClickListener {
            //确定，两个按钮的
            dismiss()
            priceCallBack.btnOk()
        }
        return v
    }

    //设置单个按钮
    fun singleBtn(): JiJiaFragmentDialog {
        btnNum = 1
        return this
    }


    fun setMessage(text: String): JiJiaFragmentDialog {
        message = text
        return this
    }

    //设置两个按钮
    fun multipleBtn(): JiJiaFragmentDialog {
        btnNum = 2
        return this
    }

    fun show(fragmentManager: FragmentManager): JiJiaFragmentDialog {
        show(fragmentManager, TAG)
        return this
    }

    interface DialogCallBack {
        fun btnOk()
    }


    fun setDialogCallBack(priceCallBack: DialogCallBack): JiJiaFragmentDialog {
        this.priceCallBack = priceCallBack
        return this
    }
}