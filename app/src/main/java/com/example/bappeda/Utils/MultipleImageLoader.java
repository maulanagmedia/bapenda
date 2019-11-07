package com.example.bappeda.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MultipleImageLoader {
    private Context context;
    private List<String> listUrl;
    private int load_counter;

    private int width = 0;
    private int height = 0;

    private List<Bitmap> listLoaded = new ArrayList<>();
    private MultipleImageLoaderListener listener;

    public MultipleImageLoader(Context context, List<String> listUrl, MultipleImageLoaderListener listener){
        this.context = context;
        this.listUrl = listUrl;
        load_counter = 0;
        this.listener = listener;

        loadNextBitmap();
    }

    public MultipleImageLoader(Context context, List<String> listUrl, int width, int height, MultipleImageLoaderListener listener){
        this.context = context;
        this.listUrl = listUrl;
        load_counter = 0;
        this.listener = listener;

        this.width = width;
        this.height = height;

        loadNextBitmap();
    }

    private void loadNextBitmap(){
        if(load_counter < listUrl.size()){
            if(width == 0 && height == 0){
                ImageLoader.preload(context, listUrl.get(load_counter), new ImageLoader.ImageLoadListener() {
                    @Override
                    public void onLoaded(Bitmap image, float width, float height) {
                        listLoaded.add(image);
                        load_counter++;

                        loadNextBitmap();
                    }
                });
            }
            else{
                ImageLoader.preload(context, listUrl.get(load_counter), new ImageLoader.ImageLoadListener() {
                    @Override
                    public void onLoaded(Bitmap image, float width, float height) {
                        listLoaded.add(image);
                        load_counter++;

                        loadNextBitmap();
                    }
                }, width, height);
            }
        }
        else{
            listener.onAllLoaded(listLoaded);
        }
    }

    public interface MultipleImageLoaderListener{
        void onAllLoaded(List<Bitmap> lisLoaded);
    }
}