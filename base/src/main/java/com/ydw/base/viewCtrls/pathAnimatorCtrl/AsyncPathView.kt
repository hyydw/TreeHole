package com.ydw.base.viewCtrls.pathAnimatorCtrl

import android.content.Context
import android.graphics.Path
import android.support.annotation.Nullable
import android.util.AttributeSet

class AsyncPathView : PathView {
    //分段路径长度
    private var mLength = 0f

    //画笔特效
    private var mPainter: AsyncPathPainter? = null

    constructor(context: Context): super(context) {

        init()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet): super(context, attrs) {
        init()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init()
    }

    protected fun init() {
        initPaint()
        try {
            initPath()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //初始化文字路径
    @Throws(Exception::class)
    override fun initPath() {
        if (mPath == null) {
            throw Exception("PathView can't work without setting a path!")
        }
        mDst.reset()
        mPathMeasure.setPath(mPath, false)
    }


    /**
     * 绘画文字路径的方法
     * @param progress 绘画进度，0-1
     */
    override fun drawPath(progress: Float) {
        var mProgress = progress
        if (!isProgressValid(mProgress)) {
            if (mProgress > 1) {
                mProgress = 1f
            } else {
                return
            }
        }

        checkFill(mProgress)

        mAnimatorValue = mProgress

        //重置路径
        mPathMeasure.setPath(mPath, false)
        mDst.reset()
        mPaintPath.reset()

        //根据进度获取路径
        while (mPathMeasure.nextContour()) {
            mLength = mPathMeasure.length
            mStop = mLength * mAnimatorValue
            mPathMeasure.getSegment(0f, mStop, mDst, true)

            //绘画画笔效果
            if (showPainterActually) {
                mPathMeasure.getPosTan(mStop, mCurPos, null)
                drawPaintPath(mCurPos[0], mCurPos[1], mPaintPath)
            }
        }

        //绘画路径
        postInvalidate()
    }

    private fun drawPaintPath(x: Float, y: Float, paintPath: Path) {
        if (mPainter != null) {
            mPainter!!.onDrawPaintPath(x, y, paintPath)
        }
    }


    //设置画笔特效
    fun setPainter(painter: AsyncPathPainter) {
        this.mPainter = painter
    }
}