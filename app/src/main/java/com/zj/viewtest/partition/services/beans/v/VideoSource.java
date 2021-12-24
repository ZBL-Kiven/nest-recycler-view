package com.zj.viewtest.partition.services.beans.v;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings({"unused", "ConstantConditions"})
public class VideoSource {

    @Nullable
    private String id;

    @Nullable
    private String sourceId;

    @Nullable
    public String pullFrom = "";

    @Nullable
    public String videoTitle;

    @Nullable
    public String videoRemoteStorageUrl;

    @Nullable
    public String imgPreviewRemoteStorageUrl;

    @Nullable
    public String downloadVideoUrl;

    @Nullable
    public Double duration;

    public long bitRate;

    @Nullable
    public Integer width;

    @Nullable
    public Integer height;

    public long size;

    @Nullable
    public String createTime;

    @Nullable
    public String type;

    @Nullable
    public String classification;

    @Nullable
    public String userOpenId;

    public String authorId;

    @Nullable
    public String authorClapCode;

    @Nullable
    public String authorName;

    @Nullable
    public String authorAvatar;

    public long clapNum = 0L;

    public long favoriteNum = 0L;

    public long shareNum = 0L;

    public long commentCount = 0L;

    public long viewNum = 0L;

    public long fullViewNum = 0L;

    public boolean isClap;

    public boolean isFavorite;

    public long coinValue;

    public boolean isAddCoin;

    public boolean isMaxCoins;

    public String groupInfoId = "";

    public boolean isGroupDemo;

    public int challengeId;

    public int challengeStatus;

    public int rank;

    public int voteCount;

    public int fonGrade;

    public int auditClapNum = 0;

    public boolean newVideo;

    public int status;

    public int contentType = 1;

    public int lastId = 0;

    public int channelId = 0;

    public String channelName = "";

    @Nullable
    public SeriesVideoInfo seriesVideoInfo = new SeriesVideoInfo();


    public int source = 0;

    public boolean isYoutube() {
        return source == 1;
    }

    public boolean featureFlag = false;


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

    public static int VIDEO_CONTENT_TYPE_NORMAL = 0;
    public static int VIDEO_CONTENT_TYPE_SHORT = 2;
    public static int VIDEO_CONTENT_TYPE_TV = 1;
}

