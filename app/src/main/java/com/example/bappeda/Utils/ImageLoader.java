package com.example.bappeda.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bappeda.R;

public class ImageLoader {
    private final static String TAG = "image_loader_log";
    public static void load(Context context, String url, ImageView view){
        Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade()).
                apply(new RequestOptions().placeholder(R.drawable.squaremerchant).diskCacheStrategy(DiskCacheStrategy.NONE)).into(view);
    }

    public static void loadPersonImage(Context context, String url, ImageView view){
        Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade()).
                apply(new RequestOptions().placeholder(R.drawable.squareprofile).diskCacheStrategy(DiskCacheStrategy.NONE)).into(view);
    }

    public static void load(Context context, int res_id, ImageView view){
        Glide.with(context).load(res_id).transition(DrawableTransitionOptions.withCrossFade()).
                apply(new RequestOptions().placeholder(new ColorDrawable(context.getResources().
                        getColor(R.color.light_grey))).diskCacheStrategy(DiskCacheStrategy.NONE)).into(view);
    }

    public static void loadProfileImage(Context context, Bitmap image, ImageView view){
        Glide.with(context).load(image).transition(DrawableTransitionOptions.withCrossFade()).
                apply(new RequestOptions().placeholder(R.drawable.squareprofile).diskCacheStrategy(DiskCacheStrategy.NONE)).into(view);
    }

    public static void load(Context context, String url, ImageView view, int width, int height){
        Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade()).
                apply(new RequestOptions().override(width, height).placeholder
                        (new ColorDrawable(context.getResources().getColor(R.color.light_grey))).
                        diskCacheStrategy(DiskCacheStrategy.NONE)).into(view);
    }

    public static void load(Context context, String url, ImageView view, ColorDrawable color){
        Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade()).
                apply(new RequestOptions().placeholder(color).
                        diskCacheStrategy(DiskCacheStrategy.NONE)).into(view);
    }

    public static void preload(Context context, String url, final ImageLoadListener listener){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().
                        placeholder(new ColorDrawable(context.getResources().getColor(R.color.light_grey))).
                                diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        listener.onLoaded(resource, resource.getWidth(), resource.getHeight());
                    }
                });
    }

    public static void preload(Context context, String url, final ImageLoadListener listener, int width, int height){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().
                        placeholder(new ColorDrawable(context.getResources().getColor(R.color.light_grey))).
                        override(width, height).
                                diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        listener.onLoaded(resource, resource.getWidth(), resource.getHeight());
                    }
                });
    }

    public interface ImageLoadListener{
        void onLoaded(Bitmap image, float width, float height);
    }
}
