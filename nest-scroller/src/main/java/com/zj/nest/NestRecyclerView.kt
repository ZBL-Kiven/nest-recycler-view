package com.zj.nest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.zj.nest.NestRecyclerView.NestHeaderIn
import com.zj.nest.NestRecyclerView.NestScrollerIn
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Created by Zjj on 21.7.7
 *
 * Recyclable components used to support multiple nested sliding.
 * It must be set to [NestScrollerIn] through the [withNestIn] method to mark an inline recyclable list of unlimited length.
 * @see [getNestedChild]. Of course the inline view should be a [androidx.core.view.NestedScrollingChild].
 * The Wrapper of this view is not subject to any restrictions.
 *
 * NestRecyclerView supports linkage with other Views as Header, and AppBarLayout is supported by default.
 * You can to use [AppBarLayout] as the linkage head to make it more convenient to use the SystemUi compatible solution and the specified distance linkage solution specified by the system.
 * If you need to customize the linkage Header, please implement the [NestHeaderIn] protocol in any View and add it to any parents leaf node or root node.
 *
 * How to use:
 *
 * It just a RecyclerView, and then set other View in Child that need nested scrolling through [withNestIn].
 * To set the linkage header, directly include the corresponding header View in the XML or ViewTree
 * */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class NestRecyclerView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : RecyclerView(context, attributeSet, def) {

    var overScrollerDispatchToParent: Boolean = true
    private var mMinFlingVelocity = 0
    private var mMaxFlingVelocity = 0
    private var mTouchSlop = 0
    private var lastRawX = 0f
    private var lastRawY = 0f
    private var downX = 0f
    private var downY = 0f
    private var lastDy = 0f
    private var curOrientation = -1
    private var mCurrentFling = 0
    private var checkSnapPendingCode = 0x1122d
    private val parentScrollConsumed = IntArray(2)
    private var lastFocusMod = NestEvent.PASS
    private var lastMaskedActionView = -1
    private var inTouchMode = false
    private var hasScrollTag = false
    private var nestHeader: View? = null
    private var inNestedScrolling = false
    private var lastClickAvailable = false
    private var consumedSelf: Boolean = false
    private var nestRootParent: ViewGroup? = null
    private var interceptNextEvent: Boolean? = null
    private var nestScrollerIn: NestScrollerIn? = null
    private var needRefreshDownIndexByPointerChange = false
    private val overScroller = OverScroller(context, LinearInterpolator())
    private var overScrollerFilling = ViewScrollerTouchListener(::onFling)
    private var headerOffsetChangedListener: HeaderOffsetChangedListener? = null
    private var mHandler = Handler(Looper.getMainLooper()) {
        if (it.what == checkSnapPendingCode) checkPendingSnapEvent(lastDy)
        return@Handler false
    }

    @SuppressLint("ClickableViewAccessibility")
    /**
     * Used to proxy the header and embed the Touch event of the View
     * */
    private var delegateTouchListener = OnTouchListener { v, ev ->
        val ps = dispatchEvent(ev, v.hashCode())
        if (ps == NestEvent.CLICK) {
            v?.performClick()
            return@OnTouchListener true
        }
        return@OnTouchListener ps == NestEvent.CONSUMED
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onFinishInflate() {
        super.onFinishInflate()
        overScrollMode = OVER_SCROLL_NEVER
        descendantFocusability = FOCUS_BLOCK_DESCENDANTS
        post {
            checkNotCoordinatorParent()
            findDefaultOverScroller()
            nestHeader?.let {
                onPatchNestMeasureHeight()
                getScrollFlags()
                requestLayout()
            }
            repeat(childCount) {
                getChildAt(it).let { child ->
                    child.isFocusableInTouchMode = false
                    if (child is RecyclerView) {
                        child.descendantFocusability = FOCUS_BLOCK_DESCENDANTS
                    }
                }
            }
        }
    }

    /**
     * Open the internal multi-layer nested scrolling interface,
     * NestRecyclerView supports multi-level nested scrolling,
     * and the nesting protocol of its direct subclass should be specified at each level separately
     * */
    fun withNestIn(nestScrollerIn: NestScrollerIn?) {
        this.nestScrollerIn = nestScrollerIn
    }

    /**
     * This setting does not affect the linkage function of the header,
     * it only calls back after scrolling has occurred,
     * the maximum scrolling distance, see [NestHeaderIn.getTotalScrollRange]
     * */
    fun setOnHeaderOffsetChangedListener(l: HeaderOffsetChangedListener?) {
        this.headerOffsetChangedListener = l
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return super.canScrollVertically(direction) || getNestedChild()?.canScrollVertically(direction) == true
    }

    /**
     * The processing here will satisfy whether there is a sliding processing of the linkage head at the top.
     * And handle the ScrollFlag.SNAP event in [MotionEvent.ACTION_UP].
     * */
    final override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        fun updateForNewEvent(ps: NestEvent) {
            if (lastFocusMod != ps) {
                val ev1 = MotionEvent.obtainNoHistory(ev)
                ev1.action = MotionEvent.ACTION_CANCEL
                super.dispatchTouchEvent(ev1)
                val ev2 = MotionEvent.obtainNoHistory(ev)
                ev2.action = MotionEvent.ACTION_DOWN
                super.dispatchTouchEvent(ev2)
                ev1.recycle()
                ev2.recycle()
            }
        }

        val ps = dispatchEvent(ev ?: return super.dispatchTouchEvent(ev), hashCode())
        return try {
            if (ps != NestEvent.CONSUMED && unConsumedEvent(ps.outer, lastFocusMod != ps, ev)) {
                return true
            }
            when (ps) {
                NestEvent.FOCUS -> {
                    updateForNewEvent(ps)
                    return super.dispatchTouchEvent(ev)
                }
                NestEvent.CLICK -> {
                    updateForNewEvent(ps)
                    super.dispatchTouchEvent(ev)
                }
                NestEvent.CONSUMED, NestEvent.CONSUME_CANCEL -> true

                NestEvent.PASS -> {
                    if (lastClickAvailable) updateForNewEvent(ps)
                    super.dispatchTouchEvent(ev)
                }
            }
        } finally {
            lastFocusMod = ps
        }
    }

    /**
     * Different from [RecyclerView.dispatchNestedPreScroll], NestRecyclerView will give priority to the processing power of nested sliding,
     * until it can not complete the scroll in the same direction and pass it to the child View.
     * When processing scroll events after consumption via ParentView,
     * refer to the parameter comments [RecyclerView.dispatchNestedPreScroll] can be linked with other components
     * that implement [androidx.core.view.NestedScrollingChild3] [androidx.core.view.NestedScrollingChild3],
     * and its [getNestedChild] will receive scroll events after [canScrollVertically] false
     * */
    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        if (consumed != null) {
            consumed[0] += dx
            consumed[1] += dy
        }
        if (!canScrollVertically(1)) {
            onScrollOffsetWhenVerticalEnd(dx, dy)
        }
        return true
    }

    /**
     * Cancel the nested sliding Fling event of the system and hand it over to the internal Scroller for reprocessing
     * */
    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return true
    }

    /**
     * @return It is at least a Child with unlimited length or a reasonable length.
     * When its length and the length of the parent control cannot meet the minimum height of this view, nested sliding will not work.
     * It might be a View class that does not implement the [androidx.core.view.NestedScrollingChild3] protocol,
     * but they should to scrollable functions such as ScrollTo and ScrollBy, and meet touch events that support scroller by their self.
     * Commonly used Views such as RecyclerView, ListView, ViewPager, NestScrollView, ScrollView, etc. are recommended.
     * These target Views need`nt require any additional special processing.
     * and the target View is need`nt as a direct children of NestRecyclerView.
     * */
    open fun getNestedChild(): View? {
        return nestScrollerIn?.getInnerView()
    }

    /**
     * In order to meet the head linkage displacement situation,
     * set the offset of the ViewGroup containing the root node to ensure that after the view is scrolled,
     * the remaining views can completely fill the root node
     * */
    open fun onPatchNestMeasureHeight() {
        val nrp = nestRootParent
        val root = nrp?.parent as? View
        if (root != null) {
            if (nrp.bottom < root.bottom + getHeaderTotalHeight()) {
                val width = nrp.measuredWidth
                val lp = nrp.layoutParams ?: ViewGroup.LayoutParams(if (width == 0) ViewGroup.LayoutParams.MATCH_PARENT else width, 0)
                lp.height = root.bottom - nrp.top + getHeaderTotalHeight()
                nrp.layoutParams = lp
            }
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        (nestRootParent as? View)?.let { np ->
            val width = nestRootParent?.measuredWidth ?: 0
            val height = np.layoutParams.height - getParentHeightFromRoot()
            if (width > 0 && height > 0) setMeasuredDimension(width, height)
        }
    }

    /**
     * [blockIfOverScrollDispatch] Can block the nesting mechanism,
     * used for head linkage, or overwrite it to achieve other behaviors
     * */
    override fun computeScroll() {
        if (inTouchMode) return
        var invalidateNeed = false
        if (overScroller.computeScrollOffset()) {
            val current = overScroller.currY
            val dy = current - mCurrentFling
            scroll(dy, false)
            mCurrentFling = current
            invalidateNeed = true
        } else {
            if (overScroller.isFinished) {
                if (hasScrollTag) {
                    hasScrollTag = false
                    mHandler.removeMessages(checkSnapPendingCode)
                    mHandler.sendEmptyMessageDelayed(checkSnapPendingCode, 150)
                } else {
                    super.computeScroll()
                    return
                }
            }
        }
        super.computeScroll()
        if (invalidateNeed) invalidate()
    }

    open fun blockIfOverScrollDispatch(dy: Int, @ViewCompat.NestedScrollType type: Int): Boolean {
        val flags = getScrollFlags()
        if (flags == -1) {
            return false
        }
        run moveTop@{
            if (canScrollNestHeader(findDefaultOverScroller() ?: return@moveTop, dy)) {
                onHeaderScroll(dy, getScrolledOffset(), getHeaderTotalHeight(), type)
                return@blockIfOverScrollDispatch true
            }
        }
        return false
    }

    open fun isNestHeaderFold(): Boolean {
        return getScrolledOffset() >= getHeaderTotalHeight()
    }

    open fun isNestHeaderExpand(): Boolean {
        return getScrolledOffset() <= 0
    }

    open fun canScrollNestHeader(headView: View, dy: Int): Boolean {
        val x1 = getScrolledOffset()
        return if (dy >= 0) {
            x1 < getHeaderTotalHeight()
        } else {
            if (checkFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)) {
                val headerIsNotFold = x1 < headView.height
                val contentExpand = !super.canScrollVertically(-1) && getNestedChild()?.canScrollVertically(-1) != true
                return contentExpand && headerIsNotFold
            } else {
                x1 < headView.height
            }
        }
    }

    open fun getScrolledOffset(): Int {
        return nestHeader?.let {
            val r = Rect()
            it.getGlobalVisibleRect(r)
            if (r.isEmpty) return@getScrolledOffset 0
            val stableHeight = it.bottom - getHeaderTotalHeight()
            getHeaderTotalHeight() - (r.height() - stableHeight)
        } ?: 0
    }

    /**
     * you can set [HeaderOffsetChangedListener] by [setOnHeaderOffsetChangedListener]
     * Override it to customize the effective scrolling of the Header,
     * example, implementing some sliding difference effects such as Material Design.
     * */
    open fun onHeaderScroll(dy: Int, scrolled: Int, total: Int, type: Int) {
        var offset: Int = scrolled
        if (dy > 0 && scrolled + dy >= total) {
            nestRootParent?.scrollTo(0, total)
            offset = total
        } else if (dy < 0 && scrolled + dy <= 0) {
            nestRootParent?.scrollTo(0, 0)
            offset = 0
        } else {
            nestRootParent?.scrollBy(0, dy)
        }
        (nestHeader as? NestHeaderIn)?.onScrolling(offset, total, type)
        headerOffsetChangedListener?.onChanged(offset, total, type)
    }

    open fun getHeaderTotalHeight(): Int {
        nestHeader?.let { ns ->
            (ns as? AppBarLayout)?.let {
                return it.totalScrollRange + if (it.totalScrollRange == it.height) -1 else 0
            }
            (ns as? NestHeaderIn)?.let {
                return it.getTotalScrollRange() + if (it.getTotalScrollRange() == ns.height) -1 else 0
            }
        }
        return 0
    }

    open fun getOrientation(): Int {
        return when (val lm = layoutManager) {
            is LinearLayoutManager -> {
                lm.orientation
            }
            is GridLayoutManager -> {
                lm.orientation
            }
            is StaggeredGridLayoutManager -> {
                lm.orientation
            }
            else -> LinearLayoutManager.VERTICAL
        }
    }

    /**
     * Fling events will be considered to be consumed here,
     * and the events will be allocated one by one through [overScroller].
     * For details of allocation, see [computeScroll]
     * */
    private fun onFling(velocityX: Float, velocityY: Float) {
        mCurrentFling = 0
        when (getOrientation()) {
            HORIZONTAL -> overScroller.fling(0, 0, velocityX.roundToInt(), 0, Int.MIN_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MAX_VALUE)
            VERTICAL -> overScroller.fling(0, 0, 0, velocityY.roundToInt(), Int.MIN_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MAX_VALUE)
        }
        invalidate()
    }

    private fun getParentHeightFromRoot(view: View = this, total: Int = 0): Int {
        var t = total
        if (view == nestRootParent) return if (t <= 0) view.top else t else t += view.top
        val parent = (view.parent as? View) ?: return t
        return getParentHeightFromRoot(parent, t)
    }

    private fun isSameScrollOrientation(): Boolean {
        return getOrientation() == VERTICAL
    }

    /**
     * proxy the header view touch event , default is true.
     * */
    @SuppressLint("ClickableViewAccessibility")
    private fun updateHeader(headView: View) {
        headView.setOnTouchListener(delegateTouchListener)
    }

    /**
     * The Down event notified to all Views, but it doesn't care whether its children get subsequent events.
     * The sliding direction is calculated before the event is distributed, and the Event offset of the opposite direction is consumed,
     * so that it can only be triggered once in the same direction.
     * The same sliding direction as Self will be judged by @see[scroll] whether it is consumed,
     * otherwise it will not be intercepted.
     * The trigger method of [performClick] of View is the same as that of [View],
     * so you can set the click event normally when using it.
     * */
    private fun dispatchEvent(ev: MotionEvent?, actionViewMask: Int): NestEvent {
        if (ev == null) return NestEvent.PASS
        if ((ev.actionMasked == MotionEvent.ACTION_DOWN && lastMaskedActionView != actionViewMask) || ev.actionMasked == MotionEvent.ACTION_POINTER_DOWN || ev.actionMasked == MotionEvent.ACTION_POINTER_UP) {
            lastMaskedActionView = actionViewMask
            lastClickAvailable = false
            needRefreshDownIndexByPointerChange = true
        }
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            inTouchMode = true
            lastClickAvailable = true
            if (!overScroller.isFinished) {
                overScroller.abortAnimation()
                lastClickAvailable = false
            }
        }
        val v1 = MotionEvent.obtain(ev)
        v1.offsetLocation(-ev.x + ev.rawX, -ev.y + ev.rawY)
        try {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = v1.x;downY = v1.y;lastRawX = v1.x; lastRawY = v1.y; lastDy = 0f;interceptNextEvent = true
                    curOrientation = -1
                    overScrollerFilling.onEventDown(v1)
                    return NestEvent.CONSUMED
                }
                MotionEvent.ACTION_MOVE -> {
                    if (needRefreshDownIndexByPointerChange) {
                        v1.action = MotionEvent.ACTION_DOWN
                        needRefreshDownIndexByPointerChange = false
                        return dispatchEvent(v1, actionViewMask)
                    }
                    if (v1.pointerCount > 1) return NestEvent.CONSUMED
                    val sameOrientation = parseCurOrientation(lastRawX - v1.x, lastRawY - v1.y)
                    val dx: Float
                    val dy: Float
                    if (getOrientation() == VERTICAL) {
                        if (sameOrientation) {
                            dx = -v1.x;dy = 0f
                        } else {
                            dx = 0f;dy = -v1.y
                        }
                    } else {
                        if (sameOrientation) {
                            dx = 0f;dy = -v1.y
                        } else {
                            dx = -v1.x;dy = 0f
                        }
                    }
                    v1.offsetLocation(dx, dy)
                    if (!sameOrientation) {
                        return NestEvent.PASS
                    }
                    if (curOrientation != -1) {
                        lastClickAvailable = false
                        try {
                            lastDy = lastRawY - v1.y
                            val ldy = lastDy.roundToInt()
                            if (ldy != 0) {
                                overScrollerFilling.onEventMove(v1)
                                return scroll(ldy, true)
                            }
                        } finally {
                            lastRawX = v1.x;lastRawY = v1.y
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (curOrientation == -1 && max(abs(downX - lastRawX), abs(downY - lastRawY)) < mTouchSlop) {
                        overScrollerFilling.clear()
                        return if (lastClickAvailable) NestEvent.CLICK else NestEvent.CONSUMED
                    }
                    overScrollerFilling.onEventUp(v1)
                    if (overScroller.isFinished) {
                        mHandler.removeMessages(checkSnapPendingCode)
                        mHandler.sendEmptyMessageDelayed(checkSnapPendingCode, 150)
                    }
                    inTouchMode = false;lastRawY = 0f;lastRawX = 0f;lastDy = 0f; downX = 0f;downY = 0f;lastMaskedActionView = -1
                    return if (curOrientation == getOrientation()) NestEvent.CONSUME_CANCEL else NestEvent.PASS
                }
            }
        } finally {
            v1.recycle()
        }
        return NestEvent.CONSUMED
    }

    /**
     * Judge whether to consume this swipe here, see [NestEvent]
     * @param dy The swipe distance that can be consumed this time.
     * @param fromTouch Whether it comes from a touch event or Scroller.
     * */
    private fun scroll(dy: Int, fromTouch: Boolean): NestEvent {
        hasScrollTag = true
        var blockSelf = false
        if (overScrollerDispatchToParent) {
            blockSelf = blockIfOverScrollDispatch(dy, if (fromTouch) ViewCompat.TYPE_TOUCH else ViewCompat.TYPE_NON_TOUCH)
        }
        val target = getNestedChild()
        if (!blockSelf) {
            if (dy > 0) {
                return if (super.canScrollVertically(1)) {
                    scrollBy(0, dy)
                    NestEvent.CONSUMED
                } else {
                    if (target?.canScrollVertically(1) == true) {
                        target.scrollBy(0, dy)
                        NestEvent.CONSUMED
                    } else {
                        abortScroller()
                        NestEvent.FOCUS
                    }
                }
            }
            if (dy < 0) {
                return if (target?.canScrollVertically(-1) == true) {
                    target.scrollBy(0, dy)
                    NestEvent.CONSUMED
                } else {
                    if (super.canScrollVertically(-1)) {
                        scrollBy(0, dy)
                        NestEvent.CONSUMED
                    } else {
                        abortScroller()
                        NestEvent.FOCUS
                    }
                }
            }
        }
        return NestEvent.PASS
    }

    /**
     * In a single Touch event, the directional sliding distance > [ViewConfiguration.getScaledTouchSlop] is met for the first time
     * */
    private fun parseCurOrientation(stepX: Float, stepY: Float): Boolean {
        val ax = abs(stepX)
        val ay = abs(stepY)
        return if (curOrientation == -1 && ax < mTouchSlop && ay < mTouchSlop) {
            true
        } else if (curOrientation == -1) {
            val o = if (ax > ay) HORIZONTAL else VERTICAL
            curOrientation = o; o == getOrientation()
        } else {
            curOrientation == getOrientation()
        }
    }

    /**
     * Find any [AppBarLayout] or [NestHeaderIn] as its head to achieve the scrolling linkage,
     * @see [NestHeaderIn]
     * */
    private fun findDefaultOverScroller(anchor: View? = this.parent as? ViewGroup): View? {
        if (anchor == null) return null
        if (nestHeader == null) {
            (anchor as? ViewGroup)?.let { p ->
                repeat(p.childCount) {
                    val v = p.getChildAt(it)
                    if (v is AppBarLayout || v is NestHeaderIn) {
                        nestHeader = v
                    }
                }
                if (nestHeader == null) {
                    return findDefaultOverScroller(p.parent as? View)
                } else {
                    nestRootParent = p
                }
            }
            nestHeader?.let {
                updateHeader(it)
            }
        }
        return nestHeader
    }

    private fun checkNotCoordinatorParent(anchor: View? = this) {
        anchor?.parent?.let {
            return if (it is CoordinatorLayout) {
                throw IllegalArgumentException("NestRecyclerView cannot be a child of CoordinatorLayout.If you need to implement Martial-Design's Appbar function, you can add a AppBarLayout or some view extends [NestHeaderIn] under the same levels leaf layout or root layout.")
            } else checkNotCoordinatorParent(it as? View)
        }
    }

    /**
     * Get the ScrollFlags of the internal View of the head [AppBarLayout].
     * If the parent layout does not contain any [AppBarLayout],
     * this method will not complete the call.
     * @return ScrollFlags If the execution is complete, false -1
     * */
    private fun findAppbarLayoutTopViewScrollFlags(): Int {
        val appBarLayout = (findDefaultOverScroller() as? AppBarLayout) ?: return -1
        var firstFlags: Int = -1
        repeat(appBarLayout.childCount) { i ->
            (appBarLayout.getChildAt(i).layoutParams as? AppBarLayout.LayoutParams)?.let {
                if (it.scrollFlags.and(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) != 0) {
                    firstFlags = it.scrollFlags
                    return@repeat
                }
            }
        }
        return firstFlags
    }

    /**
     * @return scrollFlags or -1 ，
     * @see [findDefaultOverScroller]
     * */
    private fun getScrollFlags(): Int {
        if (!hasNestHeaders()) return -1
        return if (nestHeader is AppBarLayout) {
            findAppbarLayoutTopViewScrollFlags()
        } else {
            (nestHeader as? NestHeaderIn)?.getScrollFlags() ?: -1
        }
    }

    private fun checkFlags(flag: Int, flags: Int = getScrollFlags()): Boolean {
        if (flags <= 0) return false
        return flags.and(flag) == flag
    }

    /**
     * When ScrollFlags is contains [AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP], after stop touching or scrolling,
     * the [nestHeader] will automatically expand or collapse based on distance
     * */
    private fun checkPendingSnapEvent(dy: Float = 0f) {
        if (inTouchMode) return
        if (!isNestHeaderExpand() && !isNestHeaderFold()) {
            val offset = getScrolledOffset()
            if (offset == 0) return
            nestHeader?.let {
                val x2 = getHeaderTotalHeight() - offset
                abortScroller()
                if (!checkFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL)) return
                if (checkFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP)) {
                    val d = if (offset > x2) x2 else -offset
                    overScroller.startScroll(0, 0, 0, d, 500)
                    computeScroll()
                } else {
                    if (dy == 0f) return
                    overScroller.startScroll(0, 0, 0, if (dy < 0) -offset else offset, 500)
                    computeScroll()
                }
            }
        }
    }

    private fun abortScroller() {
        if (!overScroller.isFinished) {
            overScroller.abortAnimation()
        }
        mCurrentFling = 0
    }

    inner class ViewScrollerTouchListener(private val onFlingListener: (x: Float, y: Float) -> Unit) {

        private var mScrollState: Int = SCROLL_STATE_IDLE
        private val mVelocityTracker = VelocityTracker.obtain()
        private var inMoved = false

        init {
            val vc = ViewConfiguration.get(context)
            mTouchSlop = vc.scaledTouchSlop
            mMinFlingVelocity = vc.scaledMinimumFlingVelocity
            mMaxFlingVelocity = vc.scaledMaximumFlingVelocity
        }

        private fun addMovement(event: MotionEvent) {
            mVelocityTracker.addMovement(event)
        }

        fun onEventDown(event: MotionEvent) {
            inMoved = false
            addMovement(event)
        }

        fun onEventMove(event: MotionEvent) {
            inMoved = true
            addMovement(event)
        }

        fun onEventUp(event: MotionEvent) {
            if (!inMoved) {
                resetTouch()
                return
            }
            addMovement(event)
            mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity.toFloat())
            var yVelocity = -mVelocityTracker.yVelocity
            var xVelocity = -mVelocityTracker.xVelocity
            yVelocity = if (abs(yVelocity) < mMinFlingVelocity) 0f else (-mMaxFlingVelocity.toFloat()).coerceAtLeast(yVelocity.coerceAtMost(mMaxFlingVelocity.toFloat()))
            xVelocity = if (abs(xVelocity) < mMinFlingVelocity) 0f else (-mMaxFlingVelocity.toFloat()).coerceAtLeast(xVelocity.coerceAtMost(mMaxFlingVelocity.toFloat()))
            if (xVelocity != 0f || yVelocity != 0f) {
                onFlingListener(xVelocity, yVelocity)
            }
        }

        fun clear() {
            resetTouch()
            abortScroller()
        }

        private fun resetTouch() {
            mVelocityTracker?.clear()
        }
    }

    /**
     * Whether to detect the default header view, true default,
     * @see [NestHeaderIn]
     * */
    open fun hasNestHeaders(): Boolean {
        return true
    }

    /**
     * Override to processing after the nested scroll is completed, such as adding Footer to the NestRecyclerView or [NestScrollerIn]
     * @param [dx ，dy] The distance changed this time
     * */
    open fun onScrollOffsetWhenVerticalEnd(dx: Int, dy: Int) {}

    open fun unConsumedEvent(ne: NestUnConsumedEvent, firstChange: Boolean, event: MotionEvent?): Boolean {
        return false
    }

    /**
     * It can be implemented as an upper-level Header View that implements linked scrolling.
     * It can be implemented on any type of View, even a View that does not support nested sliding.
     * */
    interface NestHeaderIn {
        /**
         * the total scroll distance you need,
         *It must be established when [getScrollFlags] contains [AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL],
         * this property is used.
         * You can use the Top of some established View to hover some controls
         * */
        fun getTotalScrollRange(): Int

        /**
         * This callback is available when [getScrollFlags] points to a scrollable Flag
         * */
        fun onScrolling(cur: Int, total: Int, @ViewCompat.NestedScrollType type: Int)

        fun getScrollFlags(): Int = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
    }

    interface HeaderOffsetChangedListener {

        fun onChanged(cur: Int, total: Int, @ViewCompat.NestedScrollType type: Int)

    }

    /**
     * Used to specify and allow other ScrollingView to be added inside,
     * but there can only be one child control (mainly nested folding control) that continues the recycling mechanism inside.
     * */
    interface NestScrollerIn {
        fun getInnerView(): View?
    }

    private enum class NestEvent(var outer: NestUnConsumedEvent) {
        CONSUMED(NestUnConsumedEvent.PASS), CONSUME_CANCEL(NestUnConsumedEvent.PASS), CLICK(NestUnConsumedEvent.CLICK), FOCUS(NestUnConsumedEvent.FOCUS), PASS(NestUnConsumedEvent.PASS)
    }

    enum class NestUnConsumedEvent {
        CLICK, FOCUS, PASS
    }
}