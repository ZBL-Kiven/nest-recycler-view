package com.zj.viewtest

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.zj.cf.managers.TabFragmentManager
import com.zj.viewtest.partition.channel.ChannelTabFragment
import com.zj.viewtest.partition.services.beans.ChannelInfo
import com.zj.viewtest.partition.widget.PartitionDetailTopBgView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_scroller)
        val vp2 = findViewById<ViewPager2>(R.id.main_pager)
        val tab = findViewById<TabLayout>(R.id.main_tab)
        val bgView = findViewById<PartitionDetailTopBgView>(R.id.main_appbar)
        val cs = arrayOf(ChannelInfo().apply { isMoreInflateData = true }, ChannelInfo(), ChannelInfo(), ChannelInfo(), ChannelInfo(), ChannelInfo(), ChannelInfo(), ChannelInfo(), ChannelInfo().apply { isMoreInflateData = true })

        bgView?.scrollAble = { true }

        object : TabFragmentManager<ChannelInfo, ChannelTabFragment>(this, vp2, 0, tab, *cs) {
            override fun tabConfigurationStrategy(tab: TabLayout.Tab, position: Int) {
                tab.text = "$position"
            }

            override fun syncSelectState(selectId: String) {
                val channel = getFragmentById(selectId)?.getData()
                if (channel?.isMoreInflateData == true) {
                    bgView?.scrollToFold()
                } else {
                    val frg = getFragmentById(selectId)
                    val nrv = frg?.getNestRecyclerView()
                    if (nrv?.canScrollVertically(-1) == false) bgView?.scrollToExpand() else bgView?.scrollToFold()
                }
            }

            override fun onCreateFragment(d: ChannelInfo, p: Int): ChannelTabFragment {
                val frg = ChannelTabFragment()
                frg.setData(d, bgView?.getTotalScrollRange() ?: 0)
                return frg
            }
        }

        bgView?.setOnClickListener {
            val i = Intent(this, MainActivity2::class.java)
            startActivity(i)
        }
    }
}