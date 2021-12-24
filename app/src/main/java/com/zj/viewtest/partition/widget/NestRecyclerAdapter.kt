package com.zj.viewtest.partition.widget


import android.view.ViewGroup
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.holders.BaseViewHolder
import com.zj.viewtest.BuildConfig
import com.zj.viewtest.partition.base.BaseComponent
import com.zj.viewtest.partition.base.ComponentDelegate
import com.zj.viewtest.partition.base.ComponentDelegate.validType
import com.zj.viewtest.partition.services.beans.ComponentInfo
import com.zj.nest.NestRecyclerView
import java.lang.NullPointerException


class NestRecyclerAdapter(private val rv: NestRecyclerView?, viewBuilder: ViewBuilder) : BaseAdapter<ComponentInfo>(viewBuilder) {

    companion object {

        fun <T : NestRecyclerView> create(rv: T?): NestRecyclerAdapter {
            return NestRecyclerAdapter(rv, ViewBuilder { p, _, viewType ->
                return@ViewBuilder if (rv == null || !validType(viewType)) {
                    if (BuildConfig.DEBUG) throw NullPointerException("rv = $rv   && type  = $viewType  is illegal") else p
                } else {
                    ComponentDelegate.getComponentWithType(rv.context, viewType)
                }
            })
        }
    }

    override fun getItemViewType(position: Int): Int {
        return data[position]?.componentType ?: -1
    }

    override fun onViewRecycled(holder: BaseViewHolder<ComponentInfo>) {
        super.onViewRecycled(holder)
        (holder.itemView as? BaseComponent<*>)?.onDestroy(holder.absoluteAdapterPosition >= 0)
    }

    override fun initData(holder: BaseViewHolder<ComponentInfo>?, position: Int, module: ComponentInfo?, payloads: MutableList<Any>?) {
        rv?.let {
            (holder?.itemView as? BaseComponent<*>)?.let { c ->
                if (module != null) {
                    c.setData(module)
                    val isInfinityComponent = ComponentDelegate.isInfinityComponent(c.type)
                    c.layoutParams = if (!isInfinityComponent) {
                        ViewGroup.LayoutParams(rv.width, -2)
                    } else {
                        ViewGroup.LayoutParams(rv.width, rv.height)
                    }
                    c.hideEndingLine(position == data.indexOfLast { !isInfinityComponent })
                    if (ComponentDelegate.isInfinityComponent(c.type)) {
                        rv.withNestIn(c as? NestRecyclerView.NestScrollerIn)
                    }
                }
            }
        }
    }

    fun notifyClear() {
        repeat(rv?.childCount ?: 0) {
            (rv?.getChildAt(it) as? BaseComponent<*>)?.onDestroy()
        }
        clear()
    }
}