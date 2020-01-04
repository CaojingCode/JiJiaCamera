package com.caojing.cameralibrary.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.caojing.cameralibrary.R
import com.caojing.cameralibrary.util.dp2px


interface RecordButtonCallBack {
    fun startRecord()
    fun recordFinsh()
    fun updateTime(time:Int)
}

/**
 *
 * Created by Caojing
 * 2019/12/181028
 * 不为往事扰，余生自愿笑
 */
class CircleProgressBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    // 录制时的环形进度条
    private lateinit var mRecordPaint: Paint
    // 录制时点击的圆形按钮
    private lateinit var mBgPaint: Paint
    //圆形按钮的灰色背景
    private lateinit var mBgBgPaint: Paint
    // 画笔宽度
    private var mStrokeWidth: Float = 0f
    // 圆形按钮半径
    private var mRadius: Float = 0f
    //控件宽度
    private var mWidth: Int = 0
    //控件高度
    private var mHeight: Int = 0
    // 圆的外接圆
    private lateinit var mRectF: RectF
    //progress max value 60S
    private var mMaxValue = 610
    //per progress value
    private var mProgressValue: Int = 0
    //是否开始record
    private var mIsStartRecord = false
    //Arc left、top value
    private var mArcValue: Float = 0f
    //录制 time
    private var mRecordTime: Long = 0

    var callBack: RecordButtonCallBack? = null

    constructor(context: Context) : this(context, null)

    init {
        initParams(context)
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            ++mProgressValue
            callBack?.updateTime(mProgressValue/10)
            postInvalidate()
            //当没有达到最大值时一直绘制
            if (mProgressValue <= mMaxValue) {
                this.sendEmptyMessageDelayed(0, 100)
            }
        }
    }

    //初始化画笔操作
    private fun initParams(context: Context) {

        mStrokeWidth = 3f.dp2px()
        mArcValue = mStrokeWidth

        mBgPaint = Paint()
        mBgPaint.isAntiAlias = true
        mBgPaint.color = context.resources.getColor(R.color.white)
        mBgPaint.strokeWidth = mStrokeWidth
        mBgPaint.style = Paint.Style.FILL

        mBgBgPaint=Paint()
        mBgBgPaint.isAntiAlias = true
        mBgBgPaint.color = context.resources.getColor(R.color.grayColor)
        mBgBgPaint.strokeWidth = mStrokeWidth
        mBgBgPaint.style = Paint.Style.FILL

        mRecordPaint = Paint()
        mRecordPaint.isAntiAlias = true
        mRecordPaint.color = context.resources.getColor(R.color.mainColor)
        mRecordPaint.strokeWidth = mStrokeWidth
        mRecordPaint.style = Paint.Style.STROKE

        mRadius = 30f.dp2px()
        mRectF = RectF()

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mWidth = width
        mHeight = height
        if (mWidth != mHeight) {
            //比较宽高，获取较小值
            val min = mWidth.coerceAtMost(mHeight)
            mWidth = min
            mHeight = min
        }

        if (mIsStartRecord) {
            canvas?.drawCircle(
                (mWidth / 2).toFloat(),
                (mHeight / 2).toFloat(), (mRadius * 1.6).toFloat(), mBgBgPaint
            )

            canvas?.drawCircle(
                (mWidth / 2).toFloat(),
                (mHeight / 2).toFloat(), (mRadius * 0.5).toFloat(), mBgPaint
            )



            if (mProgressValue <= mMaxValue) {
                //left--->距Y轴的距离
                //top--->距X轴的距离
                //right--->距Y轴的距离
                //bottom--->距X轴的距离
                mRectF.left = mArcValue
                mRectF.top = mArcValue
                mRectF.right = mWidth - mArcValue
                mRectF.bottom = mHeight - mArcValue

                canvas?.drawArc(
                    mRectF,
                    -90f,
                    mProgressValue.toFloat() / mMaxValue * 360,
                    false,
                    mRecordPaint
                )

                if (mProgressValue == mMaxValue) {
                    mProgressValue = 0
                    mHandler.removeMessages(0)
                    mIsStartRecord = false
                    //这里可以回调出去表示已到录制时间最大值
                    //code.....
                    callBack?.recordFinsh()
                }
            }
        } else {
            canvas?.drawCircle(
                (mWidth / 2).toFloat(),
                (mHeight / 2).toFloat(), (mRadius * 1.2).toFloat(), mBgBgPaint
            )
            canvas?.drawCircle((mWidth / 2).toFloat(), (mHeight / 2).toFloat(), (mRadius * 0.8).toFloat(), mBgPaint)

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsStartRecord = true
                mRecordTime = System.currentTimeMillis()
                mHandler.sendEmptyMessage(0)
                //这里可以回调出去表示已经开始录制了
                callBack?.startRecord()
            }
            MotionEvent.ACTION_UP -> {
                if (mRecordTime > 0) {
                    //录制的时间（单位：秒）
                    val actualRecordTime =
                        ((System.currentTimeMillis() - mRecordTime) / 1000).toInt()
                    //这里回调出去表示已经取消录制了
                }

                callBack?.recordFinsh()
                mHandler.removeMessages(0)
                mIsStartRecord = false
                mRecordTime = 0
                mProgressValue = 0
                postInvalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                //这里可以回调出去表示已经取消录制了
                callBack?.recordFinsh()
                mHandler.removeMessages(0)
                mIsStartRecord = false
                mRecordTime = 0
                mProgressValue = 0
                postInvalidate()
            }
        }

        return true
    }


}