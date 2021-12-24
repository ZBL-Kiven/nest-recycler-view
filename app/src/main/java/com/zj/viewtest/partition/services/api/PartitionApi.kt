package com.zj.viewtest.partition.services.api

import com.zj.api.base.BaseRetrofit
import com.zj.viewtest.partition.services.beans.ComponentContentInfo
import com.zj.viewtest.partition.services.beans.ComponentInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


object PartitionApi {

    fun getComponentContentData(componentId: Int, onResult: (isSuccess: Boolean, data: List<ComponentContentInfo>?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
//        val s = Observable.create<MutableList<ComponentContentInfo>> {
//            val d = mutableListOf<ComponentContentInfo>()
//            repeat(10) {
//                d.add(ComponentContentInfo().apply {
//                    dataType = componentId
//                })
//            }
//            it.onNext(d)
//        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
//            onResult(true, it, null)
//        }
        return null
    }

    fun getComponentListContentInfo(componentId: Int, lastId: Int, sort: Int, page: Int, size: Int, onResult: (isSuccess: Boolean, data: List<ComponentContentInfo?>?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
        val s = Observable.just {
            val d = mutableListOf<ComponentContentInfo>()
            repeat(9) {
                d.add(ComponentContentInfo())
            }
            d
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            onResult(true, it.invoke(), null)
        }
        return null
    }

    fun getComponentByChannelId(channelId: Int, onResult: (isSuccess: Boolean, data: List<ComponentInfo?>?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
        val dataMockList = mutableListOf<ComponentInfo>()
        dataMockList.add(ComponentInfo.createMock(0, 0, 0))
        dataMockList.add(ComponentInfo.createMock(1, 1, 1))
        dataMockList.add(ComponentInfo.createMock(1, 1, 1))
        dataMockList.add(ComponentInfo.createMock(1, 1, 1))
        dataMockList.add(ComponentInfo.createMock(4, 0, 2))
        onResult(true, dataMockList, null)
        return null
    }
}