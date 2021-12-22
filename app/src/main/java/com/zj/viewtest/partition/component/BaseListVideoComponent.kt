package com.zj.viewtest.partition.component

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.zj.api.base.BaseRetrofit
import com.zj.views.list.listeners.ItemClickListener
import com.zj.viewtest.partition.services.api.PartitionApi
import com.zj.viewtest.partition.services.beans.ComponentConfigDataInfo
import com.zj.viewtest.partition.services.beans.ComponentContentInfo
import com.zj.viewtest.partition.base.BaseComponent
import com.zj.viewtest.partition.data.RvDispatchers
import com.zj.views.ut.DPUtils

abstract class BaseListVideoComponent(context: Context) : BaseComponent<List<ComponentContentInfo?>?>(context) {

    companion object {
        var DEf_PADDING = DPUtils.dp2px(18f)
        var cachedData: MutableMap<Int, List<ComponentContentInfo?>?> = mutableMapOf()
        var cachedClap = mutableMapOf<String, Boolean>()
    }

    abstract fun getBehaviorScrollingView(): RecyclerView?
    private var dispatchers: RvDispatchers? = null
    private var compo: BaseRetrofit.RequestCompo? = null

    open fun getSkeletonCount(contentType: Int): Int {
        return -1
    }

    @CallSuper
    override fun initView() {
        setPadding(DEf_PADDING, DEf_PADDING, DEf_PADDING, 0)
        dispatchers = RvDispatchers()
    }

    @CallSuper
    override fun initData() {
        compo?.cancel()
        getComponentData()
    }

    open fun getComponentData() {
        cInfo?.let {
            val info = cachedData[it.id]
            if (info.isNullOrEmpty()) {
                compo = PartitionApi.getComponentContentData(it.id) { isSuccess, data, _ ->
                    if (isSuccess && !data.isNullOrEmpty()) {
                        cachedData[it.id] = data
                        setComponentData(data)
                    }
                    compo = null
                }
            } else setComponentData(info)
        }
    }

    override fun showSkeleton() {
        cInfo?.let {
            dispatchers?.update(it.contentType, getBehaviorScrollingView(), ComponentConfigDataInfo.make(it.showChannelName))
            onSetData(dispatchers?.createSkeletonData(getSkeletonCount(it.contentType)), false)
        }
    }

    override fun onDestroy(soft: Boolean) {
        super.onDestroy(soft)
        if (!soft) {
            cInfo?.id?.let { cachedData.remove(it) }
            compo?.cancel()
            compo = null
        }
        dispatchers?.clear()
    }

    @Suppress("SameParameterValue")
    override fun onSetData(data: List<ComponentContentInfo?>?, pl: Any?) {
        dispatchers?.dispatchComponentVideoListData(data?.toMutableList(), (pl as? Boolean) ?: false)
        dispatchers?.setOnItemClickListener(object : ItemClickListener<ComponentContentInfo?>() {
            override fun onItemClick(position: Int, v: View?, m: ComponentContentInfo?) {
                Toast.makeText(v?.context, m?.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}