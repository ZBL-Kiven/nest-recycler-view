package com.zj.viewtest.partition.base

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FillGridItemDecoration(
    val contentType: Int,
    private val spanCount: Int,
    private val leftDividerWidth: Int,
    private val rightDividerWidth: Int = leftDividerWidth,
    private val topDividerWidth: Int = leftDividerWidth,
    private val bottomDividerWidth: Int = leftDividerWidth,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val pos = parent.getChildAdapterPosition(view)
        val column = (pos) % spanCount + 1
        outRect.top = if (pos < spanCount) 0 else topDividerWidth
        outRect.bottom = if (pos >= (parent.adapter?.itemCount ?: 0) - spanCount) 0 else bottomDividerWidth
        outRect.left = (column - 1) * leftDividerWidth / spanCount
        outRect.right = (spanCount - column) * rightDividerWidth / spanCount
    }
}