package com.zj.viewtest.partition.services.api

import com.alibaba.fastjson.JSON
import com.zj.api.BaseApi
import com.zj.api.base.BaseRetrofit
import com.zj.api.interceptor.HeaderProvider
import com.zj.api.interceptor.UrlProvider
import com.zj.api.interfaces.ApiFactory
import com.zj.api.interfaces.ErrorHandler
import com.zj.viewtest.partition.services.beans.ComponentContentInfo
import com.zj.viewtest.partition.services.beans.ComponentInfo
import com.zj.viewtest.partition.services.beans.SortSeriesInfo
import com.zj.viewtest.partition.services.beans.TvSeriesInfo
import com.zj.viewtest.partition.services.beans.v.VideoSource
import com.zj.viewtest.partition.services.config.ApiErrorHandler
import com.zj.viewtest.partition.services.config.converter.FastJsonConverterFactory
import io.reactivex.Observable
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.http.Query
import java.util.*


object PartitionApi {

    private inline fun <reified T : Any, reified ERROR_HANDLER : ErrorHandler> getDefaultApi(baseUrl: UrlProvider, header: HeaderProvider, errorHandler: ERROR_HANDLER, timeOut: Long = 5000): BaseApi<T> {
        return BaseApi.create<T>(errorHandler).baseUrl(baseUrl).header(header).timeOut(timeOut).build(object : ApiFactory<T>() {
            override val debugAble: Boolean = true
            override val getJsonConverter: Converter.Factory?
                get() = FastJsonConverterFactory.create()
        })
    }

    private const val userId: Int = 119582

    private const val token: String = "sanhe12345"

    const val mUrl: String = "https://ugc.ccdev.lerjin.com"

    private val baseHeader = object : HeaderProvider {
        override fun headers(): Map<String, String> {
            val token: String = token
            val userId = userId
            if (token.isEmpty() || userId == 0) {
                throw NullPointerException("the header params [userId , token] was null or empty , returened by service creating!")
            }
            return hashMapOf<String, String>().apply {
                this["Content-Type"] = "application/json"
                this["token"] = token
                this["charset"] = "UTF-8"
                this["userId"] = "$userId"
                this["ostype"] = "android"
                this["lang"] = "zh"
                this["sysver"] = "11"
                this["apiver"] = "42"
                this["appver"] = "3.0.0"
                this["timezone"] = "8"
                this["uuid"] = UUID.randomUUID().toString()
            }
        }
    }


    private inline fun <reified T : Any> getUgcApi(): BaseApi<T> {
        return getDefaultApi(object : UrlProvider() {
            override fun url(): String {
                return mUrl
            }
        }, baseHeader, ApiErrorHandler)
    }

    /**
     * get short video episode info by seriesId
     * */
    fun getShortVideoInfo(seriesId: Int, onResult: (isSuccess: Boolean, data: SortSeriesInfo?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
        return getUgcApi<PartitionService>().call(observer = { it.getShortVideoInfo(seriesId) }, subscribe = onResult)
    }

    /**
     * get video episode info by seriesId
     * */
    fun getTvVideoInfo(seriesId: Int, onResult: (isSuccess: Boolean, data: TvSeriesInfo?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
        return getUgcApi<PartitionService>().call(observer = { it.getTvVideoInfo(seriesId) }, subscribe = onResult)
    }

    /**
     * get short video list by seriesId
     * */
    fun getShortVideosBySeriesId(shortSeriesId: Int, lastId: Int? = null, startEpisode: Int? = null, endEpisode: Int? = null, onResult: (isSuccess: Boolean, data: List<VideoSource>?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
        return getUgcApi<PartitionService>().call(observer = { it.getShortVideosBySeriesId(shortSeriesId, lastId, startEpisode, endEpisode) }, subscribe = onResult)
    }

    /**
     *  get video list by seriesId
     * */
    fun getTvVideosBySeriesId(tvSeriesId: Int, lastId: Int? = null, startEpisode: Int? = null, endEpisode: Int? = null, onResult: (isSuccess: Boolean, data: List<VideoSource>?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
        return getUgcApi<PartitionService>().call(observer = { it.getTvVideosBySeriesId(tvSeriesId, lastId, startEpisode, endEpisode) }, subscribe = onResult)
    }

    fun getComponentContentData(componentId: Int, onResult: (isSuccess: Boolean, data: List<ComponentContentInfo>?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
        return getUgcApi<PartitionService>().call(observer = { it.getComponentsContent(intArrayOf(componentId)) }) { isSuccess: Boolean, data: Map<String, String>?, t ->
            if (isSuccess && !data.isNullOrEmpty()) {
                data["$componentId"]?.let {
                    val d = JSON.parseArray(it, ComponentContentInfo::class.java)
                    onResult(isSuccess, d, t)
                }
            } else onResult(isSuccess, null, t)
        }
    }

    /**
     * ------------------------------- component configurations --------------------------------------
     * @since 3.0.0 new discover
     * getComponentInfo
     * */
    fun getDiscoverComponentInfo(onResult: (isSuccess: Boolean, data: List<ComponentInfo?>?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
        return getUgcApi<PartitionService>().call(observer = { it.getDiscoverComponentInfo() }, subscribe = onResult)
    }

    fun getComponentListContentInfo(componentId: Int, lastId: Int, sort: Int, page: Int, size: Int, onResult: (isSuccess: Boolean, data: List<ComponentContentInfo?>?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
        return getUgcApi<PartitionService>().call(observer = { it.getComponentContent(componentId, lastId, sort, page, size) }, subscribe = onResult)
    }

    fun getComponentByChannelId(channelId: Int, onResult: (isSuccess: Boolean, data: List<ComponentInfo?>?, throwable: HttpException?) -> Unit): BaseRetrofit.RequestCompo? {
        return getUgcApi<PartitionService>().call(observer = { it.getComponentByChannelId(channelId) }, subscribe = onResult)
    }
}