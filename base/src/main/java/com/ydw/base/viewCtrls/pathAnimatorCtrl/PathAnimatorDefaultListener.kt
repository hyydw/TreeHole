package com.ydw.base.viewCtrls.pathAnimatorCtrl

import android.animation.Animator

class PathAnimatorDefaultListener :Animator.AnimatorListener{
    private var mPathView: PathView? = null
    protected var isCancel:Boolean = false

    fun setTarget(pathView: PathView) {
        this.mPathView = pathView
    }

    private var _OnAnimationRepeat:((animation: Animator) -> Unit)? = null
    fun animationRepeat(RepeatListener:(animation: Animator) -> Unit){
        _OnAnimationRepeat = RepeatListener
    }
    override fun onAnimationRepeat(animation: Animator) {
        _OnAnimationRepeat?.invoke(animation)
    }

    private var _OnAnimationStart:((animation: Animator) -> Unit)? = null
    fun animationStart(StartListener:(animation: Animator) -> Unit){
        _OnAnimationStart = StartListener
    }
    override fun onAnimationStart(animation: Animator) {
        _OnAnimationStart?.invoke(animation)
        isCancel = false
    }

    private var _OnAnimationEnd:((animation: Animator) -> Unit)? = null
    fun animationEnd(EndListener:(animation: Animator) -> Unit){
        _OnAnimationEnd = EndListener
    }
    override fun onAnimationEnd(animation: Animator) {
        _OnAnimationEnd?.invoke(animation)
        mPathView!!.showPainterActually = false
    }

    private var _OnAnimationCancel:((animation: Animator) -> Unit)? = null
    fun animationCancel(CancelListener:(animation: Animator) -> Unit){
        _OnAnimationCancel = CancelListener
    }
    override fun onAnimationCancel(animation: Animator) {
        _OnAnimationCancel?.invoke(animation)
        isCancel = true
    }
}