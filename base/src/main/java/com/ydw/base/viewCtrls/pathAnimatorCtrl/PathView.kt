package com.ydw.base.viewCtrls.pathAnimatorCtrl

import android.content.Context
import android.support.annotation.Nullable
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.support.annotation.IntDef
import com.ydw.base.R
import android.view.animation.LinearInterpolator

import android.view.ViewGroup
import android.os.Build
import android.support.annotation.RequiresApi
import android.animation.Animator
import android.graphics.*


abstract class PathView : View{
    val TAG = "TextPathView"
    companion object {
        const val NONE = 0
        const val RESTART = 1
        const val REVERSE = 2
    }


    @IntDef(
        NONE,
        RESTART,
        REVERSE
    )
    @Retention(AnnotationRetention.SOURCE)//RetentionPolicy.SOURCE
    annotation class Repeat


    @Repeat
    protected var mRepeatStyle = NONE

    //路径的画笔
    protected var mDrawPaint: Paint? = null
    //画笔特效的画笔
    protected var mPaint: Paint? = null
    //文字装载路径、文字绘画路径、画笔特效路径
    protected var mDst = Path()
    protected var mPaintPath = Path()
    //属性动画
    protected var mAnimator: ValueAnimator? = null
    //动画进度值
    protected var mAnimatorValue = 0f

    //绘画部分长度
    protected var mStop = 0f
    //是否展示画笔特效:
    //showPainter代表动画绘画时是否展示
    //showPainterActually代表所有时候是否展示，由于动画绘画完毕应该将画笔特效消失，所以每次执行完动画都会自动设置为false
    public var showPainter = true
    public var showPainterActually = false
    //当前绘画位置
    protected var mCurPos = FloatArray(2)
    //当前点tan值,暂时无用
    //    protected float[] mCurTan = new float[2];
    //路径宽高
    protected var mPathWidth = 0f
    protected var mPathHeight = 0f

    protected var mDuration = 6000

    protected var mPathMeasure = PathMeasure()

    //要绘画的路径
    protected var mPath: Path? = null

    //文字路径的粗细，画笔粗细
    protected var mPathStrokeWidth = 5.0f
    protected var mPaintStrokeWidth = 3.0f
    //文字路径的颜色，画笔路径颜色
    protected var mTextStrokeColor = Color.BLACK
    protected var mPaintStrokeColor = Color.BLACK

    //文字填充颜色,后面会初始化默认为mTextStrokeColor
    //    protected int mFillColor = Color.BLACK;
    //文字是否填充颜色
    protected var mShouldFill = false

    //动画监听
    protected var mAnimatorListener: Animator.AnimatorListener? = null

    protected var nullPath = true

    constructor(context: Context) : super(context) {

    }
    constructor(context: Context,@Nullable attrs:AttributeSet) : super(context,attrs) {
        this.initAttr(context, attrs)
    }
    constructor(context: Context,@Nullable attrs:AttributeSet,defStyleAttr:Int) : super(context,attrs) {
        this.initAttr(context, attrs)
    }



    protected open fun initAttr(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PathView)
        mDuration = typedArray.getInteger(R.styleable.PathView_duration, mDuration)
        showPainter = typedArray.getBoolean(R.styleable.PathView_showPainter, showPainter)
        showPainterActually = typedArray.getBoolean(R.styleable.PathView_showPainterActually, showPainterActually)
        mPathStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.PathView_pathStrokeWidth, mPathStrokeWidth.toInt()).toFloat()
        mTextStrokeColor = typedArray.getColor(R.styleable.PathView_pathStrokeColor, mTextStrokeColor)
        mPaintStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.PathView_paintStrokeWidth, mPaintStrokeWidth.toInt()).toFloat()
        mPaintStrokeColor = typedArray.getColor(R.styleable.PathView_paintStrokeColor, mPaintStrokeColor)
        mRepeatStyle = typedArray.getInt(R.styleable.PathView_repeat, mRepeatStyle)
        typedArray.recycle()
    }

    /**
     * 初始化画笔
     */
    protected open fun initPaint() {

        mDrawPaint = Paint()
        mDrawPaint!!.isAntiAlias = true
        mDrawPaint!!.color = mTextStrokeColor
        mDrawPaint!!.strokeWidth = mPathStrokeWidth
        mDrawPaint!!.style = Paint.Style.STROKE

        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.color = mPaintStrokeColor
        mPaint!!.strokeWidth = mPaintStrokeWidth
        mPaint!!.style = Paint.Style.STROKE
    }

    protected fun initAnimator(start: Float, end: Float, animationStyle: Int, repeatCount: Int) {
        mAnimator = ValueAnimator.ofFloat(start, end)

        mAnimator!!.addUpdateListener { valueAnimator ->
            mAnimatorValue = valueAnimator.animatedValue as Float
            drawPath(mAnimatorValue)
        }
        if (mAnimatorListener == null) {
            mAnimatorListener = PathAnimatorDefaultListener()
            (mAnimatorListener as PathAnimatorDefaultListener).setTarget(this)
        }
        mAnimator!!.removeAllListeners()
        mAnimator!!.addListener(mAnimatorListener)

        mAnimator!!.duration = mDuration.toLong()
        mAnimator!!.interpolator = LinearInterpolator()
        if (animationStyle == RESTART) {
            mAnimator!!.repeatMode = ValueAnimator.RESTART
            mAnimator!!.repeatCount = repeatCount
        } else if (animationStyle == REVERSE) {
            mAnimator!!.repeatMode = ValueAnimator.REVERSE
            mAnimator!!.repeatCount = repeatCount
        }
    }

    /**
     * 开始绘制文字路径动画
     *
     * @param start 路径比例，范围0-1
     * @param end   路径比例，范围0-1
     */
    fun startAnimation(start: Float, end: Float) {
        startAnimation(start, end, mRepeatStyle, ValueAnimator.INFINITE)
    }

    fun startAnimation(start: Float, end: Float, animationStyle: Int, repeatCount: Int) {
        if (!isProgressValid(start) || !isProgressValid(end)) {
            return
        }
        if (mAnimator != null) {
            mAnimator!!.cancel()
        }
        initAnimator(start, end, animationStyle, repeatCount)
        //        try {
        //            initPath();
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        showPainterActually = showPainter
        mAnimator?.start()
    }

    /**
     * Stop animation
     */
    fun stopAnimation() {
        showPainterActually = false
        clear()
        if (mAnimator != null) {
            mAnimator!!.cancel()
        }
    }

    /**
     * Pause animation
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun pauseAnimation() {
        if (mAnimator != null) {
            mAnimator!!.pause()
        }
    }

    /**
     * Resume animation
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun resumeAnimation() {
        if (mAnimator != null) {
            mAnimator!!.resume()
        }
    }

    /**
     * 绘画文字路径的方法
     *
     * @param progress 绘画进度，0-1
     */
    abstract fun drawPath(progress: Float)

    @Throws(Exception::class)
    protected abstract fun initPath()

    /**
     * 重写onMeasure方法使得WRAP_CONTENT生效，未成功
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val hSpeSize = View.MeasureSpec.getSize(heightMeasureSpec)
        //        int hSpeMode = MeasureSpec.getMode(heightMeasureSpec);
        val wSpeSize = View.MeasureSpec.getSize(widthMeasureSpec)
        //        int wSpeMode = MeasureSpec.getMode(widthMeasureSpec);
        var width = wSpeSize
        var height = hSpeSize

        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT && !nullPath) {
            width = mPathWidth.toInt()
        }
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT && !nullPath) {
            height = mPathHeight.toInt()
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //画笔效果绘制
        if (showPainterActually) {
            canvas.drawPath(mPaintPath, mPaint!!)
        }
        //文字路径绘制
        canvas.drawPath(mDst, mDrawPaint!!)

    }

    //获取绘画文字的画笔
    fun getDrawPaint(): Paint? {
        return mDrawPaint
    }

    //获取绘画画笔特效的画笔
    fun getPaint(): Paint? {
        return mPaint
    }

    /**
     * 设置路径，必须先设置好路径在startAnimation()，不然会报错！
     */
    fun setPath(path: Path) {
        this.mPath = path
        try {
            initPath()
            //ToDo 这里的设置只能获取Path非空白部分的宽高，不能获取整个Path的宽高，后面再寻找方法
            val rectF = RectF()
            mPath!!.computeBounds(rectF, false)
            mPathWidth = rectF.width()
            mPathHeight = rectF.height()
            nullPath = false
        } catch (e: Exception) {
            nullPath = true
            e.printStackTrace()
        }

    }

    //清除画面
    fun clear() {
        mAnimatorValue = 0f
        if (mDst != null) {
            mDst.reset()
        }
        if (mPaintPath != null) {
            mPaintPath.reset()
        }
        postInvalidate()
    }



    //设置自定义动画监听
//    fun setAnimatorListener(animatorListener: PathAnimatorListener) {
//        mAnimatorListener = animatorListener
//        mAnimatorListener!!.setTarget(this)
//        if (mAnimator != null) {
//            mAnimator!!.removeAllListeners()
//            mAnimator!!.addListener(mAnimatorListener)
//        }
//    }
    fun setAnimatorListener(animatorListener: Animator.AnimatorListener) {
        mAnimatorListener = animatorListener
        (mAnimatorListener as PathAnimatorDefaultListener).setTarget(this)
        if (mAnimator != null) {
            mAnimator!!.removeAllListeners()
            mAnimator!!.addListener(mAnimatorListener)
        }
    }

    fun setPathAnimatorDefaultListener(listener: PathAnimatorDefaultListener.() -> Unit){
        val ca = PathAnimatorDefaultListener()
        ca.listener()
        setAnimatorListener(ca)
    }


    //直接显示填充好颜色了的全部文字
    open fun showFillColorText() {
        mShouldFill = true
        mDrawPaint?.setStyle(Paint.Style.FILL_AND_STROKE)
        drawPath(1f)
    }

    //设置动画持续时间
    fun setDuration(duration: Int) {
        this.mDuration = duration
    }

    //设置重复方式
    fun setRepeatStyle(repeatStyle: Int) {
        this.mRepeatStyle = repeatStyle
    }

    protected open fun checkFill(progress: Float) {
        if (progress != 1f && mShouldFill) {
            mShouldFill = false
            mDrawPaint?.setStyle(Paint.Style.STROKE)
        }
    }

    protected open fun isProgressValid(progress: Float): Boolean {
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

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }

}