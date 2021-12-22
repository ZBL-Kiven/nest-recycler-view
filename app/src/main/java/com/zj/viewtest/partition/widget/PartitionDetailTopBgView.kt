package com.zj.viewtest.partition.widget

import android.graphics.Rect
import android.view.ViewGroup
import android.graphics.Color
import android.content.Context
import android.graphics.Canvas
import com.zj.views.ut.DPUtils
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import android.graphics.drawable.GradientDrawable
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.appbar.AppBarLayout
import com.zj.nest.NestRecyclerView

class PartitionDetailTopBgView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : AppCompatImageView(context, attributeSet, def), com.zj.nest.NestRecyclerView.NestHeaderIn {

    private val totalScrollRange = DPUtils.dp2px(330f)
    private var expandListener: ((Boolean?, Float) -> Unit)? = null
    private var curScrolled: Int = 0
    var scrollAble = { true }

    override fun performClick(): Boolean {
        Log.e("-------", "======= CLICK TOP BAR =======")
        return super.performClick()
    }

    fun scrollToExpand() {
        (parent as? ViewGroup)?.scrollTo(0, 0)
        onScrolling(0, totalScrollRange, ViewCompat.TYPE_TOUCH)
    }

    fun scrollToFold() {
        (parent as? ViewGroup)?.scrollTo(0, totalScrollRange)
        onScrolling(totalScrollRange, totalScrollRange, ViewCompat.TYPE_TOUCH)
    }

    override fun getTotalScrollRange(): Int {
        return totalScrollRange
    }

    override fun getScrollFlags(): Int {
        return if (scrollAble.invoke()) super.getScrollFlags().or(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP) else -1
    }

    override fun onScrolling(cur: Int, total: Int, type: Int) {
        if (total in (curScrolled + 1)..cur) {
            expandListener?.invoke(true, 1.0f)
        }
        if (total in (cur + 1)..curScrolled) {
            expandListener?.invoke(false, 0.0f)
        }
        curScrolled = cur
        val offset = curScrolled * 1.0f / totalScrollRange
        alpha = 1 - offset
        expandListener?.invoke(null, offset)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (canvas == null) return
        if (width > 0 && height > 0) {
            val c = canvas.save()
            val d = GradientDrawable()
            d.colors = intArrayOf(Color.BLACK, 0)
            d.gradientType = GradientDrawable.LINEAR_GRADIENT
            d.orientation = GradientDrawable.Orientation.TOP_BOTTOM
            d.bounds = Rect(0, 0, width, height)
            d.draw(canvas)
            canvas.restoreToCount(c)
        }
    }
}