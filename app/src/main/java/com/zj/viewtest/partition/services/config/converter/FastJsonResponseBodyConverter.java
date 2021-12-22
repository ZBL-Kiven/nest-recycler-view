package com.zj.viewtest.partition.services.config.converter;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];

    private final Type mType;

    private final ParserConfig config;
    private final int featureValues;
    private final Feature[] features;

    FastJsonResponseBodyConverter(Type type, ParserConfig config, int featureValues, Feature... features) {
        mType = type;
        this.config = config;
        this.featureValues = featureValues;
        this.features = features;
    }

    @Override
    public T convert(@Nullable ResponseBody value) {
        try {
            String body = "";
            try {
                body = value.string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(body)) return null;
            return JSON.parseObject(body, mType, config, featureValues, features != null ? features : EMPTY_SERIALIZER_FEATURES);
        } finally {
            value.close();
        }
    }
}
