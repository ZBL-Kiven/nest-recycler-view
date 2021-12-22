package com.zj.viewtest.partition.services.beans.v;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings({"unused", "ConstantConditions"})
public class VideoSource {
    /**
     * id 和 sourceId 都表示视频的 serverId 。
     * 其中可能存在任意一个没有的情况。
     */
    @Nullable
    private String id;

    @Nullable
    private String sourceId;

    /**
     * 视频来源接口，统计视频用户行为时用作标识视频页面来源
     */
    @Nullable
    public String pullFrom = "";

    /**
     * 视频标题
     */
    @Nullable
    public String videoTitle;

    /**
     * 视频远程仓库地址
     */
    @Nullable
    public String videoRemoteStorageUrl;

    /**
     * 预览图远程仓库地址
     */
    @Nullable
    public String imgPreviewRemoteStorageUrl;

    /**
     * 下载地址
     */
    @Nullable
    public String downloadVideoUrl;

    /**
     * 时长
     */
    @Nullable
    public Double duration;


    /**
     * bitRate
     **/
    public long bitRate;

    /**
     * width
     */
    @Nullable
    public Integer width;

    /**
     * height
     */
    @Nullable
    public Integer height;

    /**
     * size
     */
    public long size;

    /**
     * 创建日期
     */
    @Nullable
    public String createTime;

    /**
     * data 类型 ，video & img
     */
    @Nullable
    public String type;

    /**
     * Youtube视频分类
     */
    @Nullable
    public String classification;

    /* ***********************上传者信息***********************/

    /**
     * user open id
     */
    @Nullable
    public String userOpenId;

    /**
     * 作者 user id
     */
    public String authorId;

    /**
     * code 号
     **/
    @Nullable
    public String authorClapCode;

    /**
     * 名字
     */
    @Nullable
    public String authorName;

    /**
     * 头像
     */
    @Nullable
    public String authorAvatar;

    /* **********************************互动信息***************************************/

    /**
     * 点赞数量
     */
    public long clapNum = 0L;

    /**
     * 收藏数量
     */
    public long favoriteNum = 0L;

    /**
     * 转发数量
     */
    public long shareNum = 0L;


    /**
     * 评论数量
     */
    public long commentCount = 0L;

    /**
     * 播放数量
     */
    public long viewNum = 0L;

    /**
     * 完播数量
     */
    public long fullViewNum = 0L;

    /**
     * 是否点赞
     */
    public boolean isClap;

    /**
     * 是否收藏
     */
    public boolean isFavorite;


    /* **************************价值相关字段*********************************/
    /**
     * 获得金币总数
     */
    public long coinValue;

    /**
     * 本地属性，是否添加过金币
     */
    public boolean isAddCoin;

    /**
     * 是否达到上限制
     */
    public boolean isMaxCoins;

    /* ************************* 兴趣组 **********************************************/

    public String groupInfoId = "";

    /**
     * 是否 demo 视频
     */
    public boolean isGroupDemo;

    /**
     * challenge Id
     */
    public int challengeId;

    /**
     * 挑战赛状态  0：下限 1：上线 2：正在进行中 3：结算中 4：完成
     */
    public int challengeStatus;

    /**
     * challenge 排名
     */
    public int rank;

    /**
     * challenge 投票数量
     */
    public int voteCount;

    /* *************************视频动态**********************************************/

    /**
     * 1 = D ， 2= C ，3 = A ，4 = S ，5  = SS ，6 = SSS ，7 Featured+
     */
    public int fonGrade;

    //点赞总数
    public int auditClapNum = 0;

    //是否是new标签
    public boolean newVideo;

    /**
     * 视频状态： 1，3，4 显示 通过 {@link #fonGrade } 标签
     * -1 inReview
     * 0 集赞中 显示进度条，{@link #clapNum }  / {@link #auditClapNum }
     * 1 voting ,根据 {@link #fonGrade } 来显示 voting 等级
     * 2 拒绝，显示 Rejected 标签，和拒绝提示
     * 3 晋级成功
     * 4 落选，晋级失败
     * 5 Feature+
     * 6 Feature
     * 10 视频已删除
     */
    public int status;

    /* *********************************** 3.0 剧集 **************************************/
    //类型 0 普通视频  1 剧集（单集） 2 短剧（单集）
    public int contentType = 1;

    //用于加载分页列表的剧集 ID
    public int lastId = 0;

    //频道ID
    public int channelId = 0;

    //频道名
    public String channelName = "";

    //所属剧集相关信息
    @Nullable
    public SeriesVideoInfo seriesVideoInfo = new SeriesVideoInfo();

    /* ***************************Youtube Video Info***************************************/

    //0 : 自制视频 1: Youtube视频
    public int source = 0;

    public boolean isYoutube() {
        return source == 1;
    }

    //目前仅用于Youtube视频，有featureFlag标识后才能显示Youtube视频上传时作者添加的视频置顶评论
    public boolean featureFlag = false;


    /* ************************* Adapter In **********************************************/
    @NonNull
    public String getSourceId() {
        return TextUtils.isEmpty(sourceId) ? (TextUtils.isEmpty(id) ? "" : id) : sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = this.id = sourceId;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    /**
     * ------  constance --------
     */
    public static int VIDEO_CONTENT_TYPE_NORMAL = 0;
    public static int VIDEO_CONTENT_TYPE_SHORT = 2;
    public static int VIDEO_CONTENT_TYPE_TV = 1;
}

