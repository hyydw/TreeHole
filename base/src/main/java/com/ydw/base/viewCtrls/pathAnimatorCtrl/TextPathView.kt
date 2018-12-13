package com.ydw.base.viewCtrls.pathAnimatorCtrl

import android.content.Context

import android.view.ViewGroup
import android.text.TextPaint
import android.graphics.*
import android.support.annotation.Nullable
import android.util.AttributeSet
import android.view.View
import com.ydw.base.R


abstract class TextPathView : PathView {
    //val TAG = "yjkTextPathView"
    //用于获取文字的画笔
    protected lateinit var mTextPaint: TextPaint
    //文字装载路径;
    protected var mFontPath = Path()
    //文本宽高
    protected var mTextWidth = 0f
    protected var mTextHeight = 0f
    //要刻画的字符
    protected var mText: String? = null
    //要刻画的字符字体大小
    protected var mTextSize = 108

    //是否自动开始动画
    protected var mAutoStart = false
    //文字是否居中
    protected var mTextInCenter = false
    //文字是否一开始显示
    protected var mShowInStart = false
    //文字是否填充颜色
    protected var mFillColor = false
    //字体
    protected var mTypeface: Typeface? = null
    //测量Path具体范围
    private val mPathBounds = RectF()
    //Height是否处于wrap_content
    private var wrapWidth = false
    private var wrapHeight = false


    constructor(context: Context): super(context) {

    }

    constructor(context: Context, @Nullable attrs: AttributeSet): super(context, attrs){
        this.initAttr(context, attrs)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.initAttr(context, attrs)
    }

    override fun initAttr(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextPathView)
        mText = typedArray.getString(R.styleable.TextPathView_text)
        if (mText == null) {
            mText = "Test"
        }
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.TextPathView_textSize, mTextSize)
        mDuration = typedArray.getInteger(R.styleable.TextPathView_duration, mDuration)
        showPainter = typedArray.getBoolean(R.styleable.TextPathView_showPainter, showPainter)
        showPainterActually = typedArray.getBoolean(R.styleable.TextPathView_showPainterActually, showPainterActually)
        mPathStrokeWidth =
                typedArray.getDimensionPixelOffset(R.styleable.TextPathView_pathStrokeWidth, mPathStrokeWidth.toInt())
                    .toFloat()
        mTextStrokeColor = typedArray.getColor(R.styleable.TextPathView_pathStrokeColor, mTextStrokeColor)
        mPaintStrokeWidth =
                typedArray.getDimensionPixelOffset(R.styleable.TextPathView_paintStrokeWidth, mPaintStrokeWidth.toInt())
                    .toFloat()
        mPaintStrokeColor = typedArray.getColor(R.styleable.TextPathView_paintStrokeColor, mPaintStrokeColor)
        mAutoStart = typedArray.getBoolean(R.styleable.TextPathView_autoStart, mAutoStart)
        mTextInCenter = typedArray.getBoolean(R.styleable.TextPathView_textInCenter, mTextInCenter)
        mShowInStart = typedArray.getBoolean(R.styleable.TextPathView_showInStart, mShowInStart)
        mRepeatStyle = typedArray.getInt(R.styleable.TextPathView_repeat, 0)
        typedArray.recycle()
    }

    /**
     * 初始化画笔
     */
    override fun initPaint() {
        super.initPaint()
        mTextPaint = TextPaint()
        //        TextPaint textPaint = new TextPaint(mTextPaint);
        //        mTextPaint.setTypeface();

        mTextPaint.textSize = mTextSize.toFloat()

        if (mTextInCenter) {
            mDrawPaint!!.textAlign = Paint.Align.CENTER
        }
        if (mTypeface != null) {
            mTextPaint.typeface = mTypeface
        }

    }


    /**
     * 重写onMeasure方法使得WRAP_CONTENT生效
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        //        int hSpeSize = MeasureSpec.getSize(heightMeasureSpec);
        //        int hSpeMode = MeasureSpec.getMode(heightMeasureSpec);
        //        int wSpeSize = MeasureSpec.getSize(widthMeasureSpec);
        //        int wSpeMode = MeasureSpec.getMode(widthMeasureSpec);
        var width = View.MeasureSpec.getSize(widthMeasureSpec)
        var height = View.MeasureSpec.getSize(heightMeasureSpec)

        //        mTextWidth = TextUtil.getTextWidth(mTextPaint,mText);

        if (mTextWidth > width) {
            handleNewLines(width.toFloat())
            mTextWidth = width.toFloat()
        }

        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            if (mTextWidth <= width) {
                width = mTextWidth.toInt() + 1
            } else {
                handleNewLines(width.toFloat())
                mTextWidth = width.toFloat()
            }
            wrapWidth = true
        } else {
            wrapWidth = false
        }

        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = mTextHeight.toInt() + 1
            wrapHeight = true
        } else {
            wrapHeight = false
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mFontPath.computeBounds(mPathBounds, true)
    }

    override fun onDraw(canvas: Canvas) {

        if (mTextInCenter) {
            if (!wrapWidth) {
                canvas.translate((width - mPathBounds.width()) / 2, 0f)
            }
            if (!wrapHeight) {
                canvas.translate(0f, (height - mPathBounds.height()) / 2)
            }
        }
        //画笔效果绘制
        if (showPainterActually) {
            canvas.drawPath(mPaintPath, mPaint)
        }
        //文字路径绘制
        if (mAnimatorValue >= 1) {
            canvas.drawPath(mFontPath, mDrawPaint)
        } else {
            canvas.drawPath(mDst, mDrawPaint)
        }

    }

    //处理换行：拆分字符串，分别获取它们的path，再拼接
    protected fun handleNewLines(outerWidth: Float) {
        val widths = FloatArray(mText!!.length)
        mTextPaint.getTextWidths(mText, widths)

        var widthSum = 0f
        val metrics = mTextPaint.fontMetrics
        val ascent = -mTextPaint.fontMetrics.ascent
        val height = metrics.descent + ascent
        var start = 0
        var count = 0
        mFontPath.reset()
        for (i in widths.indices) {
            val width = widths[i]
            widthSum += width
            //            Log.d(TAG, "handleNewLines: width " + width + " i: " + i);
            if (widthSum > outerWidth) {
                val text = mText!!.substring(start, i)
                widthSum = width
                start = i
                val path = Path()
                mTextPaint.getTextPath(text, 0, text.length, 0f, ascent, path)
                mFontPath.addPath(path, 0f, height * count)
                //                Log.d(TAG, "handleNewLines text: " + text);
                count++
            }
        }
        if (start < widths.size) {
            val text = mText!!.substring(start, widths.size)
            //            Log.d(TAG, "handleNewLines text: " + text);
            val path = Path()
            mTextPaint.getTextPath(text, 0, text.length, 0f, ascent, path)
            mFontPath.addPath(path, 0f, height * count)
        }
        mTextHeight = height * ++count
    }


    //设置文字内容
    open fun setText(text: String) {
        mText = text
        try {
            initPath()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        clear()
        requestLayout()
    }

    //设置字体样式
    fun setTypeface(typeface: Typeface) {
        mTypeface = typeface
        initPaint()
    }

    //直接显示填充好颜色了的全部文字
    override fun showFillColorText() {
        mFillColor = true
        mDrawPaint!!.style = Paint.Style.FILL_AND_STROKE
        drawPath(1f)
    }

    /**
     * 检查当前进度是否需要填充颜色
     * @param progress 输入进度值
     */
    override fun checkFill(progress: Float) {
        if (progress != 1f && mFillColor) {
            mFillColor = false
            mDrawPaint!!.style = Paint.Style.STROKE
        }
    }

    override fun isProgressValid(progress: Float): Boolean {
        if (progress < 0 || progress > 1) {
            try {
                throw Exception("Progress is invalid!")
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

        }
        return true
    }

}