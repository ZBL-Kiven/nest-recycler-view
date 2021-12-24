package com.zj.viewtest.partition.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.util.BannerUtils;
import com.zj.viewtest.R;
import com.zj.viewtest.partition.services.beans.v.BannerConfigBean;

import java.util.List;

public class BaseBannerAdapter extends BannerAdapter<BannerConfigBean, BaseBannerAdapter.ImageHolder> {

    private int width = 0, height = 0;

    public BaseBannerAdapter(List<BannerConfigBean> mData, int width, int height) {
        super(mData);
        this.width = width;
        this.height = height;
    }

    @Override
    public void setDatas(List<BannerConfigBean> data) {
        super.setDatas(data);
        notifyDataSetChanged();
    }

    @Override
    public ImageHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = (ImageView) BannerUtils.getView(parent, R.layout.common_banner_image_item_layout);
        return new ImageHolder(imageView);
    }

    @Override
    public void onBindView(ImageHolder holder, BannerConfigBean data, int position, int size) {
        RequestBuilder<Drawable> rm = Glide.with(holder.itemView).load(data.getDefaultResId() != 0 ? data.getDefaultResId() : data.getBannerImg());
        if (width > 0 && height > 0) rm.override(width, height);
        rm.placeholder(R.drawable.ic_luck_time_game_replace_img).into(holder.imageView);
    }

    static class ImageHolder extends RecyclerView.ViewHolder {
        AppCompatImageView imageView;

        ImageHolder(@NonNull View view) {
            super(view);
            this.imageView = (AppCompatImageView) view;
        }
    }


}