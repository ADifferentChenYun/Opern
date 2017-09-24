package com.yun.opern.common;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.AppGlideModule;


/**
 * Created by Yun on 2017/9/24 0024.
 */
@GlideModule
public class GlideConfiguration extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }
}
