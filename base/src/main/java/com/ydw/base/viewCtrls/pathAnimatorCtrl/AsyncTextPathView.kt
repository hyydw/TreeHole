package com.ydw.base.viewCtrls.pathAnimatorCtrl

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources.getSystem
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.Typeface.createFromAsset
import android.support.annotation.Nullable
import android.text.Layout
import android.util.AttributeSet


class AsyncTextPathView : TextPathView {
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
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        val tf = createFromAsset(context.assets,"书体坊兰亭体I.ttf")
        setTypeface(tf)
        initPaint()
        initPath()
        if (mAutoStart) {
            startAnimation(0f, 1f)
        }
        if (mShowInStart) {
            drawPath(1f)
        }

    }

    //初始化文字路径
    protected override fun initPath() {
        mDst.reset()
        mFontPath.reset()

        //获取宽高
        mTextWidth = Layout.getDesiredWidth(mText, mTextPaint)
        val metrics = mTextPaint.getFontMetrics()
        mTextHeight = metrics.bottom - metrics.top

        mTextPaint.getTextPath(mText, 0, mText!!.length, 0f, -metrics.ascent, mFontPath)
        mPathMeasure.setPath(mFontPath, false)
        mLength = mPathMeasure.getLength()

    }


    /**
     * 绘画文字路径的方法
     * @param progress 绘画进度，0-1
     */
    override fun drawPath(progress: Float) {
        var progress = progress
        if (!isProgressValid(progress)) {
            if (progress > 1) {
                progress = 1f
            } else {
                return
            }
        }

        checkFill(progress)

        mAnimatorValue = progress

        //重置路径
        mPathMeasure.setPath(mFontPath, true)
        mDst.reset()
        mPaintPath.reset()

        //根据进度获取路径
        while (mPathMeasure.nextContour()) {
            mLength = mPathMeasure.getLength()
            //            Log.d(TAG, "drawPath: length:" + mLength);
            mStop = mLength * mAnimatorValue
            //            Log.d(TAG, "drawPath: stop:" + mStop);
            //            Log.d(TAG, "drawPath: close? " + mPathMeasure.isClosed());
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

    //设置文字内容
    override fun setText(text: String) {
        mText = text
        initPath()
        clear()
        requestLayout()
    }

    //设置画笔特效
    fun setPathPainter(listener: AsyncPathPainter) {
        this.mPainter = listener
    }
}