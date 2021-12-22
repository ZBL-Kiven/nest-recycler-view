package com.zj.viewtest.partition.services.api

import com.zj.viewtest.partition.services.beans.ComponentContentInfo
import com.zj.viewtest.partition.services.beans.ComponentInfo
import com.zj.viewtest.partition.services.beans.SortSeriesInfo
import com.zj.viewtest.partition.services.beans.TvSeriesInfo
import com.zj.viewtest.partition.services.beans.v.VideoSource
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface PartitionService {

    /**
     * Get skit information
     * */
    @GET("/video/source/read/series/short/info")
    fun getShortVideoInfo(@Query("seriesId") seriesId: Int): Observable<SortSeriesInfo>

    /**
     * Get episode information
     * */
    @GET("/video/source/read/series/tv/info")
    fun getTvVideoInfo(@Query("seriesId") seriesId: Int): Observable<TvSeriesInfo>

    /**
     * Get a playlist of short
     * */
    @GET("/video/source/read/series/short/content")
    fun getShortVideosBySeriesId(@Query("shortSeriesId") shortSeriesId: Int, @Query("lastId") lastId: Int?, @Query("startEpisode") startEpisode: Int?, @Query("endEpisode") endEpisode: Int?): Observable<List<VideoSource>>

    /**
     * Get episode playlist
     * */
    @GET("/video/source/read/series/tv/content")
    fun getTvVideosBySeriesId(@Query("tvSeriesId") tvSeriesId: Int, @Query("lastId") lastId: Int?, @Query("startEpisode") startEpisode: Int?, @Query("endEpisode") endEpisode: Int?): Observable<List<VideoSource>>

    /**
     * Get discover page content
     * */
    @GET("/video/source/read/component/schema/list")
    fun getDiscoverComponentInfo(): Observable<List<ComponentInfo?>?>


    /**
     * Get component content in bulk or individually
     * */
    @GET("/video/source/read/component/content/batch")
    fun getComponentsContent(@Query("componentIds") ids: IntArray): Observable<Map<String, String>>

    /**
     * Get component List content in individually
     * @Param sort 0 Recommend , 1 Play volume sort ,2 Newest sorting of all videos
     * */
    @GET("/video/source/read/component/content")
    fun getComponentContent(@Query("componentId") componentId: Int, @Query("lastId") lastId: Int, @Query("sort") sort: Int, @Query("page") page: Int, @Query("size") size: Int): Observable<List<ComponentContentInfo?>?>


    /**
     * Get component by channel id
     * */
    @GET("/video/source/read/component/channel/list")
    fun getComponentByChannelId(@Query("channelId") channelId: Int): Observable<List<ComponentInfo?>?>

}