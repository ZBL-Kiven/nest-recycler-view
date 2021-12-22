package com.zj.viewtest.partition.data

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zj.views.list.adapters.BaseAdapter
import com.zj.viewtest.partition.services.beans.BaseSeriesInfo
import com.zj.viewtest.partition.services.beans.ComponentConfigDataInfo
import com.zj.viewtest.partition.services.beans.ComponentContentInfo
import com.zj.viewtest.partition.services.beans.ComponentInfo
import com.zj.views.list.holders.BaseViewHolder
import com.zj.views.ut.DPUtils
import com.zj.viewtest.R
import com.zj.viewtest.partition.base.FillGridItemDecoration
import com.zj.viewtest.partition.component.BaseListVideoComponent
import com.zj.viewtest.partition.services.beans.v.VideoSource

class RvDispatchers : BaseAdapter<ComponentContentInfo?>(LayoutBuilder { _, viewType -> if (viewType == ComponentInfo.CONTENT_TYPE_ORDINARY) R.layout.item_video else R.layout.item_episodes }) {

    companion object {
        val DIVIDER_WIDTH = DPUtils.dp2px(10f)
    }

    private var contentType: Int = -1
    private var componentConfig: ComponentConfigDataInfo? = null
    private var direction: FillGridItemDecoration? = null


    override fun getItemViewType(position: Int): Int {
        return data[position]?.dataType ?: -1
    }

    fun update(contentType: Int, recyclerView: RecyclerView?, config: ComponentConfigDataInfo) {
        this.contentType = contentType
        this.componentConfig = config
        val context = recyclerView?.context ?: return
        if ((context as? Activity)?.isFinishing == true) return
        if (direction?.contentType != contentType) {
            direction?.let { d -> recyclerView.removeItemDecoration(d) }
            direction = when (contentType) {
                ComponentInfo.CONTENT_TYPE_ORDINARY -> {
                    recyclerView.layoutManager = GridLayoutManager(context, 2)
                    FillGridItemDecoration(contentType, 2, DIVIDER_WIDTH)
                }
                ComponentInfo.CONTENT_TYPE_DRAMA, ComponentInfo.CONTENT_TYPE_SHORT -> {
                    recyclerView.layoutManager = GridLayoutManager(context, 3)
                    FillGridItemDecoration(contentType, 3, DIVIDER_WIDTH)
                }
                else -> null
            }
            direction?.let { direction -> recyclerView.addItemDecoration(direction) }
        }
        recyclerView.adapter = this
        recyclerView.itemAnimator = null
    }

    fun createSkeletonData(count: Int = -1): MutableList<ComponentContentInfo> {
        val skeletonData = mutableListOf<ComponentContentInfo>()
        repeat(if (count > 0) count else if (contentType == ComponentInfo.CONTENT_TYPE_ORDINARY) 4 else 6) {
            skeletonData.add(ComponentContentInfo().createSkeleton(contentType))
        }
        return skeletonData
    }


    @Suppress("UNCHECKED_CAST")
    fun dispatchComponentVideoListData(data: MutableList<ComponentContentInfo?>?, isLoadMore: Boolean) {
        if (isLoadMore) add(data) else change(data)
    }

    private fun initVideoSourceData(h: BaseViewHolder<ComponentContentInfo?>?, inSkeletonShow: Boolean, d: VideoSource?) {
        if (d == null) return
        val thumb = h?.getView<ImageView>(R.id.item_video_thumb)
        val tvWatchNum = h?.getView<TextView>(R.id.item_video_watch_num)
        val clapNum = h?.getView<TextView>(R.id.item_video_clap_num)
        val tvDuration = h?.getView<TextView>(R.id.item_video_duration)
        val tvTitle = h?.getView<TextView>(R.id.item_video_title)
        val watchIcon = h?.getView<View>(R.id.item_video_watch_icon)
        val tvFrom = h?.getView<TextView>(R.id.item_video_from)
        val clapIcon = h?.getView<ImageView>(R.id.item_video_clap_icon)
        val mask = h?.getView<View>(R.id.item_video_mask)
        if (inSkeletonShow) {
            tvTitle?.text = null
            tvTitle?.setBackgroundResource(R.drawable.component_skeleton_bg_default)
            thumb?.setImageResource(R.drawable.component_skeleton_bg_default)
            tvWatchNum?.visibility = View.GONE
            tvDuration?.visibility = View.GONE
            clapNum?.visibility = View.GONE
            watchIcon?.visibility = View.GONE
            clapIcon?.visibility = View.GONE
            mask?.visibility = View.GONE
            tvFrom?.visibility = View.GONE
        } else {
            tvTitle?.background = null
            tvWatchNum?.visibility = View.VISIBLE
            tvDuration?.visibility = View.VISIBLE
            clapNum?.visibility = View.VISIBLE
            watchIcon?.visibility = View.VISIBLE
            clapIcon?.visibility = View.VISIBLE
            val isClap = BaseListVideoComponent.cachedClap[d.sourceId] ?: d.isClap
            d.isClap = isClap
            mask?.visibility = View.VISIBLE
            thumb?.let {
                val width = DPUtils.dp2px(164f)
                val height = DPUtils.dp2px(92.25f)
                Glide.with(it).load(d.imgPreviewRemoteStorageUrl).override(width, height).centerCrop().into(thumb)
            }
            tvTitle?.text = d.videoTitle
            d.duration?.let { tvDuration?.text = getDuration(it.toLong() * 1000L) }
            if (componentConfig?.isShowChannelName != true || d.channelName.isEmpty()) tvFrom?.visibility = View.GONE else {
                tvFrom?.visibility = View.VISIBLE
                tvFrom?.text = d.channelName
            }
        }
    }

    private fun initVideoSourceDataWithPayloads(h: BaseViewHolder<ComponentContentInfo?>?, inSkeletonShow: Boolean, d: VideoSource?, payloads: MutableList<Any>?) {
        if (h == null || d == null || inSkeletonShow || payloads.isNullOrEmpty()) return
    }

    private fun initEpisodeData(h: BaseViewHolder<ComponentContentInfo?>?, inSkeletonShow: Boolean, d: BaseSeriesInfo?) {
        if (d == null) return
        val thumb = h?.getView<ImageView>(R.id.item_episodes_thumb)
        val vFav = h?.getView<View>(R.id.item_episodes_fav_icon)
        val tvWatchNum = h?.getView<TextView>(R.id.item_episodes_watch_num)
        val tvTitle = h?.getView<TextView>(R.id.item_episodes_title)
        val tvDesc = h?.getView<TextView>(R.id.item_episodes_desc)
        val tvFrom = h?.getView<TextView>(R.id.item_episodes_from)
        val watchIcon = h?.getView<View>(R.id.item_episodes_watch_icon)
        val mask = h?.getView<View>(R.id.item_episodes_mask)
        if (inSkeletonShow) {
            tvTitle?.text = null
            tvDesc?.text = null
            tvTitle?.setBackgroundResource(R.drawable.component_skeleton_bg_default)
            tvDesc?.setBackgroundResource(R.drawable.component_skeleton_bg_default)
            thumb?.setImageResource(R.drawable.component_skeleton_bg_default)
            vFav?.visibility = View.GONE
            watchIcon?.visibility = View.GONE
            mask?.visibility = View.GONE
            tvWatchNum?.visibility = View.GONE
            tvFrom?.visibility = View.GONE
        } else {
            tvTitle?.background = null
            tvDesc?.background = null
            vFav?.visibility = View.VISIBLE
            mask?.visibility = View.VISIBLE
            watchIcon?.visibility = View.VISIBLE
            tvWatchNum?.visibility = View.VISIBLE
            thumb?.let {
                val width = DPUtils.dp2px(109f)
                val height = DPUtils.dp2px(148f)
                Glide.with(it).load(d.thumbnail).override(width, height).centerCrop().into(it)
            }
            tvTitle?.text = d.seriesName
            tvDesc?.let { it.text = it.context.getString(if (d.isFinalized()) R.string.Episode_finished else R.string.Episode_on_going, d.episodes) }
            if (componentConfig?.isShowChannelName != true || d.channelName.isEmpty()) tvFrom?.visibility = View.GONE else {
                tvFrom?.visibility = View.VISIBLE
                tvFrom?.text = d.channelName
            }
        }
    }

    private fun initEpisodeDataWithPayloads(h: BaseViewHolder<ComponentContentInfo?>?, inSkeletonShow: Boolean, d: BaseSeriesInfo?, payloads: MutableList<Any>?) {
        if (h == null || d == null || inSkeletonShow || payloads.isNullOrEmpty()) return
    }

    private fun getDuration(mediaDuration: Long): String {
        val duration = mediaDuration / 1000
        val minute = duration / 60
        val second = duration % 60
        return String.format("${if (minute < 10) "0%d" else "%d"}:${if (second < 10) "0%d" else "%d"}", minute, second)
    }

    override fun initData(holder: BaseViewHolder<ComponentContentInfo?>?, position: Int, d: ComponentContentInfo?, payloads: MutableList<Any>?) {
        if (d == null) return
        when (d.dataType) {
            ComponentInfo.CONTENT_TYPE_ORDINARY -> {
                if (payloads.isNullOrEmpty()) {
                    initVideoSourceData(holder, d.inSkeletonShow, d.videoInfo)
                } else {
                    initVideoSourceDataWithPayloads(holder, d.inSkeletonShow, d.videoInfo, payloads)
                }
            }
            ComponentInfo.CONTENT_TYPE_DRAMA, ComponentInfo.CONTENT_TYPE_SHORT -> {
                if (payloads.isNullOrEmpty()) {
                    initEpisodeData(holder, d.inSkeletonShow, d.seriesRespEn)
                } else {
                    initEpisodeDataWithPayloads(holder, d.inSkeletonShow, d.seriesRespEn, payloads)
                }
            }
        }
    }
}