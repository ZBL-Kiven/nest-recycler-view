package com.zj.viewtest.partition.base

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.zj.viewtest.R
import com.zj.viewtest.partition.services.beans.ComponentInfo
import kotlin.math.roundToInt

abstract class BaseComponent<DATA : Any?> constructor(context: Context) : ConstraintLayout(context), Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private enum class State(val level: Int) {
        ON_DESTROYED(0), ON_ATTACHED(1), ON_INVISIBLE(2), ON_VISIBLE(3)
    }

    var pageTitle: String? = ""

    abstract val layoutId: Int

    abstract val type: Int

    var cInfo: ComponentInfo? = null

    private var state: State = State.ON_DESTROYED

    abstract fun initView()

    abstract fun initData()

    abstract fun showSkeleton()

    protected abstract fun onSetData(data: DATA?, pl: Any?)

    fun setComponentData(data: DATA?, pl: Any? = null) {
        isLoadedData = true
        onSetData(data, pl)
    }

    open fun hideEndingLine(isHide: Boolean) {}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (state.level < State.ON_ATTACHED.level) {
            state = State.ON_ATTACHED
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (state.level >= State.ON_ATTACHED.level) {
            if (visibility == View.VISIBLE && state != State.ON_VISIBLE) {
                onResumed()
            }
            if (visibility == View.GONE && state != State.ON_INVISIBLE) {
                onPaused()
            }
        }
    }

    @CallSuper
    open fun onResumed() {
        if (!isLoadedData) {
            startWipesAnim()
        }
    }

    @CallSuper
    open fun onPaused() {
        cancelAnim()
    }

    @CallSuper
    open fun onDestroy(soft: Boolean = false) {
        isLoadedData = false
        cancelAnim()
    }

    init {
        onInit(context)
        initAnimPaint()
    }

    private fun onInit(context: Context) {
        LayoutInflater.from(context).inflate(layoutId, this, true)
        initView()
    }

    fun setData(cInfo: ComponentInfo?): BaseComponent<DATA> {
        this.cInfo = cInfo
        isLoadedData = false
        showSkeleton()
        startWipesAnim()
        initData()
        return this
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (canvas == null || state == State.ON_DESTROYED) return
        if (!isLoadedData && mAnimator?.isRunning == true && curAnimOffset > 0f) {
            val w = (((curWidth + shapeWidth) * curAnimOffset) - shapeWidth).roundToInt()
            if (curWipesRect == null) curWipesRect = Rect(w, 0, w + shapeWidth, curHeight) else curWipesRect?.set(w, 0, w + shapeWidth, curHeight)
            if (scaleAnimDrawable == null) try {
                scaleAnimDrawable = ContextCompat.getDrawable(context, R.drawable.component_scale_effict)
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            }
            curWipesRect?.let { rct ->
                scaleAnimDrawable?.bounds = rct
                scaleAnimDrawable?.draw(canvas)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0 || h == 0) return
        curWidth = w
        curHeight = h
        if (!isLoadedData && mAnimator?.isRunning == false) {
            startWipesAnim()
        }
    }

    /**
     * Wipes animation
     * */
    private var mAnimator: ValueAnimator? = null
    private var mPdx: PorterDuffXfermode? = null
    private var mRounderPaint: Paint? = null
    private var shapeWidth = 300
    private var curAnimOffset = 0.0f
    private var isLoadedData = false
    private var curWidth = 0
    private var curHeight = 0
    private var curExcept = 0
    private var curWipesRect: Rect? = null
    private var scaleAnimDrawable: Drawable? = null

    private fun initAnimPaint() {
        mRounderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRounderPaint?.isDither = true
        mRounderPaint?.style = Paint.Style.FILL
        mRounderPaint?.color = Color.WHITE
        mRounderPaint?.isFilterBitmap = true
        mPdx = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private fun initAnimator() {
        mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        mAnimator?.repeatCount = ValueAnimator.INFINITE
        mAnimator?.interpolator = AccelerateDecelerateInterpolator()
        mAnimator?.duration = 900
        mAnimator?.addListener(this)
        mAnimator?.addUpdateListener(this)
    }

    private fun startWipesAnim() {
        if (mAnimator == null) {
            initAnimator()
        } else if (mAnimator?.isRunning == true) {
            mAnimator?.cancel()
        }
        mAnimator?.start()
    }

    private fun cancelAnim() {
        curAnimOffset = 0.0f
        curExcept = 0
        curWipesRect = null
        scaleAnimDrawable = null
        mAnimator?.removeAllListeners()
        mAnimator?.cancel()
        mAnimator = null
    }

    override fun onAnimationRepeat(a: Animator?) {
        curExcept++
    }

    override fun onAnimationCancel(a: Animator?) {}

    override fun onAnimationStart(a: Animator?) {}

    override fun onAnimationEnd(a: Animator?) {}

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        if (curExcept % 2 == 0) return
        curAnimOffset = animation?.animatedFraction ?: 0.0f
        this@BaseComponent.postInvalidate()
    }
}